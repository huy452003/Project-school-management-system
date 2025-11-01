package com.common.QLSV.services.imp;

import com.common.QLSV.entities.StudentEntity;
import com.common.QLSV.repositories.StudentRepo;
import com.common.QLSV.services.StudentService;
import com.model_shared.models.student.StudentModel;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import com.handle_exceptions.NotFoundExceptionHandle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.model_shared.models.user.UpdateEntityModel;
import com.model_shared.models.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import com.security_shared.services.SecurityService;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.springframework.transaction.annotation.Transactional;


@Service
public class StudentServiceImp implements StudentService {
    @Autowired
    StudentRepo studentRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    LoggingService loggingService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    SecurityService securityService;
    @Value("${security.base-url:http://localhost:8083}")
    private String securityBaseUrl;

    private static final String STUDENT_CACHE_KEY = "students:all";

    private LogContext getLogContext(String methodName) {
            return LogContext.builder()
                    .module("qlsv")
                    .className(this.getClass().getSimpleName())
                    .methodName(methodName)
                    .build();
    }

    @Override
    public List<StudentModel> gets() {
        LogContext logContext = getLogContext("gets");

        Object cached = redisTemplate.opsForValue().get(STUDENT_CACHE_KEY);
        if (cached != null) {
            loggingService.logInfo("Get data from Redis cache : " + STUDENT_CACHE_KEY, logContext);
            @SuppressWarnings("unchecked")
            List<StudentModel> cachedList = (List<StudentModel>) cached;
            return cachedList;
        }
        loggingService.logWarn("Not found cache, Query DB", logContext);

        List<StudentEntity> studentEntities = studentRepo.findAll();

        if (studentEntities.isEmpty()) {
            throw new NotFoundExceptionHandle("", Collections.emptyList(), "StudentModel");
        }

        // Collect all userIds
        List<Integer> userIds = studentEntities.stream()
                .map(StudentEntity::getUserId)
                .distinct()
                .toList();

        // Get users from Security via SecurityService
        Map<Integer, UserDto> usersById = securityService.getUsersByIds(userIds);

        // Map to StudentModel with enriched user data
        List<StudentModel> studentModels = new ArrayList<>();
        for (StudentEntity studentEntity : studentEntities) {
            UserDto userDto = usersById.get(studentEntity.getUserId());
            
            // Sync graduate to user.profileData
            if (userDto != null) {
                if (userDto.getProfileData() == null) {
                    userDto.setProfileData(new HashMap<>());
                }
                userDto.getProfileData().put("graduate", studentEntity.getGraduate());
            }
            
            StudentModel studentModel = new StudentModel();
            studentModel.setId(studentEntity.getId());
            studentModel.setUser(userDto);
            
            studentModels.add(studentModel);
            loggingService.logStudentOperation("GET", studentEntity.getId(), logContext);
        }

        loggingService.logInfo("Gets Student Successfully", logContext);

        redisTemplate.opsForValue()
                .set(STUDENT_CACHE_KEY, studentModels);
        loggingService.logInfo("Save cache to Redis : " + STUDENT_CACHE_KEY, logContext);

        return studentModels;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createByUserId(UserDto user) {
        // throw new RuntimeException("FAKE ERROR FOR TESTING DLQ");
        
        LogContext logContext = getLogContext("createByUserId");

        // Kiểm tra xem đã có student với userId này chưa
        StudentEntity existingStudent = studentRepo.findByUserId(user.getUserId()).orElse(null);
        
        if (existingStudent != null) {
            loggingService.logWarn("Student profile already exists for userId: " + user.getUserId() + 
                ". This may be orphaned from previous failed registration. Deleting and recreating...", logContext);
            
            // Xóa student cũ (orphaned) để tạo lại
            studentRepo.delete(existingStudent);
            studentRepo.flush(); // Flush để đảm bảo DELETE thực thi trước INSERT
            
            loggingService.logInfo("Deleted orphaned student for userId: " + user.getUserId(), logContext);
        }

        // Tạo student mới
        StudentEntity student = new StudentEntity();
        student.setUserId(user.getUserId());
        student.setGraduate((Boolean) user.getProfileData().get("graduate"));
        studentRepo.save(student);

        redisTemplate.delete(STUDENT_CACHE_KEY);
        loggingService.logInfo("Del cache key: " + STUDENT_CACHE_KEY + " after create student", logContext);
        loggingService.logInfo("Created student profile for userId: " + user.getUserId(), logContext);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentModel update(UpdateEntityModel req) {
        LogContext logContext = getLogContext("update");

        if (req == null || req.getId() == null) {
            throw new NotFoundExceptionHandle("", List.of("Student ID is required"), "StudentModel");
        }

        if (req.getUser() == null || req.getUser().getUserId() == null) {
            throw new NotFoundExceptionHandle("", List.of("User data is required"), "StudentModel");
        }

        // 1. Find StudentEntity by id
        StudentEntity studentEntity = studentRepo.findById(req.getId())
                .orElseThrow(() -> new NotFoundExceptionHandle("", 
                    List.of(String.valueOf(req.getId())), "StudentModel"));

        // 2. Verify userId matches (security check)
        if (!studentEntity.getUserId().equals(req.getUser().getUserId())) {
            throw new NotFoundExceptionHandle("", 
                List.of("User ID mismatch with Student"), "StudentModel");
        }

        UserDto updatedUser = securityService.updateUser(req.getUser());
        loggingService.logInfo("Updated user data for userId: " + updatedUser.getUserId(), logContext);

        // 4. Update StudentEntity (graduate field)
        if (req.getUser().getProfileData() != null && 
            req.getUser().getProfileData().containsKey("graduate")) {
            Boolean graduate = (Boolean) req.getUser().getProfileData().get("graduate");
            studentEntity.setGraduate(graduate);
            studentRepo.save(studentEntity);
            loggingService.logInfo("Updated graduate status for student id: " + studentEntity.getId(), logContext);
        }

        // 5. Sync graduate to updatedUser.profileData
        if (updatedUser.getProfileData() == null) {
            updatedUser.setProfileData(new HashMap<>());
        }
        updatedUser.getProfileData().put("graduate", studentEntity.getGraduate());

        // 6. Build and return updated StudentModel
        StudentModel updatedStudentModel = new StudentModel();
        updatedStudentModel.setId(req.getId());
        updatedStudentModel.setUser(updatedUser);

        loggingService.logStudentOperation("UPDATE", req.getId(), logContext);
        loggingService.logInfo("Update Student Successfully", logContext);

        redisTemplate.delete(STUDENT_CACHE_KEY);
        loggingService.logInfo("Del cache key: " + STUDENT_CACHE_KEY + " after update students", logContext);

        return updatedStudentModel;
    }

    // @Override
    // public Boolean deletes(List<StudentModel> StudentModels) {
    //     LogContext logContext = getLogContext("deletes");

    //     List<StudentEntity> listDelete = new ArrayList<>();
    //     List<String> listIDNotFound = new ArrayList<>();
    //     for (StudentModel StudentModel : StudentModels) {
    //         StudentEntity studentEntity = modelMapper.map(StudentModel, StudentEntity.class);
    //         if (studentRepo.findById(studentEntity.getId()).isPresent()) {
    //             listDelete.add(studentEntity);
    //             loggingService.logStudentOperation("DELETE", studentEntity.getId(), logContext);
    //         } else {
    //             listIDNotFound.add(String.valueOf(studentEntity.getId()));
    //         }
    //     }

    //     if (!listIDNotFound.isEmpty()) {
    //         loggingService.logError("Found IDs not exist: " + listIDNotFound, null, logContext);
    //         throw new NotFoundExceptionHandle("", listIDNotFound,"StudentModel");
    //     }

    //     studentRepo.deleteAll(listDelete);
    //     loggingService.logInfo("Delete Students Successfully", logContext);


    //     redisTemplate.delete(STUDENT_CACHE_KEY);
    //     loggingService.logInfo("Del cache key: " + STUDENT_CACHE_KEY + " after del students", logContext);

    //     return true;
    // }

    // @Override
    // public PagedResponseModel<StudentModel> getsPaged(PagedRequestModel pagedRequest) {
    //     LogContext logContext = getLogContext("getsPaged");
        
    //     // Tạo cache key cho phân trang
    //     String cacheKey = String.format("students:paged:page=%d:size=%d:sort=%s:%s", 
    //         pagedRequest.getPage(), 
    //         pagedRequest.getSize(), 
    //         pagedRequest.getSortBy(), 
    //         pagedRequest.getSortDirection());
        
    //     Object cached = redisTemplate.opsForValue().get(cacheKey);
    //     if (cached != null) {
    //         loggingService.logInfo("Get paged data from Redis cache : " + cacheKey, logContext);
    //         PagedResponseModel<StudentModel> cachedPaged = objectMapper.convertValue(cached, 
    //                 objectMapper.getTypeFactory().constructParametricType(
    //                     PagedResponseModel.class, StudentModel.class));
    //         return cachedPaged;
    //     }
        
    //     loggingService.logWarn("Not found paged cache, Query DB", logContext);
        
    //     // Tạo Sort object
    //     Sort sort = Sort.by(
    //         "asc".equalsIgnoreCase(pagedRequest.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC
    //         ,pagedRequest.getSortBy()
    //     );
        
    //     // Tạo Pageable object, pageable là đối tượng để phân trang
    //     Pageable pageable = PageRequest.of(
    //         pagedRequest.getPage(), 
    //         pagedRequest.getSize(), 
    //         sort
    //     );
        
    //     // lấy data dựa trên pageable đã cấu hình ( số trang, số lượng phần tử trên mỗi trang, cách sắp xếp )
    //     Page<StudentEntity> studentPage = studentRepo.findAll(pageable);
        
    //     if (studentPage.isEmpty()) {
    //         loggingService.logWarn("No students found in database", logContext);
    //         throw new NotFoundExceptionHandle(
    //             "", 
    //             List.of(String.valueOf(pageable.getPageNumber())), 
    //             "StudentModel");
    //     }
 
    //     List<StudentModel> studentModels = new ArrayList<>();
    //     for (StudentEntity studentEntity : studentPage.getContent()) {
    //         StudentModel student = modelMapper.map(studentEntity, StudentModel.class);
    //         studentModels.add(student);
    //         loggingService.logStudentOperation("GET_PAGED", studentEntity.getId(), logContext);
    //     }
        
    //     PagedResponseModel<StudentModel> pagedResponse = new PagedResponseModel<>(
    //         studentModels,
    //         pageable.getPageNumber(),
    //         pageable.getPageSize(),
    //         studentPage.getTotalElements()
    //     );
        
    //     loggingService.logInfo("Gets Paged Students Successfully", logContext);
        
    //     // Cache kết quả phân trang (cache trong 5 phút)
    //     redisTemplate.opsForValue().set(cacheKey, pagedResponse, 300, java.util.concurrent.TimeUnit.SECONDS);
    //     loggingService.logInfo("Save paged cache to Redis : " + cacheKey, logContext);
        
    //     return pagedResponse;
    // }

    // @Override
    // public List<StudentModel> filter(Integer id, String firstName, String lastName, Integer age, Gender gender, Boolean graduate) {
    //     LogContext logContext = getLogContext("filter");
        
    //     List<StudentModel> studentModels = new ArrayList<>();
    //     List<StudentEntity> studentEntities = studentRepo.findAll();

    //     if (studentEntities.isEmpty()) {
    //         loggingService.logWarn("No students found in database", logContext);
    //         throw new NotFoundExceptionHandle("", Collections.emptyList(), "StudentModel");
    //     }

    //     // Đếm số điều kiện được truyền vào
    //     int conditionCount = 0;
    //     boolean hasIdCondition = (id != null && id > 0);
    //     boolean hasFirstNameCondition = (firstName != null && !firstName.trim().isEmpty());
    //     boolean hasLastNameCondition = (lastName != null && !lastName.trim().isEmpty());
    //     boolean hasAgeCondition = (age != null && age > 0);
    //     boolean hasGenderCondition = gender != null;
    //     boolean hasGraduateCondition = graduate != null;
        
    //     if (hasIdCondition) conditionCount++;
    //     if (hasFirstNameCondition) conditionCount++;
    //     if (hasLastNameCondition) conditionCount++;
    //     if (hasAgeCondition) conditionCount++;
    //     if (hasGenderCondition) conditionCount++;
    //     if (hasGraduateCondition) conditionCount++;

    //     if (conditionCount == 0) {
    //         loggingService.logWarn("No conditions provided for filtering", logContext);
    //         return gets();
    //     }

    //     loggingService.logInfo("Filtering with " + conditionCount + " conditions", logContext);

    //     for (StudentEntity studentEntity : studentEntities) {
    //         int matchedConditions = 0;
            
    //         // Kiểm tra từng điều kiện và đếm số điều kiện thỏa mãn
    //         if (hasIdCondition && studentEntity.getId() == id) {
    //             matchedConditions++;
    //         }
    //         if (hasFirstNameCondition && studentEntity.getFirstName() != null &&
    //             studentEntity.getFirstName().toLowerCase().contains(firstName.toLowerCase())
    //         ) {
    //             matchedConditions++;
    //         }
    //         if (hasLastNameCondition && studentEntity.getLastName() != null &&
    //             studentEntity.getLastName().toLowerCase().contains(lastName.toLowerCase())
    //         ) {
    //             matchedConditions++;
    //         }
    //         if (hasAgeCondition && studentEntity.getAge() == age) {
    //             matchedConditions++;
    //         }
    //         if (hasGenderCondition && studentEntity.getGender() == gender) {
    //             matchedConditions++;
    //         }
    //         if (hasGraduateCondition && studentEntity.isGraduate() == graduate) {
    //             matchedConditions++;
    //         }
            
    //         // Chỉ thêm student nếu thỏa mãn TẤT CẢ các điều kiện được truyền vào
    //         if (matchedConditions == conditionCount) {
    //             studentModels.add(modelMapper.map(studentEntity, StudentModel.class));
    //         }
    //     }
        
    //     loggingService.logInfo("Filter Students Successfully. Found " + studentModels.size() + " students with the given filters", logContext);
    //     return studentModels;
    // }
}
