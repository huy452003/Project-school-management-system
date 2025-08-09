package com.common.QLSV.services.imp;

import com.common.QLSV.entities.StudentEntity;

import com.common.QLSV.repositories.StudentRepo;
import com.common.QLSV.services.StudentService;
import com.common.models.student.CreateStudentModel;
import com.common.models.student.StudentModel;
import com.handle_exceptions.NotFoundExceptionHandle;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class StudentServiceImp implements StudentService {
    @Autowired
    StudentRepo studentRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private static final String STUDENT_CACHE_KEY = "students:all";

    @Override
    public List<StudentModel> gets() {

        Object cached = redisTemplate.opsForValue().get(STUDENT_CACHE_KEY);
        if (cached != null) {
            log.info("Get data from Redis cache");
            return (List<StudentModel>) cached;
        }
        log.info("Not found cache, Query DB");

        List<StudentEntity> studentEntities = studentRepo.findAll();

        if (studentEntities.isEmpty()) {
            throw new NotFoundExceptionHandle("", Collections.emptyList(), "StudentModel");
        }

        List<StudentModel> studentModels = new ArrayList<>();
        for (StudentEntity students : studentEntities) {
            StudentModel studentModel = modelMapper.map(students, StudentModel.class);
            studentModels.add(studentModel);
        }
        log.info("Gets Student Successfully");

        redisTemplate.opsForValue()
                .set(STUDENT_CACHE_KEY, studentModels);
        log.info("Save cache to Redis");

        return studentModels;
    }

    @Override
    public List<StudentEntity> creates(List<CreateStudentModel> studentModels) {
        List<StudentEntity> studentEntities = new ArrayList<>();
        for (CreateStudentModel studentModel : studentModels) {
            StudentEntity studentEntity = modelMapper.map(studentModel, StudentEntity.class);
            studentEntities.add(studentEntity);
        }
        studentRepo.saveAll(studentEntities);
        log.info("Create Students Successfully");

        redisTemplate.delete(STUDENT_CACHE_KEY);
        log.info("Del cache key = students:all , after create students");

        return studentEntities;
    }

    @Override
    public List<StudentEntity> updates(List<StudentModel> studentModels) {
        List<StudentEntity> studentEntities = new ArrayList<>();
        List<Integer> listIDNotFound = new ArrayList<>();
        for (StudentModel studentModel : studentModels) {
            StudentEntity studentEntity = modelMapper.map(studentModel, StudentEntity.class);
            if (studentRepo.findById(studentModel.getId()).isPresent()) {
                studentEntities.add(studentEntity);
            } else {
                listIDNotFound.add(studentEntity.getId());
            }
        }

        if (!listIDNotFound.isEmpty()) {
            log.error("Found IDs not exist: " + listIDNotFound);
            throw new NotFoundExceptionHandle("", listIDNotFound, "StudentModel");
        }

        studentRepo.saveAll(studentEntities);
        log.info("Update Students Successfully");

        redisTemplate.delete(STUDENT_CACHE_KEY);
        log.info("Del cache key = students:all , after update students");

        return studentEntities;
    }

    @Override
    public Boolean deletes(List<StudentModel> StudentModels) {
        List<StudentEntity> listDelete = new ArrayList<>();
        List<Integer> listIDNotFound = new ArrayList<>();
        for (StudentModel StudentModel : StudentModels) {
            StudentEntity studentEntity = modelMapper.map(StudentModel, StudentEntity.class);
            if (studentRepo.findById(studentEntity.getId()).isPresent()) {
                listDelete.add(studentEntity);
            } else {
                listIDNotFound.add(studentEntity.getId());
            }
        }

        if (!listIDNotFound.isEmpty()) {
            log.error("Found IDs not exist: " + listIDNotFound);
            throw new NotFoundExceptionHandle("", listIDNotFound,"StudentModel");
        }

        studentRepo.deleteAll(listDelete);
        log.info("Delete Students Successfully");


        redisTemplate.delete(STUDENT_CACHE_KEY);
        log.info("Del cache key = students:all , after del students");

        return true;
    }
}
