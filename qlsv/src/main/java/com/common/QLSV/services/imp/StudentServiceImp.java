package com.common.QLSV.services.imp;

import com.common.QLSV.entities.StudentEntity;
import com.common.QLSV.exceptions.NotFoundIDStudentsException;
import com.common.QLSV.models.Student.CreateStudentModel;
import com.common.QLSV.models.Student.StudentModel;
import com.common.QLSV.repositories.StudentRepo;
import com.common.QLSV.services.StudentService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class StudentServiceImp implements StudentService {
    @Autowired
    StudentRepo studentRepo;
    @Autowired
    ModelMapper modelMapper;

    @Cacheable(value = "students")
    @Override
    public List<StudentModel> gets() {
        System.out.println("run cache");
        List<StudentEntity> studentEntities = studentRepo.findAll();
        log.info(studentEntities.isEmpty() ? "No students found" : "Gets Student Successfully");
        List<StudentModel> studentModels = new ArrayList<>();
        for (StudentEntity students : studentEntities) {
            StudentModel studentModel = modelMapper.map(students, StudentModel.class);
            studentModels.add(studentModel);
        }
        if (studentModels.isEmpty()) {
            throw new NotFoundIDStudentsException("", null);
        }
        return studentModels;
    }

    @CacheEvict(value = "students", allEntries = true)
    @Override
    public List<StudentEntity> creates(List<CreateStudentModel> studentModels) {
        List<StudentEntity> studentEntities = new ArrayList<>();
        for (CreateStudentModel studentModel : studentModels) {
            StudentEntity studentEntity = modelMapper.map(studentModel, StudentEntity.class);
            studentEntities.add(studentEntity);
        }
        log.info(studentEntities.isEmpty() ? "Create Students Failed" : "Create Students Successfully");
        studentRepo.saveAll(studentEntities);
        return studentEntities;
    }

    @CacheEvict(value = "students", allEntries = true)
    @Override
    public List<StudentEntity> updates(List<StudentModel> studentModels) {
        System.out.println("del cache");
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
        log.info(listIDNotFound.isEmpty() ? "Update Students Successfully" : "Not Found ID: " + listIDNotFound);
        if (listIDNotFound.isEmpty()) {
            studentRepo.saveAll(studentEntities);
            return studentEntities;
        }
        throw new NotFoundIDStudentsException("", listIDNotFound);
    }

    @CacheEvict(value = "students", allEntries = true)
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
        log.info(listIDNotFound.isEmpty() ? "Delete Students Successfully" : "Not Found ID: " + listIDNotFound);
        if (listIDNotFound.isEmpty()) {
            studentRepo.deleteAll(listDelete);
            return true;
        }
        throw new NotFoundIDStudentsException("", listIDNotFound);
    }
}
