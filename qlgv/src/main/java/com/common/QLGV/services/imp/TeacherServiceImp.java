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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private static final String TEACHERS_CACHE_KEY = "teachers:all";

    @Override
    public List<TeacherModel> gets() {

        Object cached = redisTemplate.opsForValue().get(TEACHERS_CACHE_KEY);
        if (cached != null) {
            log.info("Get data from Redis cache");
            return (List<TeacherModel>) cached;
        }
        log.info("Not found cache, Query DB");

        List<TeacherEntity> teacherEntities = teacherRepo.findAll();
        if(teacherEntities.isEmpty()){
            throw new NotFoundTeacherException("", Collections.emptyList());
        }

        List<TeacherModel> teacherModels = new ArrayList<>();
        for(TeacherEntity teacherEntity : teacherEntities){
            TeacherModel teacherModel = modelMapper.map(teacherEntity, TeacherModel.class);
            teacherModels.add(teacherModel);
        }
        log.info("Gets teachers Successfully");

        redisTemplate.opsForValue()
                .set(TEACHERS_CACHE_KEY, teacherModels);
        log.info("Save cache to Redis");

        return teacherModels;
    }

    @Override
    public List<TeacherEntity> creates(List<CreateTeacherModel> createTeacherModels) {
        List<TeacherEntity> teacherEntities = new ArrayList<>();
        for(CreateTeacherModel createTeacherModel : createTeacherModels){
            TeacherEntity teacherEntity = modelMapper.map(createTeacherModel, TeacherEntity.class);
            teacherEntities.add(teacherEntity);
        }
        teacherRepo.saveAll(teacherEntities);
        log.info("Create Teachers Successfully");

        redisTemplate.delete(TEACHERS_CACHE_KEY);
        log.info("Del cache key = teachers:all , after create teachers");

        return teacherEntities;
    }

    @Override
    public List<TeacherEntity> updates(List<TeacherModel> teacherModels) {
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

        if(!listIDNotFound.isEmpty()){
            throw new NotFoundTeacherException("",listIDNotFound);
        }

        teacherRepo.saveAll(teacherEntities);
        log.info("Update Teacher Successfully");

        redisTemplate.delete(TEACHERS_CACHE_KEY);
        log.info("Del cache key = teachers:all after update teachers");

        return teacherEntities;
    }

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

        if(!listIDNotFound.isEmpty()){
            throw new NotFoundTeacherException("",listIDNotFound);
        }

        teacherRepo.deleteAll(teacherEntities);
        log.info("Delete Teachers Successfully");

        redisTemplate.delete(TEACHERS_CACHE_KEY);
        log.info("Del cache key = teachers:all After del teachers");

        return true;
    }
}
