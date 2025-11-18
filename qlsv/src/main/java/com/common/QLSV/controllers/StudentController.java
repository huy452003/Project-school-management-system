package com.common.QLSV.controllers;

import com.common.QLSV.services.imp.StudentServiceImp;
import com.security_shared.annotations.RequiresAuth;
import com.security_shared.annotations.CurrentUser;
import com.model_shared.models.Response;
import com.model_shared.models.user.EntityModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.model_shared.models.user.UserDto;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import com.kafka_shared.models.StudentEvent;
import com.kafka_shared.services.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handle_exceptions.TooManyRequestsExceptionHandle;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;

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
    @Autowired
    ObjectMapper objectMapper;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlsv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @GetMapping("")
    @RequiresAuth(roles = {"ADMIN", "STUDENT"}, permissions = {"STUDENT_READ"})
    @RateLimiter(name = "student-controller", fallbackMethod = "getRateLimitFallback")
    ResponseEntity<Response<List<EntityModel>>> get(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("get");
        
        loggingService.logInfo("Get students API called successfully by user: " + currentUser.getUsername()
                , logContext);

        List<EntityModel> entityModels = studentServiceImp.gets();
        Response<List<EntityModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.getSuccess", null, locale)
                , "StudentsModel"
                , null
                , entityModels
        );
        return ResponseEntity.status(response.status()).body(response);
    }

    public ResponseEntity<Response<List<EntityModel>>> getRateLimitFallback(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            io.github.resilience4j.ratelimiter.RequestNotPermitted ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getRateLimitFallback");
        
        loggingService.logWarn("Rate limit exceeded for GET API: '/students' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous"), logContext);
        
        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage("response.error.rateLimitExceeded", 
            new Object[]{retryAfterSeconds}, locale);
        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }

    @PutMapping("/{id}")
    @RequiresAuth(roles = {"ADMIN", "STUDENT"}, permissions = {"STUDENT_WRITE"})
    @RateLimiter(name = "student-controller", fallbackMethod = "updateRateLimitFallback")
    ResponseEntity<Response<EntityModel>> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateEntityModel req,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("update");

        loggingService.logInfo("Update students API called successfully by user: " + currentUser.getUsername(), logContext);
        req.setId(id);
        EntityModel student = studentServiceImp.update(req);
        
        String fullName = student.getUser().getFirstName() + " " + student.getUser().getLastName();
            StudentEvent studentEvent = StudentEvent.studentUpdated(
                student.getId().toString(),
                fullName
            );
            kafkaProducerService.sendStudentEvent(studentEvent);
            loggingService.logInfo("Sent student updated event for student: " + fullName, logContext);
        
        Response<EntityModel> response = new Response<>(
                200
                , messageSource.getMessage("response.message.updateSuccess", null, locale)
                , "StudentsModel"
                , null
                , student
            );
            return ResponseEntity.status(response.status()).body(response);
    }

    public ResponseEntity<Response<EntityModel>> updateRateLimitFallback(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateEntityModel req,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            io.github.resilience4j.ratelimiter.RequestNotPermitted ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("updateRateLimitFallback");
        
        loggingService.logWarn("Rate limit exceeded for PUT API: '/students/" + id + "' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous"), logContext);
        
        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage("response.error.rateLimitExceededUpdate", 
            new Object[]{retryAfterSeconds}, locale);
        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }

    @DeleteMapping("")
    @RequiresAuth(roles = {"ADMIN", "STUDENT"}, permissions = {"STUDENT_DELETE"})
    @RateLimiter(name = "student-controller", fallbackMethod = "deleteRateLimitFallback")
    ResponseEntity<Response<List<EntityModel>>> delete(
            @RequestBody List<Integer> userIds,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("delete");
        loggingService.logInfo("Delete students API called successfully by user : " + currentUser.getUsername()
                , logContext);
        studentServiceImp.deletes(userIds);
        
        for (Integer userId : userIds) {
            StudentEvent studentEvent = StudentEvent.studentDeleted(
                userId.toString()
            );
            kafkaProducerService.sendStudentEvent(studentEvent);
            loggingService.logInfo("Sent student deleted event for student: " + userId, logContext);
        }

            Response<List<EntityModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.deleteSuccess", null, locale)
                    , "StudentsModel",
                    null,
                    null
            );
            return ResponseEntity.status(response.status()).body(response);
    }

    public ResponseEntity<Response<List<EntityModel>>> deleteRateLimitFallback(
            @RequestBody List<Integer> userIds,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            io.github.resilience4j.ratelimiter.RequestNotPermitted ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("deleteRateLimitFallback");
        
        loggingService.logWarn("Rate limit exceeded for DELETE API: '/students' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous"), logContext);
        
        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage("response.error.rateLimitExceededDelete", 
            new Object[]{retryAfterSeconds}, locale);
        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }

    @GetMapping("/public")
    ResponseEntity<Response<List<EntityModel>>> public_get(
            @RequestHeader(value = "Accept-Language"
                    , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("public_get");

        loggingService.logInfo("Get students public API called successfully ", logContext);
        List<EntityModel> studentModels = studentServiceImp.gets();
        Response<List<EntityModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.getSuccess", null, locale)
                , "StudentsModel"
                , null
                , studentModels
        );
        return ResponseEntity.status(response.status()).body(response);
    }


    // @GetMapping("/paged")
    // @RequiresAuth(roles = {"ADMIN", "STUDENT"}, permissions = {"STUDENT_READ"})
    // ResponseEntity<Response<PagedResponseModel<StudentModel>>> getPaged(
    //     // để trang hiện tại mặc định là 0 bên backend và khi frontend muốn hiển thị phân trang sẽ +1
    //         @RequestParam(defaultValue = "0") int page, 
    //         @RequestParam(defaultValue = "3") int size,
    //         @RequestParam(defaultValue = "id") String sortBy,
    //         @RequestParam(defaultValue = "asc") String sortDirection,
    //         @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
    //         @CurrentUser UserDto currentUser)
    // {
    //     Locale locale = Locale.forLanguageTag(acceptLanguage);
    //     LogContext logContext = getLogContext("getPaged");
        
    //     loggingService.logInfo("Get paged students API called successfully by user: " + currentUser.getUsername()
    //             + " - Page: " + page + ", Size: " + size + ", Sort: " + sortBy + " " + sortDirection, logContext);

    //     PagedRequestModel pagedRequest = new PagedRequestModel(page, size, sortBy, sortDirection);
    //     PagedResponseModel<StudentModel> pagedResponse = studentServiceImp.getsPaged(pagedRequest);
        
    //     Response<PagedResponseModel<StudentModel>> response = new Response<>(
    //             200, 
    //             messageSource.getMessage("response.message.getSuccess", null, locale),
    //             "StudentsModel",
    //             null, 
    //             pagedResponse
    //     );
        
    //     return ResponseEntity.status(response.getStatus()).body(response);
    // }

//     @GetMapping("/filter")
//     @RequiresAuth(roles = {"ADMIN", "STUDENT"}, permissions = {"STUDENT_READ"})
//     public ResponseEntity<Response<List<StudentModel>>> filter(
//         @RequestParam(required = false) Integer id,
//         @RequestParam(required = false) String firstName,
//         @RequestParam(required = false) String lastName,
//         @RequestParam(required = false) Integer age,
//         @RequestParam(required = false) Gender gender,
//         @RequestParam(required = false) Boolean graduate,
//         @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
//         @CurrentUser UserDto currentUser
//     ){
//         Locale locale = Locale.forLanguageTag(acceptLanguage);
//         LogContext logContext = getLogContext("filter");
//         loggingService.logInfo(
//                 "Filter students API called successfully by user: " + currentUser.getUsername()
//                 , logContext
//         );

//         List<StudentModel> studentModels = studentServiceImp.filter(id, firstName, lastName, age, gender, graduate);
//         Response<List<StudentModel>> response = new Response<>(
//                 200
//                 , messageSource.getMessage("response.message.getSuccess", null, locale)
//                 , "StudentsModel"
//                 , null
//                 , studentModels
//         );
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

}
