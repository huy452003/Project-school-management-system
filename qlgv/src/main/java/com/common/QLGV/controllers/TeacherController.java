package com.common.QLGV.controllers;

import com.common.QLGV.entities.TeacherEntity;
import com.common.models.Response;
import com.common.models.teacher.CreateTeacherModel;
import com.common.models.teacher.TeacherModel;
import com.common.models.teacher.request.CreateTeacherModelRequest;
import com.common.models.teacher.request.TeacherModelRequest;
import com.common.QLGV.services.imp.TeacherServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    @Autowired
    TeacherServiceImp teacherServiceImp;
    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @GetMapping("")
    ResponseEntity<Response<List<TeacherModel>>> get(
            @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<TeacherModel> teacherModels = teacherServiceImp.gets();
        Response<List<TeacherModel>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.getSuccess", null,locale),
                "TeacherModel",
                null,
                teacherModels
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("")
    ResponseEntity<Response<List<CreateTeacherModel>>> add(
            @Valid @RequestBody CreateTeacherModelRequest req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<CreateTeacherModel> createTeacherModels = req.getTeachers();
        List<TeacherEntity> studentEntities = teacherServiceImp.creates(createTeacherModels);
        Response<List<CreateTeacherModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.createSuccess", null, locale)
                , "TeacherModel"
                , null
                , createTeacherModels
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("")
    ResponseEntity<Response<List<TeacherModel>>> update(
            @Valid @RequestBody TeacherModelRequest req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<TeacherModel> teacherModels = req.getTeachers();
        List<TeacherEntity> studentEntities = teacherServiceImp.updates(teacherModels);
        Response<List<TeacherModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.updateSuccess", null, locale)
                , "TeacherModel"
                , null
                , teacherModels
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("")
    ResponseEntity<Response<List<TeacherModel>>> delete(
            @RequestBody List<TeacherModel> studentModels
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        teacherServiceImp.deletes(studentModels);
        Response<List<TeacherModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.deleteSuccess", null, locale)
                , "TeacherModel",
                null,
                null
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
