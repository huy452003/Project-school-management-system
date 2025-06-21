package com.common.QLGV.services.imp;

import com.common.QLGV.entities.TeacherEntity;
import com.common.QLGV.exceptions.NotFoundTeacherException;
import com.common.models.teacher.CreateTeacherModel;
import com.common.models.teacher.TeacherModel;
import com.common.QLGV.repositories.TeacherRepo;
import com.common.QLGV.services.TeacherService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class TeacherServiceImp implements TeacherService {
    @Autowired
    TeacherRepo teacherRepo;
    @Autowired
    ModelMapper modelMapper;

    @Cacheable(value = "teachers")
    @Override
    public List<TeacherModel> gets() {
        log.info("Caching teachers");
        List<TeacherEntity> teacherEntities = teacherRepo.findAll();
        log.info(teacherEntities.isEmpty() ? "No teachers found" : "Gets teachers Successfully");
        List<TeacherModel> teacherModels = new ArrayList<>();
        for(TeacherEntity teacherEntity : teacherEntities){
            TeacherModel teacherModel = modelMapper.map(teacherEntity, TeacherModel.class);
            teacherModels.add(teacherModel);
        }
        if(teacherModels.isEmpty()){
            throw new NotFoundTeacherException("", Collections.emptyList());
        }
        return teacherModels;
    }

    @CacheEvict(value = "teachers", allEntries = true)
    @Override
    public List<TeacherEntity> creates(List<CreateTeacherModel> createTeacherModels) {
        List<TeacherEntity> teacherEntities = new ArrayList<>();
        for(CreateTeacherModel createTeacherModel : createTeacherModels){
            TeacherEntity teacherEntity = modelMapper.map(createTeacherModel, TeacherEntity.class);
            teacherEntities.add(teacherEntity);
        }
        log.info(teacherEntities.isEmpty() ? "Create Teachers Failed" : "Create Teachers Successfully");
        teacherRepo.saveAll(teacherEntities);
        return teacherEntities;
    }

    @CacheEvict(value = "teachers", allEntries = true)
    @Override
    public List<TeacherEntity> updates(List<TeacherModel> teacherModels) {
        log.info("Delete Caching teachers");
        List<TeacherEntity> teacherEntities = new ArrayList<>();
        List<Integer> listIDNotFound = new ArrayList<>();
        for(TeacherModel teacherModel : teacherModels){
            TeacherEntity teacherEntity = modelMapper.map(teacherModel, TeacherEntity.class);
            if (teacherRepo.findById(teacherModel.getId()).isPresent()) {
                teacherEntities.add(teacherEntity);
            }else {
                listIDNotFound.add(teacherEntity.getId());
            }
        }
        log.info(listIDNotFound.isEmpty() ? "Update Teacher Successfully" : "Not Found ID: " + listIDNotFound);
        if(!listIDNotFound.isEmpty()){
            throw new NotFoundTeacherException("",listIDNotFound);
        }
        teacherRepo.saveAll(teacherEntities);
        return teacherEntities;
    }

    @CacheEvict(value = "teachers", allEntries = true)
    @Override
    public boolean deletes(List<TeacherModel> teacherModels) {
        List<Integer> listIDNotFound = new ArrayList<>();
        List<TeacherEntity> teacherEntities = new ArrayList<>();
        for(TeacherModel teacherModel : teacherModels){
            TeacherEntity teacherEntity = modelMapper.map(teacherModel, TeacherEntity.class);
            if (teacherRepo.findById(teacherModel.getId()).isPresent()) {
                teacherEntities.add(teacherEntity);
            }else {
                listIDNotFound.add(teacherEntity.getId());
            }
        }
        log.info(listIDNotFound.isEmpty() ? "Delete Teachers Successfully" : "Not Found ID: " + listIDNotFound);
        if(!listIDNotFound.isEmpty()){
            throw new NotFoundTeacherException("",listIDNotFound);
        }
        teacherRepo.deleteAll(teacherEntities);
        return true;
    }
}
