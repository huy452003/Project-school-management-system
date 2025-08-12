package com.common.QLSV.controllers;

import com.common.QLSV.entities.StudentEntity;
import com.common.QLSV.services.imp.StudentServiceImp;
import com.common.models.Response;
import com.common.models.student.CreateStudentModel;
import com.common.models.student.StudentModel;
import com.common.models.student.request.CreateStudentModelRequest;
import com.common.models.student.request.StudentModelRequest;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    StudentServiceImp studentServiceImp;
    @Autowired
    ReloadableResourceBundleMessageSource messageSource;
    @Autowired
    LoggingService loggingService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlsv")
                .className("StudentController")
                .methodName(methodName)
                .build();
    }

    @GetMapping("")
    ResponseEntity<Response<List<StudentModel>>> get(
            @RequestHeader(value = "Accept-Language"
                    , defaultValue = "en") String acceptLanguage)
    {
        LogContext logContext = getLogContext("get");

        Locale locale = Locale.forLanguageTag(acceptLanguage);
        loggingService.logInfo("Get students API called successfully", logContext);
        List<StudentModel> studentModels = studentServiceImp.gets();
        Response<List<StudentModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.getSuccess", null, locale)
                , "StudentsModel"
                , null
                , studentModels
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("")
    ResponseEntity<Response<List<CreateStudentModel>>> add(
            @Valid @RequestBody CreateStudentModelRequest req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        LogContext logContext = getLogContext("add");

        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<CreateStudentModel> studentModels = req.getStudents();
        loggingService.logInfo("Create students API called successfully", logContext);
        List<StudentEntity> studentEntities = studentServiceImp.creates(studentModels);
            Response<List<CreateStudentModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.createSuccess", null, locale)
                    , "StudentsModel"
                    , null
                    , studentModels
            );
            return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("")
    ResponseEntity<Response<List<StudentModel>>> update(
            @Valid @RequestBody StudentModelRequest req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        LogContext logContext = getLogContext("update");

        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<StudentModel> studentModels = req.getStudents();
        loggingService.logInfo("Update students API called successfully", logContext);
        List<StudentEntity> studentEntities = studentServiceImp.updates(studentModels);
            Response<List<StudentModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.updateSuccess", null, locale)
                    , "StudentsModel"
                    , null
                    , studentModels
            );
            return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("")
    ResponseEntity<Response<List<StudentModel>>> delete(
            @RequestBody List<StudentModel> studentModels
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        LogContext logContext = getLogContext("delete");

        Locale locale = Locale.forLanguageTag(acceptLanguage);
        loggingService.logInfo("Delete students API called successfully", logContext);
        studentServiceImp.deletes(studentModels);
            Response<List<StudentModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.deleteSuccess", null, locale)
                    , "StudentsModel",
                    null,
                    null
            );
            return ResponseEntity.status(response.getStatus()).body(response);
    }
}
