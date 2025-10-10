package com.common.QLSV.services.imp;

import com.common.QLSV.entities.StudentEntity;
import com.common.QLSV.repositories.StudentRepo;
import com.common.QLSV.services.StudentService;
import com.model_shared.models.pages.PagedResponse;
import com.model_shared.models.pages.PaginationRequest;
import com.model_shared.models.student.CreateStudentModel;
import com.model_shared.models.student.StudentModel;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import com.handle_exceptions.NotFoundExceptionHandle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        List<StudentModel> studentModels = new ArrayList<>();
        for (StudentEntity students : studentEntities) {
            StudentModel studentModel = modelMapper.map(students, StudentModel.class);
            studentModels.add(studentModel);
            loggingService.logStudentOperation("GET", String.valueOf(students.getId()), logContext);
        }
        loggingService.logInfo("Gets Student Successfully", logContext);

        redisTemplate.opsForValue()
                .set(STUDENT_CACHE_KEY, studentModels);
        loggingService.logInfo("Save cache to Redis : " + STUDENT_CACHE_KEY, logContext);

        return studentModels;
    }

    @Override
    public PagedResponse<StudentModel> getsPaged(PaginationRequest paginationRequest) {
        LogContext logContext = getLogContext("getsPaged");
        
        // Tạo cache key cho phân trang
        String cacheKey = String.format("students:paged:page=%d:size=%d:sort=%s:%s", 
            paginationRequest.getPage(), 
            paginationRequest.getSize(), 
            paginationRequest.getSortBy(), 
            paginationRequest.getSortDirection());
        
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            loggingService.logInfo("Get paged data from Redis cache : " + cacheKey, logContext);
            PagedResponse<StudentModel> cachedPaged = objectMapper.convertValue(cached, 
                    objectMapper.getTypeFactory().constructParametricType(
                        PagedResponse.class, StudentModel.class));
            return cachedPaged;
        }
        
        loggingService.logWarn("Not found paged cache, Query DB", logContext);
        
        // Tạo Sort object
        Sort sort = Sort.by(
            "asc".equalsIgnoreCase(paginationRequest.getSortDirection()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC, 
            paginationRequest.getSortBy()
        );
        
        // Tạo Pageable object, pageable là đối tượng để phân trang
        Pageable pageable = PageRequest.of(
            paginationRequest.getPage(), 
            paginationRequest.getSize(), 
            sort
        );
        
// lấy data dựa trên pageable đã cấu hình ( số trang, số lượng phần tử trên mỗi trang, cách sắp xếp )
        Page<StudentEntity> studentPage = studentRepo.findAll(pageable);
        
        if (studentPage.isEmpty()) {
            throw new NotFoundExceptionHandle(
                "", 
                List.of(String.valueOf(pageable.getPageNumber())), 
                "StudentModel");
        }
 
        List<StudentModel> studentModels = new ArrayList<>();
        for (StudentEntity studentEntity : studentPage.getContent()) {
            StudentModel student = modelMapper.map(studentEntity, StudentModel.class);
            studentModels.add(student);
            loggingService.logStudentOperation("GET_PAGED", String.valueOf(studentEntity.getId()), logContext);
        }
        
        PagedResponse<StudentModel> pagedResponse = new PagedResponse<>(
            studentModels,
            paginationRequest.getPage(),
            paginationRequest.getSize(),
            studentPage.getTotalElements()
        );
        
        loggingService.logInfo("Gets Paged Students Successfully", logContext);
        
        // Cache kết quả phân trang (cache trong 5 phút)
        redisTemplate.opsForValue().set(cacheKey, pagedResponse, 300, java.util.concurrent.TimeUnit.SECONDS);
        loggingService.logInfo("Save paged cache to Redis : " + cacheKey, logContext);
        
        return pagedResponse;
    }

    @Override
    public List<StudentEntity> creates(List<CreateStudentModel> studentModels) {
        LogContext logContext = getLogContext("creates");

        List<StudentEntity> studentEntities = new ArrayList<>();
        for (CreateStudentModel studentModel : studentModels) {
            StudentEntity studentEntity = modelMapper.map(studentModel, StudentEntity.class);
            studentEntities.add(studentEntity);
            loggingService.logStudentOperation("CREATE", String.valueOf(studentEntity.getId()), logContext);
        }
        studentRepo.saveAll(studentEntities);
        
        loggingService.logInfo("Create Students Successfully", logContext);

        redisTemplate.delete(STUDENT_CACHE_KEY);
        loggingService.logInfo("Del cache key = students:all , after create students : " + STUDENT_CACHE_KEY, logContext);

        return studentEntities;
    }

    @Override
    public List<StudentEntity> updates(List<StudentModel> studentModels) {
        LogContext logContext = getLogContext("updates");

        List<StudentEntity> studentEntities = new ArrayList<>();
        List<String> listIDNotFound = new ArrayList<>();
        for (StudentModel studentModel : studentModels) {
            StudentEntity studentEntity = modelMapper.map(studentModel, StudentEntity.class);
            if (studentRepo.findById(studentModel.getId()).isPresent()) {
                studentEntities.add(studentEntity);
                loggingService.logStudentOperation("UPDATE", String.valueOf(studentModel.getId()), logContext);
            } else {
                listIDNotFound.add(String.valueOf(studentEntity.getId()));
            }
        }

        if (!listIDNotFound.isEmpty()) {
            loggingService.logError("Found IDs not exist: " + listIDNotFound, null, logContext);
            throw new NotFoundExceptionHandle("", listIDNotFound, "StudentModel");
        }

        studentRepo.saveAll(studentEntities);
        loggingService.logInfo("Update Students Successfully", logContext);

        redisTemplate.delete(STUDENT_CACHE_KEY);
        loggingService.logInfo("Del cache key = students:all , after update students : " + STUDENT_CACHE_KEY, logContext);

        return studentEntities;
    }

    @Override
    public Boolean deletes(List<StudentModel> StudentModels) {
        LogContext logContext = getLogContext("deletes");

        List<StudentEntity> listDelete = new ArrayList<>();
        List<String> listIDNotFound = new ArrayList<>();
        for (StudentModel StudentModel : StudentModels) {
            StudentEntity studentEntity = modelMapper.map(StudentModel, StudentEntity.class);
            if (studentRepo.findById(studentEntity.getId()).isPresent()) {
                listDelete.add(studentEntity);
                loggingService.logStudentOperation("DELETE", String.valueOf(studentEntity.getId()), logContext);
            } else {
                listIDNotFound.add(String.valueOf(studentEntity.getId()));
            }
        }

        if (!listIDNotFound.isEmpty()) {
            loggingService.logError("Found IDs not exist: " + listIDNotFound, null, logContext);
            throw new NotFoundExceptionHandle("", listIDNotFound,"StudentModel");
        }

        studentRepo.deleteAll(listDelete);
        loggingService.logInfo("Delete Students Successfully", logContext);


        redisTemplate.delete(STUDENT_CACHE_KEY);
        loggingService.logInfo("Del cache key = students:all , after del students : " + STUDENT_CACHE_KEY, logContext);

        return true;
    }
}
