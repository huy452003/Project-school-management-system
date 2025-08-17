package com.security.controllers;

import com.common.models.Response;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.security.models.Login;
import com.security.models.Register;
import com.security.models.SecurityResponse;
import com.security.services.AuthService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Locale;
import com.security.repositories.UserRepo;

@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    LoggingService loggingService;
    @Autowired
    private UserRepo userRepo;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security")
                .className("AuthController")
                .methodName(methodName)
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<Response<SecurityResponse>> register(
            @Valid @RequestBody Register request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){    
        log.info("Registration attempt for username: {}", request.getUsername());
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        
        try {
            SecurityResponse securityResponse = authService.register(request);
            Response<SecurityResponse> response = new Response<>(
                    200,
                    "User registered successfully",
                    "Authentication",
                    null,
                    securityResponse
            );
            log.info("User registered successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration failed for username: {}, error: {}", request.getUsername(), e.getMessage());
            throw e; // Let global exception handler deal with it
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response<SecurityResponse>> login(
            @Valid @RequestBody Login request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        log.info("Login attempt for username: {}", request.getUsername());
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        
        try {
            SecurityResponse securityResponse = authService.login(request);
            Response<SecurityResponse> response = new Response<>(
                    200,
                    "User logged in successfully",
                    "Authentication",
                    null,
                    securityResponse
            );
            log.info("User logged in successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for username: {}, error: {}", request.getUsername(), e.getMessage());
            throw e; // Let global exception handler deal with it
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response<SecurityResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
    ){
        log.info("Refresh token request");
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        
        try {
            SecurityResponse securityResponse = authService.refreshToken(authHeader);
            Response<SecurityResponse> response = new Response<>(
                    200,
                    "Token refreshed successfully",
                    "Authentication",
                    null,
                    securityResponse
            );
            log.info("Token refreshed successfully for user: {}", securityResponse.getUserName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw e; // Let global exception handler deal with it
        }
    }
}
