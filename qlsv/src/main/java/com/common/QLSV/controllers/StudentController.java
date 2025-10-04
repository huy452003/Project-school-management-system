package com.common.QLSV.controllers;

import com.common.QLSV.services.imp.StudentServiceImp;
import com.security_shared.annotations.RequiresAuth;
import com.security_shared.annotations.CurrentUser;
import com.model_shared.models.Response;
import com.model_shared.models.student.CreateStudentModel;
import com.model_shared.models.student.StudentModel;
import com.model_shared.models.student.request.CreateStudentModelRequest;
import com.model_shared.models.student.request.StudentModelRequest;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import com.model_shared.models.UserDto;
import com.kafka_shared.models.StudentEvent;
import com.kafka_shared.services.KafkaProducerService;
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
    @Autowired
    KafkaProducerService kafkaProducerService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlsv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @GetMapping("")
    @RequiresAuth(permissions = {"ADMIN_READ", "STUDENT_READ"})
    ResponseEntity<Response<List<StudentModel>>> get(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("get");
        
        loggingService.logInfo("Get students API called successfully by user: " + currentUser.getUserName()
                , logContext);

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
    @RequiresAuth(roles = {"ADMIN", "STUDENT"}, permissions = {"ADMIN_WRITE", "STUDENT_WRITE"})
    ResponseEntity<Response<List<CreateStudentModel>>> add(
            @Valid @RequestBody CreateStudentModelRequest req,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("add");

        List<CreateStudentModel> studentModels = req.getStudents();
        loggingService.logInfo("Create students API called successfully by user: " + currentUser.getUserName(), logContext);
        studentServiceImp.creates(studentModels);
        
        // Send Kafka events for each created student
        for (CreateStudentModel studentModel : studentModels) {
            String fullName = studentModel.getFirstName() + " " + studentModel.getLastName();
            StudentEvent studentEvent = StudentEvent.studentCreated(
                "temp-id-" + System.currentTimeMillis(), // Temporary ID since CreateStudentModel doesn't have ID
                fullName
            );
            kafkaProducerService.sendEvent(studentEvent, "student-events");
            loggingService.logInfo("Sent student created event for student: " + fullName, logContext);
        }
        
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
    @RequiresAuth(roles = {"ADMIN", "STUDENT"}, permissions = {"ADMIN_WRITE", "STUDENT_WRITE"})
    ResponseEntity<Response<List<StudentModel>>> update(
            @Valid @RequestBody StudentModelRequest req,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("update");

        List<StudentModel> studentModels = req.getStudents();
        loggingService.logInfo("Update students API called successfully by user: " + currentUser.getUserName(), logContext);
        studentServiceImp.updates(studentModels);
        
        // Send Kafka events for each updated student
        for (StudentModel studentModel : studentModels) {
            String fullName = studentModel.getFirstName() + " " + studentModel.getLastName();
            StudentEvent studentEvent = StudentEvent.studentUpdated(
                studentModel.getId().toString(),
                fullName
            );
            kafkaProducerService.sendEvent(studentEvent, "student-events");
            loggingService.logInfo("Sent student updated event for student: " + fullName, logContext);
        }
        
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
    @RequiresAuth(roles = {"ADMIN", "STUDENT"}, permissions = {"ADMIN_DELETE", "STUDENT_DELETE"})
    ResponseEntity<Response<List<StudentModel>>> delete(
            @RequestBody List<StudentModel> studentModels,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("delete");
        loggingService.logInfo("Delete students API called successfully by user : " + currentUser.getUserName()
                , logContext);
        studentServiceImp.deletes(studentModels);
        
        // Send Kafka events for each deleted student BEFORE deletion
        for (StudentModel studentModel : studentModels) {
            String fullName = studentModel.getFirstName() + " " + studentModel.getLastName();
            StudentEvent studentEvent = StudentEvent.studentDeleted(
                studentModel.getId().toString(),
                fullName
            );
            kafkaProducerService.sendEvent(studentEvent, "student-events");
            loggingService.logInfo("Sent student deleted event for student: " + fullName, logContext);
        }

            Response<List<StudentModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.deleteSuccess", null, locale)
                    , "StudentsModel",
                    null,
                    null
            );
            return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/public")
    ResponseEntity<Response<List<StudentModel>>> public_get(
            @RequestHeader(value = "Accept-Language"
                    , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("public_get");

        loggingService.logInfo("Get students public API called successfully ", logContext);
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

}
