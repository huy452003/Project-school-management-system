package com.common.QLGV.controllers;

import com.security_shared.annotations.RequiresAuth;
import com.security_shared.annotations.CurrentUser;
import com.model_shared.models.Response;
import com.handle_exceptions.ServiceUnavailableExceptionHandle;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.model_shared.models.user.UserDto;
import com.model_shared.models.user.EntityModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.common.QLGV.services.imp.TeacherServiceImp;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.security_shared.services.SecurityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import com.model_shared.utils.IpAddressUtils;
import com.handle_exceptions.TooManyRequestsExceptionHandle;
import com.model_shared.enums.Gender;


import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    @Autowired
    TeacherServiceImp teacherServiceImp;
    @Autowired
    ReloadableResourceBundleMessageSource messageSource;
    @Autowired
    LoggingService loggingService;
    @Autowired
    SecurityService securityService;

    private LogContext getLogContext(String methodName) {
            return LogContext.builder()
                    .module("qlgv")
                    .className(this.getClass().getSimpleName())
                    .methodName(methodName)
                    .build();
    }

    // Clear cache endpoint - chỉ cho phép ADMIN
    @DeleteMapping("/cache")
    @RequiresAuth(roles = {"ADMIN"})
    ResponseEntity<Response<String>> clearCache(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
            , @CurrentUser UserDto currentUser) {
        LogContext logContext = getLogContext("clearCache");
        
        loggingService.logInfo("Clear teachers cache API called by user: " + currentUser.getUsername(), logContext);
        
        teacherServiceImp.clearCache();
        
        Response<String> response = new Response<>(
            200,
            "Teachers cache cleared successfully",
            "TeacherModel",
            null,
            "Cache cleared"
        );
        return ResponseEntity.status(response.status()).body(response);
    }

    // get method
    @GetMapping("")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
    @RateLimiter(name = "teacher-controller", fallbackMethod = "getRateLimitFallback")
    ResponseEntity<Response<List<EntityModel>>> get(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
            , @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("get");

        loggingService.logInfo(
            "Get teachers API called successfully by user: " + currentUser.getUsername(),
            logContext
        );

        List<EntityModel> teacherModels = teacherServiceImp.gets();
        Response<List<EntityModel>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.getSuccess", null,locale),
                "TeacherModel",
                null,
                teacherModels
        );
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/{userId}")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
    @RateLimiter(name = "teacher-controller", fallbackMethod = "getByUserIdRateLimitFallback")
    ResponseEntity<Response<EntityModel>> getByUserId(
            @PathVariable("userId") Integer userId,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getByUserId");

        loggingService.logInfo(
            "Get teacher by userId: " + userId
            + " called successfully by user: " + currentUser.getUsername(),
            logContext
        );

        EntityModel teacher = teacherServiceImp.getByUserId(userId);
        Response<EntityModel> response = new Response<>(
                200, 
                messageSource.getMessage("response.message.getSuccess", null, locale), 
                "TeacherModel", 
                null, 
                teacher
            );
            return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/{classManaging}/students")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
    @RateLimiter(name = "teacher-controller", fallbackMethod = "getStudentsByClassManagingRateLimitFallback")
    ResponseEntity<Response<List<EntityModel>>> getStudentsByClassManaging(
            @PathVariable("classManaging") String classManaging,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @RequestHeader(value = "Authorization") String authHeader,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getStudentsByClassManaging");

        loggingService.logInfo(
            "Get students by classManaging: "  + classManaging
            + " called successfully by user: " + currentUser.getUsername(),
            logContext
        );
        
        String authToken = authHeader != null && authHeader.startsWith("Bearer ") 
            ? authHeader.substring(7) 
            : authHeader;

        List<EntityModel> students = teacherServiceImp.getStudentsByClassManaging(classManaging, authToken);
        Response<List<EntityModel>> response = new Response<>(
            200,
            messageSource.getMessage("response.message.getSuccess", null, locale),
            "StudentModel",
            null,
            students
        );
        return ResponseEntity.status(response.status()).body(response);
    }

    // update method
    @PutMapping("/{id}")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_WRITE"})
    @RateLimiter(name = "teacher-update-controller", fallbackMethod = "updateRateLimitFallback")
    ResponseEntity<Response<EntityModel>> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateEntityModel req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage
            , @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("update");

        loggingService.logInfo(
            "Update teachers API called successfully by user : " + currentUser.getUsername(), 
            logContext
        );

        req.setId(id);
        EntityModel teacherModel = teacherServiceImp.update(req);
        Response<EntityModel> response = new Response<>(
                200, 
                messageSource.getMessage("response.message.updateSuccess", null, locale), 
                "TeacherModel", 
                null, 
                teacherModel
        );
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_DELETE"})
    @RateLimiter(name = "teacher-controller", fallbackMethod = "deleteRateLimitFallback")
    ResponseEntity<Response<List<EntityModel>>> delete(
            @RequestBody List<Integer> userIds
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage
            , @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("delete");

        loggingService.logInfo(
            "Delete teachers API called successfully by user : " + currentUser.getUsername(), 
            logContext
        );

        teacherServiceImp.deletes(userIds);
        Response<List<EntityModel>> response = new Response<>(
                200, 
                messageSource.getMessage("response.message.deleteSuccess", null, locale), 
                "TeacherModel",
                null,
                null
        );
        return ResponseEntity.status(response.status()).body(response);
    }
    
    // paged method
    @GetMapping("/paged")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
    @RateLimiter(name = "teacher-paged-controller", fallbackMethod = "getPagedRateLimitFallback")
    public ResponseEntity<Response<PagedResponseModel<EntityModel>>> getPaged(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "3") int size,
                @RequestParam(defaultValue = "id") String sortBy,
                @RequestParam(defaultValue = "asc") String sortDirection,
                @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
                @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getPaged");

        loggingService.logInfo(String.format(
                "Get paged teachers API called successfully by user: %s - Page: %d, Size: %d, Sort: %s : %s", 
                currentUser.getUsername(), page, size, sortBy, sortDirection), logContext);
        
        PagedRequestModel pagedRequest = new PagedRequestModel(page, size, sortBy, sortDirection);
        PagedResponseModel<EntityModel> pagedResponse = teacherServiceImp.getsPaged(pagedRequest);

        Response<PagedResponseModel<EntityModel>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.getSuccess", null, locale),
                "TeacherModel",
                null,
                pagedResponse
        );

        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/filter")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
    @RateLimiter(name = "teacher-controller", fallbackMethod = "filterRateLimitFallback")
    public ResponseEntity<Response<List<EntityModel>>> filter(
        @RequestParam(required = false) Integer id,
        @RequestParam(required = false) String firstName,
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) Integer age,
        @RequestParam(required = false) Gender gender,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String phoneNumber,
        @RequestParam(required = false) String classManaging,
        @RequestParam(required = false) String department,
        @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
        @CurrentUser UserDto currentUser
    ){
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("filter");

        loggingService.logInfo("Filter teachers API called successfully by user: " + currentUser.getUsername(), logContext);
        List<EntityModel> teacherModels = teacherServiceImp.filter(
            id, firstName, lastName, 
            age, gender, email, 
            phoneNumber, classManaging, department
        );
        Response<List<EntityModel>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.getSuccess", null, locale),
                "TeacherModel",
                null,
                teacherModels
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

        loggingService.logWarn("Rate limit exceeded for GET API: '/teachers' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);

        // Frontend sẽ xử lý exception và retry
        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage("response.error.rateLimitExceeded", 
            new Object[]{retryAfterSeconds}, locale);
        throw new TooManyRequestsExceptionHandle(
            message,
            "TeacherModel",
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

        loggingService.logWarn("Rate limit exceeded for GET API: '/teachers/" + userId + "' by user: " + 
        (currentUser != null ? currentUser.getUsername() : "anonymous") + 
        " from IP: " + ipAddress, logContext);

        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage("response.error.rateLimitExceeded", 
        new Object[]{retryAfterSeconds}, locale);
        throw new TooManyRequestsExceptionHandle(
        message,
        "TeacherModel",
        retryAfterSeconds
        );
        }

        public ResponseEntity<Response<List<EntityModel>>> getStudentsByClassManagingRateLimitFallback(
        @PathVariable("classManaging") String classManaging,
        @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
        @RequestHeader(value = "Authorization") String authHeader,
        @CurrentUser UserDto currentUser,
        Throwable ex) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getStudentsByClassManagingRateLimitFallback");

        // Lấy HttpServletRequest từ RequestContextHolder vì Resilience4j không inject nó vào fallback
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";

        securityService.sendIpViolation(ipAddress);

        loggingService.logWarn("Rate limit exceeded for GET API: '/teachers/" + classManaging + "/students' by user: " + 
        (currentUser != null ? currentUser.getUsername() : "anonymous") + 
        " from IP: " + ipAddress, logContext);

        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage("response.error.rateLimitExceeded", 
        new Object[]{retryAfterSeconds}, locale);
        
        throw new TooManyRequestsExceptionHandle(
        message,
        "TeacherModel",
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

        // Lấy HttpServletRequest từ RequestContextHolder vì Resilience4j không inject nó vào fallback
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";
        // Track IP violation (tăng counter và auto-block nếu vượt threshold)
        securityService.sendIpViolation(ipAddress);

        loggingService.logWarn("Rate limit exceeded for PUT API: '/teachers/" + id + "' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);


        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage("response.error.rateLimitExceeded", 
            new Object[]{retryAfterSeconds}, locale);
        throw new TooManyRequestsExceptionHandle(
            message,
            "TeacherModel",
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

        loggingService.logWarn("Rate limit exceeded for DELETE API: '/teachers' by user: " + 
            (currentUser != null ? currentUser.getUsername() : "anonymous") + 
            " from IP: " + ipAddress, logContext);

        Long retryAfterSeconds = 60L;
        String message = messageSource.getMessage("response.error.rateLimitExceeded", 
            new Object[]{retryAfterSeconds}, locale);
        throw new TooManyRequestsExceptionHandle(
            message,
            "TeacherModel",
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
            loggingService.logWarn("Rate limit exceeded for GET API: '/teachers/paged' by user: " + 
                (currentUser != null ? currentUser.getUsername() : "anonymous") + 
                " from IP: " + ipAddress, logContext);

            Long retryAfterSeconds = 60L;
            String message = messageSource.getMessage("response.error.rateLimitExceeded", 
                new Object[]{retryAfterSeconds}, locale);
            throw new TooManyRequestsExceptionHandle(
                message,
                "TeacherModel",
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
            @RequestParam(required = false) String classManaging,
            @RequestParam(required = false) String department,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser,
            Throwable ex) {
            Locale locale = Locale.forLanguageTag(acceptLanguage);
            LogContext logContext = getLogContext("filterRateLimitFallback");

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
            String ipAddress = request != null ? IpAddressUtils.getClientIpAddress(request) : "unknown";

            securityService.sendIpViolation(ipAddress);
            loggingService.logWarn("Rate limit exceeded for GET API: '/teachers/filter' by user: " + 
                (currentUser != null ? currentUser.getUsername() : "anonymous") + 
                " from IP: " + ipAddress, logContext);

            Long retryAfterSeconds = 60L;
            String message = messageSource.getMessage("response.error.rateLimitExceeded", 
                new Object[]{retryAfterSeconds}, locale);
            throw new TooManyRequestsExceptionHandle(
                message,
                "TeacherModel",
                retryAfterSeconds
            );
        }
}