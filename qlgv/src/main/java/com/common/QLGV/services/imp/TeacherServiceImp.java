package com.common.QLGV.services.imp;

import com.common.QLGV.entities.TeacherEntity;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.handle_exceptions.NotFoundExceptionHandle;
import com.common.QLGV.repositories.TeacherRepo;
import com.common.QLGV.services.TeacherService;
import com.common.QLGV.services.StudentServiceFromTeacher;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import com.model_shared.models.user.EntityModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.model_shared.models.user.UserDto;
import com.security_shared.services.SecurityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model_shared.enums.Gender;
import org.springframework.transaction.annotation.Isolation;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.transaction.annotation.Transactional;
import com.handle_exceptions.ServiceUnavailableExceptionHandle;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TeacherServiceImp implements TeacherService {
    @Autowired
    TeacherRepo teacherRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    LoggingService loggingService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SecurityService securityService;
    @Autowired
    StudentServiceFromTeacher studentServiceFromTeacher;

    private static final String TEACHERS_CACHE_KEY = "teachers:all";
    private static final String TEACHER_BY_USER_ID_CACHE_PREFIX = "teachers:user:";
    private static final String TEACHER_BY_CLASS_MANAGING_CACHE_PREFIX = "teachers:classManaging:";
    private static final long CACHE_TTL_MINUTES = 10; // TTL cho cache (10 phút)
    
    private LogContext getLogContext(String methodName){
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @Override
    public void clearCache() {
        LogContext logContext = getLogContext("clearCache");
        
        // Xóa cache chính
        redisTemplate.delete(TEACHERS_CACHE_KEY);
        
        // Xóa tất cả cache theo pattern
        Set<String> userKeys = redisTemplate.keys(TEACHER_BY_USER_ID_CACHE_PREFIX + "*");
        if (userKeys != null && !userKeys.isEmpty()) {
            redisTemplate.delete(userKeys);
        }
        
        Set<String> classKeys = redisTemplate.keys(TEACHER_BY_CLASS_MANAGING_CACHE_PREFIX + "*");
        if (classKeys != null && !classKeys.isEmpty()) {
            redisTemplate.delete(classKeys);
        }
        
        Set<String> pagedKeys = redisTemplate.keys("teachers:paged:*");
        if (pagedKeys != null && !pagedKeys.isEmpty()) {
            redisTemplate.delete(pagedKeys);
        }
        
        loggingService.logInfo("Cleared all teachers cache (all, user:*, classManaging:*, paged:*)", logContext);
    }

    @Override
    @CircuitBreaker(name = "qlgv-service", fallbackMethod = "getsFallback")
    public List<EntityModel> gets() {
        LogContext logContext = getLogContext("gets");

        Object cached = redisTemplate.opsForValue().get(TEACHERS_CACHE_KEY);
        if(cached != null){
            List<EntityModel> cachedTeachers = objectMapper.convertValue(
                cached, 
                new TypeReference<List<EntityModel>>() {}
            );
            loggingService.logInfo("Get Data from cache: " + TEACHERS_CACHE_KEY, logContext);
            return cachedTeachers;
        }
        loggingService.logWarn("Not Found Data from cache, query DB ", logContext);

        List<TeacherEntity> teacherEntities = teacherRepo.findAll();
        if(teacherEntities.isEmpty()){
            throw new NotFoundExceptionHandle("", Collections.emptyList(),"TeacherModel");
        }

        List<Integer> userIds = teacherEntities.stream()
                    .map(TeacherEntity::getUserId)
                    .distinct()
                    .toList();
        Map<Integer, UserDto> userIdFound = securityService.getUsersByIds(userIds);
        
        List<EntityModel> teacherModels = new ArrayList<>();
        for(TeacherEntity teacherEntity : teacherEntities){
            UserDto userDto = userIdFound.get(teacherEntity.getUserId());
            
            if(userDto != null){
                if(userDto.getProfileData() == null){
                    userDto.setProfileData(new HashMap<>());
                }
                userDto.getProfileData().put("classManaging", teacherEntity.getClassManaging());
                userDto.getProfileData().put("department", teacherEntity.getDepartment());
                
                EntityModel teacherModel = new EntityModel();
                teacherModel.setId(teacherEntity.getId());
                teacherModel.setUser(userDto);
                
                teacherModels.add(teacherModel);
                loggingService.logTeacherOperation("GET", teacherModel.getId(), logContext);
            }
        }
        redisTemplate.opsForValue().set(TEACHERS_CACHE_KEY, teacherModels, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        loggingService.logInfo("save cached to redis: " + TEACHERS_CACHE_KEY, logContext);

        loggingService.logInfo("Get Teachers Successfully", logContext);
        return teacherModels;
    }

    @Override
    @CircuitBreaker(name = "qlgv-service", fallbackMethod = "getByUserIdFallback")
    public EntityModel getByUserId(Integer userId) {
        LogContext logContext = getLogContext("getByUserId");


        String cacheKey = TEACHER_BY_USER_ID_CACHE_PREFIX + userId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if(cached != null){
            EntityModel cachedTeacher = objectMapper.convertValue(
                cached,
                 EntityModel.class
            );
            loggingService.logInfo("Get teacher by userId from cache: " + cacheKey, logContext);
            return cachedTeacher;
        }
        loggingService.logWarn("Not found cache for userId: " + userId + ", query DB", logContext);

        TeacherEntity teacherEntity = teacherRepo.findByUserId(userId).orElseThrow(
            () -> new NotFoundExceptionHandle("", List.of(String.valueOf(userId)), "TeacherModel")
        );
        
        UserDto userDto = securityService.getUsersByIds(List.of(userId)).get(userId);
        
        if (userDto != null) {
            if (userDto.getProfileData() == null) {
                userDto.setProfileData(new HashMap<>());
            }
            // Put các field từ TeacherEntity vào profileData nếu cần
            userDto.getProfileData().put("classManaging", teacherEntity.getClassManaging());
            userDto.getProfileData().put("department", teacherEntity.getDepartment());
        }
        
        EntityModel teacherModel = new EntityModel();
        teacherModel.setId(teacherEntity.getId());
        teacherModel.setUser(userDto);

        redisTemplate.opsForValue().set(cacheKey, teacherModel, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        loggingService.logInfo("Save cache to Redis : " + cacheKey, logContext);

        loggingService.logInfo("Get teacher by userId: " + userId + " Successfully", logContext);
        return teacherModel;
    }

    @Override
    @CircuitBreaker(name = "qlgv-service", fallbackMethod = "getStudentsByClassManagingFallback")
    public List<EntityModel> getStudentsByClassManaging(String classManaging, String authToken) {
        LogContext logContext = getLogContext("getStudentsByClassManaging");
        
        if (classManaging == null || classManaging.trim().isEmpty()) {
            loggingService.logWarn("classManaging is null or empty", logContext);
            return Collections.emptyList();
        }
        
        String normalizedClass = classManaging.trim();
    
        String cacheKey = TEACHER_BY_CLASS_MANAGING_CACHE_PREFIX + normalizedClass;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            List<EntityModel> cachedTeachers = objectMapper.convertValue(
                cached,
                new TypeReference<List<EntityModel>>() {}
            );
            loggingService.logInfo("Get students by classManaging : " + classManaging 
                + " from cache: " + cacheKey,
                logContext
            );
            return cachedTeachers;
        }
        loggingService.logWarn("Not found cache for classManaging: " + classManaging + "query DB", logContext);
        
        // Gọi StudentService để lấy students theo schoolClass từ QLSV module
        loggingService.logInfo("Getting students for classManaging: " + classManaging, logContext);
        List<EntityModel> students = studentServiceFromTeacher.getStudentsBySchoolClass(classManaging, authToken);
        loggingService.logInfo("Found " + students.size() + " students for classManaging: " + classManaging, logContext);
        
        // Cache result với TTL (chỉ cache nếu có students, không cache empty array)
        if (!students.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, students, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            loggingService.logInfo("Save cache to Redis : " + cacheKey, logContext);
        } else {
            loggingService.logWarn("Not found students with classManaging: " + classManaging + " from teacher", logContext);
        }

        loggingService.logInfo("Get students by classManaging: " + classManaging + " From teacher Successfully", logContext);
        return students;
    }

    @Override
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    @CircuitBreaker(name = "qlgv-service", fallbackMethod = "createByUserIdFallback")
    public void createByUserId(UserDto user) {
        LogContext logContext = getLogContext("createByUserId");

        TeacherEntity existingTeacher = teacherRepo.findByUserId(user.getUserId()).orElse(null);

        if(existingTeacher != null){
            loggingService.logWarn("Teacher profile already exists for userId: " + user.getUserId() +
                    ". This may be orphaned from previous failed registration. Deleting and recreating...", logContext);

            teacherRepo.delete(existingTeacher);
            teacherRepo.flush();

            loggingService.logInfo("Deleted orphaned teacher for userId: " + user.getUserId(), logContext);
        }

        TeacherEntity newTeacher = new TeacherEntity();
        newTeacher.setUserId(user.getUserId());
        
        if(user.getProfileData() != null && !user.getProfileData().isEmpty()){
            List<String> nullFields = new ArrayList<>();

            Object classManagingOjb = user.getProfileData().get("classManaging");
            if(classManagingOjb != null){
                newTeacher.setClassManaging(classManagingOjb.toString());
            }else{
                nullFields.add("classManaging");
            }

            Object departmentObj = user.getProfileData().get("department");
            if(departmentObj != null){
                newTeacher.setDepartment(departmentObj.toString());
            }else{
                nullFields.add("department");
            }

            if(!nullFields.isEmpty()){
                loggingService.logWarn("Null fields detected in create teacher: " + nullFields, logContext);
            }
        }

        loggingService.logInfo("Created teacher profile for userId: " + user.getUserId(), logContext);
        teacherRepo.save(newTeacher);

        redisTemplate.delete(TEACHERS_CACHE_KEY);
        loggingService.logInfo("Del cache key: " + TEACHERS_CACHE_KEY
                + " after create teacher", logContext);
    }

    @Override
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    @CircuitBreaker(name = "qlgv-service", fallbackMethod = "updateFallback")
    public EntityModel update(UpdateEntityModel req) {
        LogContext logContext = getLogContext("update");
        if (req.getUser() == null || req.getUser().getUserId() == null) {
            throw new NotFoundExceptionHandle("", Collections.emptyList(),"TeacherModel");
        }

        TeacherEntity teacherEntity = teacherRepo.findById(req.getId()).orElseThrow(
                () -> new NotFoundExceptionHandle("", List.of(String.valueOf(req.getId()))
                        ,"TeacherModel")
        );

        if(!req.getUser().getUserId().equals(teacherEntity.getUserId())){
            throw new NotFoundExceptionHandle("", Collections.emptyList(),"TeacherModel");
        }

        if (req.getUser().getProfileData() != null && !req.getUser().getProfileData().isEmpty()) {
            List<String> nullFields = new ArrayList<>();
            Object classManagingObj = req.getUser().getProfileData().get("classManaging");
            if(classManagingObj != null){
                teacherEntity.setClassManaging(classManagingObj.toString());
            }else{
                nullFields.add("classManaging");
            }

            Object departmentObj = req.getUser().getProfileData().get("department");
            if(departmentObj != null){
                teacherEntity.setDepartment(departmentObj.toString());
            }else{
                nullFields.add("department");
            }

            if(!nullFields.isEmpty()){
                loggingService.logWarn("Null fields detected in update teacher: " + nullFields, logContext);
                throw new NotFoundExceptionHandle("", nullFields, "TeacherModel");
            }

            teacherRepo.save(teacherEntity);
            loggingService.logTeacherOperation("UPDATE", teacherEntity.getId(), logContext);
            loggingService.logInfo("Update teacher Successfully", logContext);
        }

        UserDto user = securityService.updateUser(req.getUser());
        loggingService.logInfo("Updated user data for userId: " + user.getUserId(), logContext);

        if(user.getProfileData() == null){
            user.setProfileData(new HashMap<>());
        }
        user.getProfileData().put("classManaging", teacherEntity.getClassManaging());
        user.getProfileData().put("department", teacherEntity.getDepartment());

        EntityModel teacherModel = new EntityModel();
        teacherModel.setId(req.getId());
        teacherModel.setUser(user);

        // Invalidate tất cả cache liên quan
        redisTemplate.delete(TEACHERS_CACHE_KEY);
        redisTemplate.delete(TEACHER_BY_USER_ID_CACHE_PREFIX + req.getUser().getUserId());
        if (teacherEntity.getClassManaging() != null) {
            redisTemplate.delete(TEACHER_BY_CLASS_MANAGING_CACHE_PREFIX + teacherEntity.getClassManaging());
        }
        // Xóa cache paged (dùng pattern matching)
        Set<String> pagedKeys = redisTemplate.keys("teachers:paged:*");
        if (pagedKeys != null && !pagedKeys.isEmpty()) {
            redisTemplate.delete(pagedKeys);
        }
        loggingService.logInfo("Invalidated all teacher caches after update userId: " + req.getUser().getUserId(), logContext);

        return teacherModel;
    }

    @Override
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    @CircuitBreaker(name = "qlgv-service", fallbackMethod = "deletesFallback")
    public Boolean deletes(List<Integer> userIds) {
        LogContext logContext = getLogContext("delete");

        Set<Integer> uniqueUserIds = new LinkedHashSet<>();
        List<Integer> duplicates = new ArrayList<>();
        for (Integer userId : userIds) {
            if (!uniqueUserIds.add(userId)) {
                duplicates.add(userId);
            }
        }

        if (!duplicates.isEmpty()) {
            loggingService.logWarn("Duplicate userIds detected in delete request: " + duplicates
                    + ". They will be processed only once.", logContext);
        }

        List<Integer> uniqueUserIdsList = new ArrayList<>(uniqueUserIds);

        List<String> listIDNotFound = new ArrayList<>();
        List<TeacherEntity> studentEntitiesToDelete = new ArrayList<>();

        for(Integer userId : uniqueUserIdsList) {
            TeacherEntity teacherEntity = teacherRepo.findByUserId(userId)
                    .orElse(null);
            if (teacherEntity != null) {
                studentEntitiesToDelete.add(teacherEntity);
            } else {
                listIDNotFound.add(String.valueOf(userId));
            }
        }

        if (!listIDNotFound.isEmpty()) {
            loggingService.logError("Found IDs not exist: " + listIDNotFound, null, logContext);
            throw new NotFoundExceptionHandle("", listIDNotFound, "StudentModel");
        }

        teacherRepo.deleteAll(studentEntitiesToDelete);
        loggingService.logInfo("Deleted teachers with userIds: " + uniqueUserIdsList, logContext);

        List<Integer> deletedUserIds = securityService.deleteUsers(uniqueUserIdsList);
        loggingService.logInfo("Deleted users with ids: " + deletedUserIds, logContext);

        redisTemplate.delete(TEACHERS_CACHE_KEY);
        loggingService.logInfo("Del cache key: " + TEACHERS_CACHE_KEY + " after del teachers", logContext);
        return true;
    }

    // paged method
    @Override
    @CircuitBreaker(name = "qlgv-service", fallbackMethod = "getsPagedFallback")
    public PagedResponseModel<EntityModel> getsPaged(PagedRequestModel pagedRequest) {
        LogContext logContext = getLogContext("getsPaged");
        
        String cacheKey = String.format("teachers:paged:page=%d:size=%d:sort=%s:%s", 
            pagedRequest.getPage(), 
            pagedRequest.getSize(), 
            pagedRequest.getSortBy(), 
            pagedRequest.getSortDirection());
            
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if(cached != null){
            PagedResponseModel<EntityModel> cachedPaged = objectMapper.convertValue(
                cached,
                new TypeReference<PagedResponseModel<EntityModel>>() {}
            );
            loggingService.logInfo("Get paged data from Redis cache : " + cacheKey, logContext);
            return cachedPaged;
        }

        loggingService.logWarn("Not found paged cache, query DB", logContext);

        Sort sort = Sort.by(
            "asc".equalsIgnoreCase(pagedRequest.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC
            ,pagedRequest.getSortBy()
        );

        Pageable pageable = PageRequest.of(
            pagedRequest.getPage(),
            pagedRequest.getSize(),
            sort
        );

        Page<TeacherEntity> teacherPage = teacherRepo.findAll(pageable);
        if(teacherPage.isEmpty()){
            loggingService.logWarn("Not found teachers in database", logContext);
            throw new NotFoundExceptionHandle(
                "", 
                List.of(String.valueOf(pageable.getPageNumber())), 
                "TeacherModel");
        }
        // tạo list userIds để gửi qua security service
        List<Integer> userIds = teacherPage.getContent().stream()
                .map(TeacherEntity::getUserId)
                .distinct()
                .toList();
        Map<Integer, UserDto> usersById = securityService.getUsersByIds(userIds);

        List<EntityModel> teacherModels = new ArrayList<>();
        for(TeacherEntity teacherEntity : teacherPage.getContent()){
            UserDto userDto = usersById.get(teacherEntity.getUserId());
            // Bỏ filter status để ADMIN có thể quản lý tất cả users (kể cả DISABLED)
            if(userDto != null){
                if(userDto.getProfileData() == null){
                    userDto.setProfileData(new HashMap<>());
                }
                userDto.getProfileData().put("classManaging", teacherEntity.getClassManaging());
                userDto.getProfileData().put("department", teacherEntity.getDepartment());
                
                EntityModel teacherModel = new EntityModel();
                teacherModel.setId(teacherEntity.getId());
                teacherModel.setUser(userDto);
            
                teacherModels.add(teacherModel);
                loggingService.logTeacherOperation("GET_PAGED", teacherEntity.getId(), logContext);
            }
        }

        PagedResponseModel<EntityModel> pagedResponse = new PagedResponseModel<>(
            teacherModels,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            teacherPage.getTotalElements()
        );

        redisTemplate.opsForValue().set(cacheKey, pagedResponse, 5, TimeUnit.MINUTES);
        loggingService.logInfo("Save paged cache to Redis : " + cacheKey, logContext);

        loggingService.logInfo("Gets Paged Teachers Successfully", logContext);
        return pagedResponse;
    }

    // Filter
    @Override
    @CircuitBreaker(name = "qlgv-service", fallbackMethod = "filterFallback")
    public List<EntityModel> filter(Integer id, String firstName, String lastName, 
                                    Integer age, Gender gender, String email, 
                                    String phoneNumber, String classManaging, String department
    ) {
        LogContext logContext = getLogContext("filter");

        String cacheKey = String.format(
            "teachers:filter:id=%d:firstName=%s:lastName=%s:age=%d:gender=%s:email=%s:phoneNumber=%s:classManaging=%s:department=%s", 
            id, firstName, lastName, age, gender, email, phoneNumber, classManaging, department
        );
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            List<EntityModel> cachedFilteredData = objectMapper.convertValue(
                cached, 
                new TypeReference<List<EntityModel>>() {}
            );
            loggingService.logInfo("Get filtered data from Redis cache : " + cacheKey, logContext);
            return cachedFilteredData;
        }

        int conditionCount = 0;
        boolean hasIdCondition = (id != null && id > 0);
        boolean hasFirstNameCondition = (firstName != null && !firstName.trim().isEmpty());
        boolean hasLastNameCondition = (lastName != null && !lastName.trim().isEmpty());
        boolean hasAgeCondition = (age != null && age > 0);
        boolean hasGenderCondition = gender != null;
        boolean hasEmailCondition = email != null && !email.trim().isEmpty();
        boolean hasPhoneNumberCondition = phoneNumber != null && !phoneNumber.trim().isEmpty();
        boolean hasClassManagingCondition = classManaging != null && !classManaging.trim().isEmpty();
        boolean hasDepartmentCondition = department != null && !department.trim().isEmpty();

        if (hasIdCondition) conditionCount++;
        if (hasFirstNameCondition) conditionCount++;
        if (hasLastNameCondition) conditionCount++;
        if (hasAgeCondition) conditionCount++;
        if (hasGenderCondition) conditionCount++;
        if (hasEmailCondition) conditionCount++;
        if (hasPhoneNumberCondition) conditionCount++;
        if (hasClassManagingCondition) conditionCount++;
        if (hasDepartmentCondition) conditionCount++;

        if (conditionCount == 0) {
            loggingService.logWarn("No conditions provided for filtering", logContext);
            return gets();
        }

        loggingService.logInfo("Filtering with " + conditionCount + " conditions", logContext);

        List<EntityModel> teacherModels = new ArrayList<>();
        List<TeacherEntity> teacherEntities = teacherRepo.findAll();

        if (teacherEntities.isEmpty()) {
            loggingService.logWarn("No teachers found in database with the given filters", logContext);
            throw new NotFoundExceptionHandle("", Collections.emptyList(), "TeacherModel");
        }

        Map<Integer, UserDto> usersById = securityService.getUsersByIds(teacherEntities.stream()
                .map(TeacherEntity::getUserId)
                .distinct()
                .toList()
            );

        for(TeacherEntity teacherEntity : teacherEntities){
            int matchedConditions = 0;

            UserDto userDto = usersById.get(teacherEntity.getUserId());
            
            if(userDto != null){
                if (hasIdCondition && teacherEntity.getId() == id) {
                    matchedConditions++;
                }
                if (hasFirstNameCondition && userDto.getFirstName() != null &&
                    userDto.getFirstName().toLowerCase().contains(firstName.toLowerCase())
                )
                {
                    matchedConditions++;
                }
                if (hasLastNameCondition && userDto.getLastName() != null &&
                    userDto.getLastName().toLowerCase().contains(lastName.toLowerCase())
                )
                {
                    matchedConditions++;
                }
                if (hasAgeCondition && userDto.getAge() == age) {
                    matchedConditions++;
                }
                if (hasGenderCondition && userDto.getGender() == gender) {
                    matchedConditions++;
                }
                if (hasEmailCondition && userDto.getEmail() == email)
                {
                    matchedConditions++;
                }
                if (hasPhoneNumberCondition && userDto.getPhoneNumber() == phoneNumber)
                {
                    matchedConditions++;
                }
                if (hasClassManagingCondition && teacherEntity.getClassManaging() != null &&
                    teacherEntity.getClassManaging().toLowerCase().contains(classManaging.toLowerCase())
                )
                {
                    matchedConditions++;
                }
                if (hasDepartmentCondition && teacherEntity.getDepartment() != null &&
                    teacherEntity.getDepartment().toLowerCase().contains(department.toLowerCase())
                )
                {
                    matchedConditions++;
                }
                if (matchedConditions == conditionCount) {
                    // Set profileData vào userDto giống như getsPaged
                    if (userDto.getProfileData() == null) {
                        userDto.setProfileData(new HashMap<>());
                    }
                    userDto.getProfileData().put("department", teacherEntity.getDepartment());
                    userDto.getProfileData().put("classManaging", teacherEntity.getClassManaging());
                    
                    // Tạo EntityModel với user info đầy đủ
                    EntityModel teacherModel = new EntityModel();
                    teacherModel.setId(teacherEntity.getId());
                    teacherModel.setUser(userDto);
                    
                    teacherModels.add(teacherModel);
                }
            }
        }
        // Cache kết quả filter (cache trong 5 phút)
        redisTemplate.opsForValue().set(cacheKey, teacherModels, 5, TimeUnit.MINUTES);
        loggingService.logInfo("Save filtered cache to Redis : " + cacheKey, logContext);

        loggingService.logInfo("Filter Teachers Successfully. Found " + teacherModels.size() + " teachers with the given filters", logContext);
        return teacherModels;     
    }

    // fallback method
    public List<EntityModel> getsFallback(Exception e) {
        LogContext logContext = getLogContext("getsFallback");
        loggingService.logError("Exception in gets method, calling fallback method", e, logContext);

        String cacheKey = TEACHERS_CACHE_KEY;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if(cached != null){
            List<EntityModel> cachedTeachers = objectMapper.convertValue(
                cached,
                new TypeReference<List<EntityModel>>() {}
            );
            loggingService.logInfo("Get Data from cache: " + cacheKey + " in fallback method", logContext);
            return cachedTeachers;
        }
        loggingService.logError("No cached data found, throwing ServiceUnavailableException", e, logContext);
        loggingService.logError("Failed to get teachers, service is currently unavailable", e, logContext);
        throw new ServiceUnavailableExceptionHandle("QLGV service is currently unavailable", "Please try again later");
    }
    
    public EntityModel getByUserIdFallback(Integer userId, Exception e) {
        LogContext logContext = getLogContext("getByUserIdFallback");
        loggingService.logError("Exception in getByUserId method, calling fallback method", e, logContext);

        String cacheKey = TEACHER_BY_USER_ID_CACHE_PREFIX + userId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if(cached != null){
            EntityModel cachedTeacher = objectMapper.convertValue(
                cached,
                EntityModel.class
            );
            loggingService.logInfo("Get teacher by userId from cache: " + cacheKey + " in fallback method", logContext);
            return cachedTeacher;
        }
        loggingService.logError("No cached data found, throwing ServiceUnavailableException", e, logContext);
        loggingService.logError("Failed to get teacher by userId, service is currently unavailable", e, logContext);
        throw new ServiceUnavailableExceptionHandle("QLGV service is currently unavailable", "Please try again later");
    }

    public List<EntityModel> getStudentsByClassManagingFallback(String classManaging, String authToken, Exception e) {
        LogContext logContext = getLogContext("getStudentsByClassManagingFallback");
        loggingService.logError("Exception in getStudentsByClassManaging method, calling fallback method", e, logContext);

        String cacheKey = TEACHER_BY_CLASS_MANAGING_CACHE_PREFIX + classManaging;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if(cached != null){
            List<EntityModel> cachedTeachers = objectMapper.convertValue(
                cached,
                new TypeReference<List<EntityModel>>() {}
            );
            loggingService.logInfo("Get students by classManaging from cache: " + cacheKey + " in fallback method", logContext);
            return cachedTeachers;
        }
        loggingService.logError("No cached data found, throwing ServiceUnavailableException", e, logContext);
        loggingService.logError("Failed to get students by classManaging, service is currently unavailable", e, logContext);
        throw new ServiceUnavailableExceptionHandle("QLGV service is currently unavailable", "Please try again later");
    }

    public void createByUserIdFallback(Exception e) {
        LogContext logContext = getLogContext("createByUserIdFallback");
        loggingService.logError("Exception in createByUserId method, calling fallback method", e, logContext);
        loggingService.logError("Failed to create teacher by userId, service is currently unavailable", e, logContext);
        throw new ServiceUnavailableExceptionHandle("QLGV service is currently unavailable", "Please try again later");
    }

    public EntityModel updateFallback(Exception e) {
        LogContext logContext = getLogContext("updateFallback");
        loggingService.logError("Exception in update method, calling fallback method", e, logContext);
        loggingService.logError("Failed to update teacher, service is currently unavailable", e, logContext);
        throw new ServiceUnavailableExceptionHandle("QLGV service is currently unavailable", "Please try again later");
    }

    public Boolean deletesFallback(Exception e) {
        LogContext logContext = getLogContext("deletesFallback");
        loggingService.logError("Exception in delete method, calling fallback method", e, logContext);
        loggingService.logError("Failed to delete teachers, service is currently unavailable", e, logContext);
        throw new ServiceUnavailableExceptionHandle("QLGV service is currently unavailable", "Please try again later");
    }
    
    public PagedResponseModel<EntityModel> getsPagedFallback(PagedRequestModel pagedRequest, Exception e) {
        LogContext logContext = getLogContext("getsPagedFallback");
        loggingService.logError("Exception in getsPaged method, calling fallback method", e, logContext);

        String cacheKey = String.format("teachers:paged:page=%d:size=%d:sort=%s:%s", 
            pagedRequest.getPage(), 
            pagedRequest.getSize(), 
            pagedRequest.getSortBy(), 
            pagedRequest.getSortDirection());
        
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if(cached != null){
            PagedResponseModel<EntityModel> cachedPaged = objectMapper.convertValue(
                cached,
                new TypeReference<PagedResponseModel<EntityModel>>() {}
            );
            loggingService.logInfo("Get paged data from cache: " + cacheKey + " in fallback method", logContext);
            return cachedPaged;
        }
        loggingService.logError("No cached data found, throwing ServiceUnavailableException", e, logContext);
        loggingService.logError("Failed to get paged teachers, service is currently unavailable", e, logContext);
        throw new ServiceUnavailableExceptionHandle("QLGV service is currently unavailable", "Please try again later");
    }

    public List<EntityModel> filterFallback(Integer id, String firstName, String lastName, 
                                            Integer age, Gender gender, String email, 
                                            String phoneNumber, String classManaging, String department, Exception e) {
        LogContext logContext = getLogContext("filterFallback");
        loggingService.logError("Exception in filter method, calling fallback method", e, logContext);

        String cacheKey = String.format(
            "teachers:filter:id=%d:firstName=%s:lastName=%s:age=%d:gender=%s:email=%s:phoneNumber=%s:classManaging=%s:department=%s", 
            id, firstName, lastName, age, gender, email, phoneNumber, classManaging, department
        );
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            List<EntityModel> cachedFilteredData = objectMapper.convertValue(
                cached, 
                new TypeReference<List<EntityModel>>() {}
            );
            loggingService.logInfo("Fallback: Get filtered data from cache: " + cacheKey, logContext);
            return cachedFilteredData;
        }
        loggingService.logError("No cached data found, throwing ServiceUnavailableException", e, logContext);
        loggingService.logError("Failed to filter teachers, service is currently unavailable", e, logContext);
        throw new ServiceUnavailableExceptionHandle("QLGV service is currently unavailable", "Please try again later");
    }
}

