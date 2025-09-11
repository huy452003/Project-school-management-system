package com.common.QLGV.controllers;

import com.common.QLGV.configurations.RequiresJwt;
import com.common.QLGV.entities.TeacherEntity;
import com.common.QLGV.services.imp.StudentClientServiceImp;
import com.common.models.CreateTeacherAndStudent;
import com.common.models.Response;
import com.common.models.UserDto;
import com.common.models.student.StudentModel;
import com.common.models.teacher.CreateTeacherModel;
import com.common.models.teacher.TeacherModel;
import com.common.models.teacher.request.CreateTeacherModelRequest;
import com.common.models.teacher.request.TeacherModelRequest;
import com.common.QLGV.services.imp.TeacherServiceImp;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    @Autowired
    TeacherServiceImp teacherServiceImp;
    @Autowired
    ReloadableResourceBundleMessageSource messageSource;
    @Autowired
    StudentClientServiceImp studentClientServiceImp;
    @Autowired
    LoggingService loggingService;

    private LogContext getLogContext(String methodName) {
            return LogContext.builder()
                    .module("qlgv")
                    .className(this.getClass().getName())
                    .methodName(methodName)
                    .build();
    }

    @GetMapping("")
    @RequiresJwt
    ResponseEntity<Response<List<TeacherModel>>> get(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
            , HttpServletRequest request)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("get");

        UserDto currentUser = (UserDto) request.getAttribute("currentUser");
        loggingService.logInfo("Get teachers API called successfully by user: " + currentUser.getUserName()
                , logContext);

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
        LogContext logContext = getLogContext("add");

        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<CreateTeacherModel> createTeacherModels = req.getTeachers();
        loggingService.logInfo("Create teachers API called successfully", logContext);
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
        LogContext logContext = getLogContext("update");

        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<TeacherModel> teacherModels = req.getTeachers();
        loggingService.logInfo("Update teachers API called successfully", logContext);
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
        LogContext logContext = getLogContext("delete");

        Locale locale = Locale.forLanguageTag(acceptLanguage);
        loggingService.logInfo("Delete teachers API called successfully", logContext);
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

    @GetMapping("/studentsByTeacher")
    ResponseEntity<Response<?>> getStudentsFromQLSV(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language)
    {
        Locale locale = Locale.forLanguageTag(language);
        LogContext logContext = getLogContext("getStudentsFromQLSV");


        try {
            List<StudentModel> studentModels = studentClientServiceImp.getAllStudents();
            loggingService.logInfo("Get students from QLSV API called successfully", logContext);
            return ResponseEntity.status(200).body(new Response<>(
                    200,
                    messageSource.getMessage("response.message.getSuccess",null,locale),
                    "StudentModel",
                    null,
                    studentModels
            ));
        } catch (RuntimeException e) {
            loggingService.logError("Get students from QLSV API called failed",e , logContext);
            return ResponseEntity.status(500).body(new Response<>(
                    500,
                    messageSource.getMessage(
                            "response.error.getStudentsFromQLSVFail",null,locale
                    ),
                    "StudentModel",
                    Map.of(
                            "errors: " , "500 Internal Server Error"
                    ),
                    null
            ));
        }
    }

    @PostMapping("/teachersAndStudents")
    ResponseEntity<Response<?>> createTeachersAndStudents(
            @RequestBody @Valid CreateTeacherAndStudent request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language
    ) {

        LogContext logContext = getLogContext("createTeachersAndStudents");

        Locale locale = Locale.forLanguageTag(language);
        try {
            studentClientServiceImp.createTeacherAndStudent(request);
            loggingService.logInfo("Create teachers and students API called successfully", logContext);
            return ResponseEntity.status(200).body(new Response<>(
                    200,
                    messageSource.getMessage("response.message.createSuccess",null,locale),
                    "TeacherModel-StudentModel",
                    null,
                    new CreateTeacherAndStudent(
                            request.getTeachers(),
                            request.getStudents()
                    )
            ));
        }catch (RuntimeException e){
            loggingService.logError("Create teachers and students API called failed", e, logContext);
            return ResponseEntity.status(500).body(new Response<>(
                    500,
                    messageSource.getMessage(
                            "response.error.createTeacherAndStudentFail",null,locale
                    ),
                    "TeacherModel-StudentModel",
                    Map.of(
                            "errors: " , "500 Internal Server Error"
                    ),
                    null
            ));
        }

    }

}
