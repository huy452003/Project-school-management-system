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

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class StudentClientServiceImp implements StudentClientService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    TeacherRepo teacherRepo;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private static final String STUDENTS_CACHE_KEY = "students:all";
    private static final String TEACHERS_CACHE_KEY = "teachers:all";

    @Override
    public List<StudentModel> getAllStudents() {

        String studentApi = "http://localhost:8080/students";
            ResponseEntity<Response<List<StudentModel>>> response = restTemplate.exchange(
                    studentApi,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            Object cached = redisTemplate.opsForValue().get(STUDENTS_CACHE_KEY);
            if (cached != null) {
                log.info("Get data from Redis cache");
                return (List<StudentModel>) cached;
            }
            log.info("Not found cache, Query DB");

            redisTemplate.opsForValue().set(STUDENTS_CACHE_KEY, response.getBody().getData());
            log.info("Save cache to Redis");
            
            return response.getBody().getData();
    }

    @Transactional
    @Override
    public void createTeacherAndStudent(CreateTeacherAndStudent createTeacherAndStudent) {
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
        log.info("Del cache key = students:all , teachers:all , after create students-teachers");
    }
}
