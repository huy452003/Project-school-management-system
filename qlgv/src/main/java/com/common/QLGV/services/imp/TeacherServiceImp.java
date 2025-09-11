package com.common.QLGV.services.imp;

import com.common.QLGV.entities.TeacherEntity;
import com.common.models.teacher.CreateTeacherModel;
import com.common.models.teacher.TeacherModel;
import com.handle_exceptions.NotFoundExceptionHandle;
import com.common.QLGV.repositories.TeacherRepo;
import com.common.QLGV.services.TeacherService;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static final String TEACHERS_CACHE_KEY = "teachers:all";

    private LogContext getLogContext(String methodName){
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getName())
                .methodName(methodName)
                .build();
    }

    @Override
    public List<TeacherModel> gets() {

        LogContext logContext = getLogContext("gets");

        Object cached = redisTemplate.opsForValue().get(TEACHERS_CACHE_KEY);
        if (cached != null) {
            loggingService.logInfo("Get teachers from Redis cache", logContext);
            return (List<TeacherModel>) cached;
        }
        loggingService.logInfo("Not found cache, Query DB", logContext);

        List<TeacherEntity> teacherEntities = teacherRepo.findAll();
        if(teacherEntities.isEmpty()){
            loggingService.logWarn("No teachers found in database", logContext);
            throw new NotFoundExceptionHandle("", Collections.emptyList(), "TeacherModel");
        }

        List<TeacherModel> teacherModels = new ArrayList<>();
        for(TeacherEntity teacherEntity : teacherEntities){
            TeacherModel teacherModel = modelMapper.map(teacherEntity, TeacherModel.class);
            loggingService.logTeacherOperation("GET", String.valueOf(teacherEntity.getId()), logContext);
            teacherModels.add(teacherModel);
        }

        redisTemplate.opsForValue()
                .set(TEACHERS_CACHE_KEY, teacherModels);
        loggingService.logInfo("Save cache to Redis", logContext);

        return teacherModels;
    }

    @Override
    public List<TeacherEntity> creates(List<CreateTeacherModel> createTeacherModels) {
        LogContext logContext = getLogContext("creates");

        List<TeacherEntity> teacherEntities = new ArrayList<>();
        for(CreateTeacherModel createTeacherModel : createTeacherModels){
            TeacherEntity teacherEntity = modelMapper.map(createTeacherModel, TeacherEntity.class);
            teacherEntities.add(teacherEntity);
            loggingService.logTeacherOperation("CREATE", String.valueOf(teacherEntity.getId()), logContext);
        }
        teacherRepo.saveAll(teacherEntities);
        
        loggingService.logInfo("Create Teachers Successfully", logContext);

        redisTemplate.delete(TEACHERS_CACHE_KEY);
        loggingService.logInfo("Del cache key = teachers:all , after create teachers", logContext);

        return teacherEntities;
    }

    @Override
    public List<TeacherEntity> updates(List<TeacherModel> teacherModels) {
         LogContext logContext = getLogContext("updates");

        List<TeacherEntity> teacherEntities = new ArrayList<>();
        List<String> listIDNotFound = new ArrayList<>();
        for(TeacherModel teacherModel : teacherModels){
            TeacherEntity teacherEntity = modelMapper.map(teacherModel, TeacherEntity.class);
            if (teacherRepo.findById(teacherModel.getId()).isPresent()) {
                teacherEntities.add(teacherEntity);
                loggingService.logTeacherOperation("UPDATE", String.valueOf(teacherModel.getId()), logContext);
            }else {
                listIDNotFound.add(String.valueOf(teacherEntity.getId()));
            }
        }

        if(!listIDNotFound.isEmpty()){
            loggingService.logWarn("Teachers not found: " + listIDNotFound, logContext);
            throw new NotFoundExceptionHandle("", listIDNotFound, "TeacherModel");
        }

        teacherRepo.saveAll(teacherEntities);
        loggingService.logInfo("Update Teacher Successfully", logContext);

        redisTemplate.delete(TEACHERS_CACHE_KEY);
        loggingService.logInfo("Del cache key = teachers:all , after update teachers", logContext);

        return teacherEntities;
    }

    @Override
    public boolean deletes(List<TeacherModel> teacherModels) {
        LogContext logContext = getLogContext("deletes");

        List<String> listIDNotFound = new ArrayList<>();
        for(TeacherModel teacherModel : teacherModels){
            if (teacherRepo.findById(teacherModel.getId()).isPresent()) {
                teacherRepo.deleteById(teacherModel.getId());
                loggingService.logTeacherOperation("DELETE", String.valueOf(teacherModel.getId()), logContext);
            }else {
                listIDNotFound.add(String.valueOf(teacherModel.getId()));
            }
        }

        if(!listIDNotFound.isEmpty()){
            loggingService.logWarn("Teachers not found: " + listIDNotFound, logContext);
            throw new NotFoundExceptionHandle("", listIDNotFound, "TeacherModel");
        }

        loggingService.logInfo("Delete Teacher Successfully", logContext);

        redisTemplate.delete(TEACHERS_CACHE_KEY);
        loggingService.logInfo("Del cache key = teachers:all , after delete teachers", logContext);
        
        return true;
    }
}
