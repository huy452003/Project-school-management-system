package com.security.controllers;

import com.model_shared.models.Response;
import com.model_shared.models.user.UserDto;
import com.model_shared.enums.Status;
import com.handle_exceptions.NotFoundExceptionHandle;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.security.entities.UserEntity;
import com.security.models.Login;
import com.security.models.Register;
import com.security.models.SecurityResponse;
import com.security.models.TokenInfo;
import com.security.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import com.model_shared.models.user.UpdateUserDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.security.repositories.UserRepo;

@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;
    
    @Autowired
    private ModelMapper modelMapper;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<Response<SecurityResponse>> register(
            @Valid @RequestBody Register request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        Locale locale = Locale.forLanguageTag(acceptLanguage);

        LogContext logContext = getLogContext("register");
        logContext.setUserId(request.getUsername());

        loggingService.logInfo("Register API Calling... by user: " + logContext.getUserId(), logContext);
        
        SecurityResponse securityResponse = authService.register(request);
        Response<SecurityResponse> response = new Response<>(
                200,
                messageSource.getMessage("response.message.registerSuccess", null, locale),
                "Security-Model",
                null,
                securityResponse
        );
        loggingService.logInfo("User registered successfully: " + logContext.getUserId(), logContext);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response<SecurityResponse>> login(
            @Valid @RequestBody Login request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("login");
        logContext.setUserId(request.getUsername());

        loggingService.logInfo("Login API calling... by user: " + logContext.getUserId(), logContext);
        SecurityResponse securityResponse = authService.login(request);
        Response<SecurityResponse> response = new Response<>(
                200,
                messageSource.getMessage("response.message.loginSuccess",null,locale),
                "Security-Model",
                null,
                securityResponse
        );
        loggingService.logInfo("User logged in successfully: " + logContext.getUserId(), logContext);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<Map<String, Object>>> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("logout");

        loggingService.logInfo("Logout API calling...", logContext);
        
        String token = authHeader.substring(7);
        Map<String, Object> logoutResponse = authService.logout(token);

        Response<Map<String, Object>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.logoutSuccess", null, locale),
                "Security-Model",
                null,
                logoutResponse
        );

        loggingService.logInfo("User logged out from all devices successfully: " + logoutResponse.get("username")
        + " at timestamp: " + logoutResponse.get("timestamp"), logContext);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response<SecurityResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        Locale locale = Locale.forLanguageTag(acceptLanguage);

        LogContext logContext = getLogContext("refreshToken");

        loggingService.logInfo("refreshToken API Calling...", logContext);
        SecurityResponse securityResponse = authService.refreshToken(authHeader);
        Response<SecurityResponse> response = new Response<>(
                200,
                messageSource.getMessage("response.message.refreshTokenSuccess",null,locale),
                "Security-Model",
                null,
                securityResponse
        );
        loggingService.logInfo("refreshToken successfully", logContext);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Response<UserDto>> validateToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        Locale locale = Locale.forLanguageTag(acceptLanguage);

        LogContext logContext = getLogContext("validate");

        loggingService.logInfo("validate API Calling...", logContext);
        String token = authHeader.substring(7); 

            String username = authService.getUsernameFromToken(token);
            UserEntity user = userRepo.findByUsername(username).orElseThrow(
                    () -> new NotFoundExceptionHandle("", List.of(username), "Security-Model")
            );

            // Convert using ModelMapper
            UserDto userDto = modelMapper.map(user, UserDto.class);

            Response<UserDto> response = new Response<>(
                    200,
                    messageSource.getMessage("response.message.validateSuccess",null,locale),
                    "Security-Model",
                    null,
                    userDto
            );

            loggingService.logInfo("Token validated successfully for user: " + username, logContext);
            return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/decode")
    public ResponseEntity<Response<TokenInfo>> decodeToken(
        @RequestHeader("Authorization") String authHeader,
        @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("decodeToken");

        loggingService.logInfo("decodeToken API Calling...", logContext);
        String token = authHeader.substring(7);

        TokenInfo tokenInfo = authService.decodeToken(token);
        Response<TokenInfo> response = new Response<>(
                200,
                messageSource.getMessage("response.message.decodeTokenSuccess", null, locale),
                "Security-Model",
                null,
                tokenInfo
        );

        loggingService.logInfo("Token decoded successfully", logContext);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @PostMapping("/internal/users/batch")
    public ResponseEntity<List<UserDto>> getUsersByIds(
            @RequestBody Map<String, List<Integer>> request
    ) {
        LogContext logContext = getLogContext("getUsersByIds");
        
        List<Integer> userIds = request.get("ids");
        
        if (userIds == null || userIds.isEmpty()) {
            loggingService.logWarn("Empty userIds list in batch request", logContext);
            return ResponseEntity.ok(List.of());
        }
        
        loggingService.logInfo("Batch getting users, count: " + userIds.size(), logContext);
        
        // Lấy tất cả users theo IDs
        List<UserEntity> entities = userRepo.findAllById(userIds);
        
        // Convert sang UserDto using ModelMapper
        List<UserDto> userDtos = entities.stream()
                .map(entity -> modelMapper.map(entity, UserDto.class))
                .collect(Collectors.toList());
        
        loggingService.logInfo("Successfully retrieved " + userDtos.size() + " users", logContext);
        
        // Internal API: Trả về data trực tiếp, không cần Response wrapper
        return ResponseEntity.ok(userDtos);
    }

    @PostMapping("/internal/users/update")
    public ResponseEntity<UserDto> updateUser(
        @Valid @RequestBody UpdateUserDto updateUserDto
    ){
        LogContext logContext = getLogContext("updateUser");
        
        loggingService.logInfo("updateUser API Calling... for userId: " + updateUserDto.getUserId(), logContext);
        
        UserEntity userEntity = userRepo.findById(updateUserDto.getUserId()).orElseThrow(
                () -> new NotFoundExceptionHandle("", List.of(updateUserDto.getUserId().toString()), "Security-Model")
        );
        
        // Check và log warning nếu user đang DISABLED (cho phép update nhưng có warning)
        if (userEntity.getStatus().equals(Status.DISABLED)) {
            loggingService.logWarn("Updating user with DISABLED status for userId: " + updateUserDto.getUserId() 
                + ". User is disabled but update is allowed.", logContext);
        }
        
        userEntity.setFirstName(updateUserDto.getFirstName());
        userEntity.setLastName(updateUserDto.getLastName());
        userEntity.setAge(updateUserDto.getAge());
        userEntity.setGender(updateUserDto.getGender());
        userEntity.setBirth(updateUserDto.getBirth());
        
        userRepo.save(userEntity);
        loggingService.logInfo("Updated user profile for userId: " + updateUserDto.getUserId(), logContext);
        
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/internal/users/delete")
    public ResponseEntity<List<Integer>> deleteUser(
        @RequestBody List<Integer> req
    ){
        LogContext logContext = getLogContext("deleteUser");

        Set<Integer> uniqueUserIds = new LinkedHashSet<>();
        List<Integer> duplicates = new ArrayList<>();
        for (Integer userId : req) {
            if (!uniqueUserIds.add(userId)) {
                duplicates.add(userId);
            }
        }
        if (!duplicates.isEmpty()) {
            loggingService.logWarn("Duplicate userIds detected in input: " + duplicates + ". They will be processed only once.", logContext);
        }

        List<Integer> userIds = new ArrayList<>(uniqueUserIds);

        for (Integer userId : userIds) {
            UserEntity userEntity = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundExceptionHandle("", List.of(userId.toString()), "Security-Model")
            );
            userRepo.delete(userEntity);
            loggingService.logInfo("Deleted user with id: " + userId, logContext);
        }
        
        return ResponseEntity.ok(userIds);
    }

    @PatchMapping("/internal/users/disable")
    public ResponseEntity<Response<List<Integer>>> disableUsers(
        @RequestBody List<Integer> req,
        @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        LogContext logContext = getLogContext("disableUsers");
        Locale locale = Locale.forLanguageTag(acceptLanguage);

        Set<Integer> uniqueUserIds = new LinkedHashSet<>();
        List<Integer> duplicates = new ArrayList<>();
        for (Integer userId : req) {
            if (!uniqueUserIds.add(userId)) {
                duplicates.add(userId);
            }
        }
        
        if (!duplicates.isEmpty()) {
            loggingService.logWarn("Duplicate userIds detected in input: " + duplicates + ". They will be processed only once.", logContext);
        }
        
        List<Integer> userIds = new ArrayList<>(uniqueUserIds);

        for (Integer userId : userIds) {
            UserEntity userEntity = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundExceptionHandle("", List.of(userId.toString()), "Security-Model")
            );
            
            // Check nếu user đã DISABLED
            if (userEntity.getStatus() == Status.DISABLED) {
                loggingService.logWarn("User with id: " + userId + " is already DISABLED. Skipping (idempotent operation).", logContext);
                continue;
            }
            
            userEntity.setStatus(Status.DISABLED);
            userRepo.save(userEntity);
            loggingService.logInfo("Disabled user with id: " + userId, logContext);
        }
        
        Response<List<Integer>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.disableUsersSuccess", null, locale),
                "Security-Model",
                null,
                null
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/internal/users/enable")
    public ResponseEntity<Response<List<Integer>>> enableUsers(
        @RequestBody List<Integer> req,
        @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        LogContext logContext = getLogContext("enableUsers");
        Locale locale = Locale.forLanguageTag(acceptLanguage);

        Set<Integer> uniqueUserIds = new LinkedHashSet<>();
        List<Integer> duplicates = new ArrayList<>();
        for (Integer userId : req) {
            if (!uniqueUserIds.add(userId)) {
                duplicates.add(userId);
            }
        }
        
        if (!duplicates.isEmpty()) {
            loggingService.logWarn("Duplicate userIds detected in input: " + duplicates + ". They will be processed only once.", logContext);
        }
        
        List<Integer> userIds = new ArrayList<>(uniqueUserIds);

        for (Integer userId : userIds) {
            UserEntity userEntity = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundExceptionHandle("", List.of(userId.toString()), "Security-Model")
            );
            if (userEntity.getStatus() == Status.ENABLED) {
                loggingService.logWarn("User with id: " + userId + " is already ENABLED. Skipping (idempotent operation).", logContext);
                continue;
            }
            userEntity.setStatus(Status.ENABLED);
            userRepo.save(userEntity);
            loggingService.logInfo("Enabled user with id: " + userId, logContext);
        }

        Response<List<Integer>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.enableUsersSuccess", null, locale),
                "Security-Model",
                null,
                null
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping("/check-status")
    public ResponseEntity<Response<Map<String, Object>>> checkAccountStatus(
        @RequestHeader("Authorization") String authHeader,
        @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("checkAccountStatus");

        loggingService.logInfo("checkAccountStatus API Calling...", logContext);
        String token = authHeader.substring(7);
        
        String username = authService.getUsernameFromToken(token);
        UserEntity user = userRepo.findByUsername(username).orElseThrow(
                () -> new NotFoundExceptionHandle("", List.of(username), "Security-Model")
        );
        
        Map<String, Object> statusInfo = new HashMap<>();
        statusInfo.put("status", user.getStatus().name());
        statusInfo.put("username", user.getUsername());
        statusInfo.put("userId", user.getUserId());
        
        // Get status message from message source
        String statusMessageKey = switch (user.getStatus()) {
            case PENDING -> "response.status.pending";
            case ENABLED -> "response.status.enabled";
            case FAILED -> "response.status.failed";
            case DISABLED -> "response.status.disabled";
        };
        
        String statusMessage = messageSource.getMessage(statusMessageKey, null, locale);
        statusInfo.put("statusMessage", statusMessage);
        statusInfo.put("canLogin", user.getStatus() == com.model_shared.enums.Status.ENABLED);

        Response<Map<String, Object>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.checkStatusSuccess", null, locale),
                "Security-Model",
                null,
                statusInfo
        );

        loggingService.logInfo("Account status checked for user: " + username, logContext);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
