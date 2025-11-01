package com.common.QLGV.services.imp;

import com.common.QLGV.entities.TeacherEntity;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.model_shared.models.teacher.TeacherModel;
import com.handle_exceptions.NotFoundExceptionHandle;
import com.common.QLGV.repositories.TeacherRepo;
import com.common.QLGV.services.TeacherService;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model_shared.enums.Gender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private static final String TEACHERS_CACHE_KEY = "teachers:all";

    private LogContext getLogContext(String methodName){
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    // @Override
    // public List<TeacherModel> gets() {

    //     LogContext logContext = getLogContext("gets");

    //     Object cached = redisTemplate.opsForValue().get(TEACHERS_CACHE_KEY);
    //     if (cached != null) {
    //         loggingService.logInfo("Get teachers from Redis cache : " + TEACHERS_CACHE_KEY, logContext);
    //         @SuppressWarnings("unchecked")
    //         List<TeacherModel> cachedList = (List<TeacherModel>) cached;
    //         return cachedList;
    //     }
    //     loggingService.logInfo("Not found cache, Query DB", logContext);

    //     List<TeacherEntity> teacherEntities = teacherRepo.findAll();
    //     if(teacherEntities.isEmpty()){
    //         loggingService.logWarn("No teachers found in database", logContext);
    //         throw new NotFoundExceptionHandle("", Collections.emptyList(), "TeacherModel");
    //     }

    //     List<TeacherModel> teacherModels = new ArrayList<>();
    //     for(TeacherEntity teacherEntity : teacherEntities){
    //         TeacherModel teacherModel = modelMapper.map(teacherEntity, TeacherModel.class);
    //         loggingService.logTeacherOperation("GET", teacherEntity.getId(), logContext);
    //         teacherModels.add(teacherModel);
    //     }

    //     redisTemplate.opsForValue()
    //             .set(TEACHERS_CACHE_KEY, teacherModels);
    //     loggingService.logInfo("Save cache to Redis : " + TEACHERS_CACHE_KEY, logContext);

    //     return teacherModels;
    // }

    // @Override
    // public List<TeacherEntity> creates(List<CreateTeacherModel> createTeacherModels) {
    //     LogContext logContext = getLogContext("creates");

    //     List<TeacherEntity> teacherEntities = new ArrayList<>();
    //     for(CreateTeacherModel createTeacherModel : createTeacherModels){
    //         TeacherEntity teacherEntity = modelMapper.map(createTeacherModel, TeacherEntity.class);
    //         teacherEntities.add(teacherEntity);
    //         loggingService.logTeacherOperation("CREATE", teacherEntity.getId(), logContext);
    //     }
    //     teacherRepo.saveAll(teacherEntities);
        
    //     loggingService.logInfo("Create Teachers Successfully", logContext);

    //     redisTemplate.delete(TEACHERS_CACHE_KEY);
    //     loggingService.logInfo("Del cache key: " + TEACHERS_CACHE_KEY + " after create teachers", logContext);

    //     return teacherEntities;
    // }

    // @Override
    // public List<TeacherEntity> updates(List<TeacherModel> teacherModels) {
    //      LogContext logContext = getLogContext("updates");

    //     List<TeacherEntity> teacherEntities = new ArrayList<>();
    //     List<String> listIDNotFound = new ArrayList<>();
    //     for(TeacherModel teacherModel : teacherModels){
    //         TeacherEntity teacherEntity = modelMapper.map(teacherModel, TeacherEntity.class);
    //         if (teacherRepo.findById(teacherModel.getId()).isPresent()) {
    //             teacherEntities.add(teacherEntity);
    //             loggingService.logTeacherOperation("UPDATE", teacherModel.getId(), logContext);
    //         }else {
    //             listIDNotFound.add(String.valueOf(teacherEntity.getId()));
    //         }
    //     }

    //     if(!listIDNotFound.isEmpty()){
    //         loggingService.logWarn("Teachers not found: " + listIDNotFound, logContext);
    //         throw new NotFoundExceptionHandle("", listIDNotFound, "TeacherModel");
    //     }

    //     teacherRepo.saveAll(teacherEntities);
    //     loggingService.logInfo("Update Teacher Successfully", logContext);

    //     redisTemplate.delete(TEACHERS_CACHE_KEY);
    //     loggingService.logInfo("Del cache key: " + TEACHERS_CACHE_KEY + " after update teachers", logContext);

    //     return teacherEntities;
    // }

    // @Override
    // public boolean deletes(List<TeacherModel> teacherModels) {
    //     LogContext logContext = getLogContext("deletes");

    //     List<String> listIDNotFound = new ArrayList<>();
    //     for(TeacherModel teacherModel : teacherModels){
    //         if (teacherRepo.findById(teacherModel.getId()).isPresent()) {
    //             teacherRepo.deleteById(teacherModel.getId());
    //             loggingService.logTeacherOperation("DELETE", teacherModel.getId(), logContext);
    //         }else {
    //             listIDNotFound.add(String.valueOf(teacherModel.getId()));
    //         }
    //     }

    //     if(!listIDNotFound.isEmpty()){
    //         loggingService.logWarn("Teachers not found: " + listIDNotFound, logContext);
    //         throw new NotFoundExceptionHandle("", listIDNotFound, "TeacherModel");
    //     }

    //     loggingService.logInfo("Delete Teacher Successfully", logContext);

    //     redisTemplate.delete(TEACHERS_CACHE_KEY);
    //     loggingService.logInfo("Del cache key: " + TEACHERS_CACHE_KEY + " after delete teachers", logContext);
        
    //     return true;
    // }

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

