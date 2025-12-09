package com.common.QLSV.controllers;

import com.common.QLSV.services.imp.StudentServiceImp;
import com.security_shared.annotations.RequiresAuth;
import com.security_shared.annotations.CurrentUser;
import com.security_shared.services.SecurityService;
import com.model_shared.models.Response;
import com.model_shared.utils.IpAddressUtils;
import com.model_shared.models.user.EntityModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.model_shared.models.user.UserDto;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.model_shared.enums.Gender;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import com.handle_exceptions.TooManyRequestsExceptionHandle;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


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
    SecurityService securityService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlsv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    // Clear cache endpoint - chỉ cho phép ADMIN
    @DeleteMapping("/cache")
    @RequiresAuth(roles = {"ADMIN"})
    ResponseEntity<Response<String>> clearCache(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        LogContext logContext = getLogContext("clearCache");
        
        loggingService.logInfo("Clear students cache API called by user: " + currentUser.getUsername(), logContext);
        
        studentServiceImp.clearCache();
        
        Response<String> response = new Response<>(
            200,
            "Students cache cleared successfully",
            "StudentModel",
            null,
            "Cache cleared"
        );
        return ResponseEntity.status(response.status()).body(response);
    }
    
    // Get
    @GetMapping("")
    @RequiresAuth(roles = {"ADMIN", "STUDENT", "TEACHER"}, permissions = {"STUDENT_READ","TEACHER_READ"})
    @RateLimiter(name = "student-controller", fallbackMethod = "getRateLimitFallback")
    ResponseEntity<Response<List<EntityModel>>> get(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("get");
        
        loggingService.logInfo("Get students API called successfully by user: "
            + currentUser.getUsername(), logContext);

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

    @GetMapping("/{userId}")
    @RequiresAuth(roles = {"ADMIN", "STUDENT", "TEACHER"}, permissions = {"STUDENT_READ","TEACHER_READ"})
    @RateLimiter(name = "student-controller", fallbackMethod = "getByUserIdRateLimitFallback")
    ResponseEntity<Response<EntityModel>> getByUserId(
            @PathVariable("userId") Integer userId,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getByUserId");

        loggingService.logInfo("Get student by userId: " + userId
            + " called successfully by user: " + currentUser.getUsername(), logContext);

        EntityModel student = studentServiceImp.getByUserId(userId);
        Response<EntityModel> response = new Response<>(
                200
                , messageSource.getMessage("response.message.getSuccess", null, locale)
                , "StudentsModel"
                , null
                , student
            );
            return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/by-class/{schoolClass}")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
    @RateLimiter(name = "student-controller", fallbackMethod = "getBySchoolClassRateLimitFallback")
    ResponseEntity<Response<List<EntityModel>>> getBySchoolClass(
            @PathVariable("schoolClass") String schoolClass,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getBySchoolClass");

        loggingService.logInfo("Get students by schoolClass: " + schoolClass
            + " called successfully by user: " + currentUser.getUsername(), logContext);
        
        List<EntityModel> students = studentServiceImp.getBySchoolClass(schoolClass);
        Response<List<EntityModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.getSuccess", null, locale)
                , "StudentsModel"
                , null
                , students
            );
            return ResponseEntity.status(response.status()).body(response);
    }

    // Update
    @PutMapping("/{id}")
    @RequiresAuth(roles = {"ADMIN", "STUDENT", "TEACHER"}, permissions = {"STUDENT_WRITE","TEACHER_WRITE"})
    @RateLimiter(name = "student-update-controller", fallbackMethod = "updateRateLimitFallback")
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
        EntityModel student = studentServiceImp.update(req, currentUser);
        
        Response<EntityModel> response = new Response<>(
                200
                , messageSource.getMessage("response.message.updateSuccess", null, locale)
                , "StudentsModel"
                , null
                , student
            );
            return ResponseEntity.status(response.status()).body(response);
    }

    // Delete
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

            Response<List<EntityModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.deleteSuccess", null, locale)
                    , "StudentsModel",
                    null,
                    null
            );
            return ResponseEntity.status(response.status()).body(response);
    }

    // Paged
    @GetMapping("/paged")
    @RequiresAuth(roles = {"ADMIN", "STUDENT", "TEACHER"}, permissions = {"STUDENT_READ", "TEACHER_READ"})
    @RateLimiter(name = "student-controller", fallbackMethod = "getPagedRateLimitFallback")
    ResponseEntity<Response<PagedResponseModel<EntityModel>>> getPaged(
        // để trang hiện tại mặc định là 0 bên backend và khi frontend muốn hiển thị phân trang sẽ +1
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getPaged");
        
        loggingService.logInfo("Get paged students API called successfully by user: " + currentUser.getUsername()
                + " - Page: " + page + ", Size: " + size + ", Sort: " + sortBy + " " + sortDirection, logContext);

        PagedRequestModel pagedRequest = new PagedRequestModel(page, size, sortBy, sortDirection);
        PagedResponseModel<EntityModel> pagedResponse = studentServiceImp.getsPaged(pagedRequest);
        
        Response<PagedResponseModel<EntityModel>> response = new Response<>(
                200, 
                messageSource.getMessage("response.message.getSuccess", null, locale),
                "StudentsModel",
                null, 
                pagedResponse
        );
        
        return ResponseEntity.status(response.status()).body(response);
    }

    // Filter
    @GetMapping("/filter")
    @RequiresAuth(roles = {"ADMIN", "STUDENT", "TEACHER"}, permissions = {"STUDENT_READ", "TEACHER_READ"})
    @RateLimiter(name = "student-controller", fallbackMethod = "filterRateLimitFallback")
    public ResponseEntity<Response<List<EntityModel>>> filter(
        @RequestParam(required = false) Integer id,
        @RequestParam(required = false) String firstName,
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) Integer age,
        @RequestParam(required = false) Gender gender,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String phoneNumber,
        @RequestParam(required = false) Double score,
        @RequestParam(required = false) String schoolClass,
        @RequestParam(required = false) String major,
        @RequestParam(required = false) Boolean graduate,
        @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
        @CurrentUser UserDto currentUser
    ){
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("filter");
        loggingService.logInfo(
                "Filter students API called successfully by user: " + currentUser.getUsername()
                , logContext
        );

        List<EntityModel> studentModels = studentServiceImp.filter(
            id, firstName, lastName, 
            age, gender, email, 
            phoneNumber, score, schoolClass, 
            major, graduate
        );
        Response<List<EntityModel>> response = new Response<>(
                200,
                 messageSource.getMessage("response.message.getSuccess", null, locale), 
                "StudentsModel", 
                null, 
                studentModels
        );
        return ResponseEntity.status(response.status()).body(response);
    }


        // fallback method
        public ResponseEntity<Response<List<EntityModel>>> getRateLimitFallback(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            Throwable ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getRateLimitFallback");
        
        // Lấy HttpServletRequest từ RequestContextHolder vì Resilience4j không inject nó vào fallback
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";
        
        securityService.sendIpViolation(ipAddress);
        
        loggingService.logWarn("Rate limit exceeded for GET API: '/students' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);
        
        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage(
            "response.error.rateLimitExceeded",  new Object[]{retryAfterSeconds}, locale
        );

        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }

    public ResponseEntity<Response<List<EntityModel>>> getBySchoolClassRateLimitFallback(
            @PathVariable("schoolClass") String schoolClass,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            Throwable ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getBySchoolClassRateLimitFallback");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";
        securityService.sendIpViolation(ipAddress);
        loggingService.logWarn("Rate limit exceeded for GET API: '/students/by-class/" + schoolClass + "' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);

        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage(
            "response.error.rateLimitExceeded", new Object[]{retryAfterSeconds}, locale
        );

        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }

    public ResponseEntity<Response<EntityModel>> getByUserIdRateLimitFallback(
            @PathVariable("userId") Integer userId,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            Throwable ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getByUserIdRateLimitFallback");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";
        securityService.sendIpViolation(ipAddress);
        loggingService.logWarn("Rate limit exceeded for GET API: '/students/" + userId + "' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);

        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage(
            "response.error.rateLimitExceeded", new Object[]{retryAfterSeconds}, locale
        );

        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }

    public ResponseEntity<Response<EntityModel>> updateRateLimitFallback(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateEntityModel req,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            Throwable ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("updateRateLimitFallback");
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";
        securityService.sendIpViolation(ipAddress);
        
        loggingService.logWarn("Rate limit exceeded for PUT API: '/students/" + id + "' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);
        
        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage(
            "response.error.rateLimitExceeded", new Object[]{retryAfterSeconds}, locale
        );

        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }   

    public ResponseEntity<Response<List<EntityModel>>> deleteRateLimitFallback(
            @RequestBody List<Integer> userIds,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            Throwable ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("deleteRateLimitFallback");
        
        // Lấy HttpServletRequest từ RequestContextHolder vì Resilience4j không inject nó vào fallback
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";
        // Track IP violation (tăng counter và auto-block nếu vượt threshold)
        securityService.sendIpViolation(ipAddress);
        
        loggingService.logWarn("Rate limit exceeded for DELETE API: '/students' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);
        
        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage("response.error.rateLimitExceeded", 
            new Object[]{retryAfterSeconds}, locale);
        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }

    public ResponseEntity<Response<PagedResponseModel<EntityModel>>> getPagedRateLimitFallback(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            Throwable ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getPagedRateLimitFallback");
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";
        
        securityService.sendIpViolation(ipAddress);
        loggingService.logWarn("Rate limit exceeded for GET API: '/students/paged' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);
        
        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage(
            "response.error.rateLimitExceeded", new Object[]{retryAfterSeconds}, locale
        );
        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }

    public ResponseEntity<Response<List<EntityModel>>> filterRateLimitFallback(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Double score,
            @RequestParam(required = false) String schoolClass,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Boolean graduate,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            Throwable ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("filterRateLimitFallback");
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";
        
        securityService.sendIpViolation(ipAddress);
        loggingService.logWarn("Rate limit exceeded for GET API: '/students/filter' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);

        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage(
            "response.error.rateLimitExceeded", new Object[]{retryAfterSeconds}, locale
        );
        throw new TooManyRequestsExceptionHandle(
            message,
            "StudentModel",
            retryAfterSeconds
        );
    }
}
