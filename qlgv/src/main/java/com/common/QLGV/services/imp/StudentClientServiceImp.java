package com.common.QLGV.services.imp;

import com.common.QLGV.entities.TeacherEntity;
import com.common.QLGV.repositories.TeacherRepo;
import com.common.QLGV.services.StudentClientService;
import com.common.models.CreateStudentForTeacher;
import com.common.models.CreateTeacherAndStudent;
import com.common.models.Response;
import com.common.models.student.StudentModel;
import com.common.models.teacher.CreateTeacherModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class StudentClientServiceImp implements StudentClientService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    TeacherRepo teacherRepo;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    LoggingService loggingService;

    private static final String STUDENTS_CACHE_KEY = "students:all";
    private static final String TEACHERS_CACHE_KEY = "teachers:all";

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getName())
                .methodName(methodName)
                .build();
    }

    @Override
    public List<StudentModel> getAllStudents() {

        LogContext logContext = getLogContext("getAllStudents");

        String studentApi = "http://localhost:8080/students";
            ResponseEntity<Response<List<StudentModel>>> response = restTemplate.exchange(
                    studentApi,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            Object cached = redisTemplate.opsForValue().get(STUDENTS_CACHE_KEY);
            if (cached != null) {
                loggingService.logInfo("Get data from Redis cache", logContext);
                return (List<StudentModel>) cached;
            }
            loggingService.logInfo("Not found cache, Query DB", logContext);

            redisTemplate.opsForValue().set(STUDENTS_CACHE_KEY, response.getBody().getData());
            loggingService.logInfo("Save cache to Redis", logContext);
            
            return response.getBody().getData();
    }

    @Transactional
    @Override
    public void createTeacherAndStudent(CreateTeacherAndStudent createTeacherAndStudent) {

        LogContext logContext = getLogContext("createTeacherAndStudent");

        for (CreateTeacherModel teacherModel : createTeacherAndStudent.getTeachers()) {
            TeacherEntity teacherEntity = modelMapper.map(teacherModel , TeacherEntity.class);
            teacherRepo.save(teacherEntity);
        }
        String studentApi = "http://localhost:8080/students";
        HttpEntity<CreateStudentForTeacher> entity = new HttpEntity<>(
                new CreateStudentForTeacher(createTeacherAndStudent.getStudents())
        );
        ResponseEntity<Response<?>> response = restTemplate.exchange(
                studentApi,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        redisTemplate.delete(STUDENTS_CACHE_KEY);
        redisTemplate.delete(TEACHERS_CACHE_KEY);
        loggingService.logInfo("Del cache key = students:all , teachers:all , after create students-teachers", logContext);
    }
}
