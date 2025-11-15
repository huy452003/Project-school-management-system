package com.common.QLGV.services.imp;

import com.common.QLGV.entities.TeacherEntity;
import com.model_shared.enums.Status;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.handle_exceptions.NotFoundExceptionHandle;
import com.common.QLGV.repositories.TeacherRepo;
import com.common.QLGV.services.TeacherService;
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
import org.springframework.transaction.annotation.Transactional;

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

    private static final String TEACHERS_CACHE_KEY = "teachers:all";

    private LogContext getLogContext(String methodName){
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @Override
    public List<EntityModel> gets() {
        LogContext logContext = getLogContext("gets");

        Object cached = redisTemplate.opsForValue().get(TEACHERS_CACHE_KEY);
        if(cached != null){
            loggingService.logInfo("Get Data from cache: " + TEACHERS_CACHE_KEY, logContext);
            @SuppressWarnings("unchecked")
            List<EntityModel> teachers = (List<EntityModel>) cached;
            return teachers;
        }
        loggingService.logWarn("Not Found Data from cache, query DB ", logContext);

        List<TeacherEntity> teacherEntities = teacherRepo.findAll();
        if(teacherEntities.isEmpty()){
            throw new NotFoundExceptionHandle("", Collections.emptyList(),"TeacherModel");
        }

        List<Integer> userIds = teacherEntities.stream().map(TeacherEntity::getUserId).distinct().toList();

        Map<Integer, UserDto> userByIds = securityService.getUsersByIds(userIds);
        List<EntityModel> teacherModels = new ArrayList<>();

        for(TeacherEntity teacherEntity : teacherEntities){
            UserDto user = userByIds.get(teacherEntity.getUserId());
            if(user != null && user.getStatus().equals(Status.ENABLED)){
                if(user.getProfileData() == null){
                    user.setProfileData(new HashMap<>());
                }
                EntityModel teacherModel = modelMapper.map(teacherEntity, EntityModel.class);
                teacherModel.setId(teacherEntity.getId());
                teacherModel.setUser(user);

                teacherModels.add(teacherModel);
                loggingService.logTeacherOperation("GET", teacherModel.getId(), logContext);
            }
        }
        loggingService.logInfo("Get Teachers Successfully", logContext);
        redisTemplate.opsForValue().set(TEACHERS_CACHE_KEY, teacherModels);
        loggingService.logInfo("save cached to redis: " + TEACHERS_CACHE_KEY, logContext);
        return teacherModels;
    }

    @Override
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
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

        teacherRepo.save(newTeacher);
        redisTemplate.delete(TEACHERS_CACHE_KEY);

        loggingService.logInfo("Deleted cached key: " + TEACHERS_CACHE_KEY
                + " after create teacher", logContext);
        loggingService.logInfo("Created teacher profile for userId: " + user.getUserId(), logContext);
    }

    @Override
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
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

        teacherRepo.save(teacherEntity);
        loggingService.logStudentOperation("UPDATE", teacherEntity.getId(), logContext);
        loggingService.logInfo("Update teacher Successfully", logContext);

        UserDto user = securityService.updateUser(req.getUser());
        loggingService.logInfo("Updated user data for userId: " + user.getUserId(), logContext);
        user.setProfileData(new HashMap<>());

        EntityModel teacherModel = new EntityModel();
        teacherModel.setId(req.getId());
        teacherModel.setUser(user);

        redisTemplate.delete(TEACHERS_CACHE_KEY);
        loggingService.logInfo("Del cache key: " + TEACHERS_CACHE_KEY + " after update teacher", logContext);

        return teacherModel;
    }

    @Override
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
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

        // @Override
    // public PagedResponseModel<TeacherModel> getsPaged(PagedRequestModel pagedRequest) {
    //     LogContext logContext = getLogContext("getsPaged");
        
    //     String cacheKey = String.format("teachers:paged:page=%d:size=%d:sort=%s:%s", 
    //         pagedRequest.getPage(), 
    //         pagedRequest.getSize(), 
    //         pagedRequest.getSortBy(), 
    //         pagedRequest.getSortDirection());
            
    //     Object cached = redisTemplate.opsForValue().get(cacheKey);
    //     if(cached != null){
    //         loggingService.logInfo("Get paged data from Redis cache : " + cacheKey, logContext);
    //         PagedResponseModel<TeacherModel> cachedPaged = objectMapper.convertValue(cached, 
    //                 objectMapper.getTypeFactory().constructParametricType(
    //                     PagedResponseModel.class, TeacherModel.class));
    //         return cachedPaged;
    //     }

    //     loggingService.logWarn("Not found paged cache, query DB", logContext);

    //     Sort sort = Sort.by(
    //         "asc".equalsIgnoreCase(pagedRequest.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC
    //         ,pagedRequest.getSortBy()
    //     );

    //     Pageable pageable = PageRequest.of(
    //         pagedRequest.getPage(),
    //         pagedRequest.getSize(),
    //         sort
    //     );

    //     Page<TeacherEntity> teacherPage = teacherRepo.findAll(pageable);
    //     if(teacherPage.isEmpty()){
    //         loggingService.logWarn("Not found teachers in database", logContext);
    //         throw new NotFoundExceptionHandle(
    //             "", 
    //             List.of(String.valueOf(pageable.getPageNumber())), 
    //             "TeacherModel");
    //     }
    //     List<TeacherModel> teacherModels = new ArrayList<>();
    //     for(TeacherEntity teacherEntity : teacherPage.getContent()){
    //         TeacherModel teacherModel = modelMapper.map(teacherEntity, TeacherModel.class);
    //         teacherModels.add(teacherModel);
    //         loggingService.logTeacherOperation("GET_PAGED", teacherEntity.getId(), logContext);
    //     }

    //     PagedResponseModel<TeacherModel> pagedResponse = new PagedResponseModel<>(
    //         teacherModels,
    //         pageable.getPageNumber(),
    //         pageable.getPageSize(),
    //         teacherPage.getTotalElements()
    //     );

    //     loggingService.logInfo("Gets Paged Teachers Successfully", logContext);

    //     redisTemplate.opsForValue().set(cacheKey, pagedResponse, 300, TimeUnit.SECONDS);
    //     loggingService.logInfo("Save paged cache to Redis : " + cacheKey, logContext);

    //     return pagedResponse;
    // }

    // @Override
    // public List<TeacherModel> filter(Integer id, String firstName, String lastName, Integer age, Gender gender) {
    //     LogContext logContext = getLogContext("filter");

    //     int conditionCount = 0;
    //     boolean hasIdCondition = (id != null && id > 0);
    //     boolean hasFirstNameCondition = (firstName != null && !firstName.trim().isEmpty());
    //     boolean hasLastNameCondition = (lastName != null && !lastName.trim().isEmpty());
    //     boolean hasAgeCondition = (age != null && age > 0);
    //     boolean hasGenderCondition = gender != null;

    //     if (hasIdCondition) conditionCount++;
    //     if (hasFirstNameCondition) conditionCount++;
    //     if (hasLastNameCondition) conditionCount++;
    //     if (hasAgeCondition) conditionCount++;
    //     if (hasGenderCondition) conditionCount++;

    //     if (conditionCount == 0) {
    //         loggingService.logWarn("No conditions provided for filtering", logContext);
    //         return gets();
    //     }

    //     loggingService.logInfo("Filtering with " + conditionCount + " conditions", logContext);

    //     List<TeacherModel> teacherModels = new ArrayList<>();
    //     List<TeacherEntity> teacherEntities = teacherRepo.findAll();

    //     if (teacherEntities.isEmpty()) {
    //         loggingService.logWarn("No teachers found in database with the given filters", logContext);
    //         throw new NotFoundExceptionHandle("", Collections.emptyList(), "TeacherModel");
    //     }

    //     for(TeacherEntity teacherEntity : teacherEntities){
    //         int matchedConditions = 0;
    //         if (hasIdCondition && teacherEntity.getId() == id) {
    //             matchedConditions++;
    //         }
    //         if (hasFirstNameCondition && teacherEntity.getFirstName() != null &&
    //             teacherEntity.getFirstName().toLowerCase().contains(firstName.toLowerCase())
    //         )
    //         {
    //             matchedConditions++;
    //         }
    //         if (hasLastNameCondition && teacherEntity.getLastName() != null &&
    //             teacherEntity.getLastName().toLowerCase().contains(lastName.toLowerCase())
    //         )
    //         {
    //             matchedConditions++;
    //         }
    //         if (hasAgeCondition && teacherEntity.getAge() == age) {
    //             matchedConditions++;
    //         }
    //         if (hasGenderCondition && teacherEntity.getGender() == gender) {
    //             matchedConditions++;
    //         }
    //         if (matchedConditions == conditionCount) {
    //             teacherModels.add(modelMapper.map(teacherEntity, TeacherModel.class));
    //         }
    //     }

    //     loggingService.logInfo("Filter Teachers Successfully. Found " + teacherModels.size() + " teachers with the given filters", logContext);
        
    //     return teacherModels;     
    // }
    
}

