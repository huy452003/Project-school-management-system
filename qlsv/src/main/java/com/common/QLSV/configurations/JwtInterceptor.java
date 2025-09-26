package com.common.QLSV.configurations;

import com.common.QLSV.services.JwtValidationService;
import com.common.models.UserDto;
import com.handle_exceptions.UnauthorizedExceptionHandle;
import com.handle_exceptions.ForbiddenExceptionHandle;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import java.util.List;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtValidationService jwtValidationService;

    @Autowired
    private LoggingService loggingService;
    
    @Autowired
    @Lazy
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlsv")
                .className(this.getClass().getName())
                .methodName(methodName)
                .build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request
            , HttpServletResponse response
            , Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        LogContext logContext = getLogContext("preHandle");

        String requestURI = request.getRequestURI();
        if (requestURI.contains("/public/") || requestURI.contains("/public") || requestURI.contains("/error/")) {
            loggingService.logDebug("Public request calling...", logContext);
            return true; 
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresJwt requiresJwt = handlerMethod.getMethodAnnotation(RequiresJwt.class);

        if (requiresJwt == null) {
            loggingService.logDebug("Missing @RequiresJwt annotation", logContext);
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                    "Missing @RequiresJwt annotation", 
                    "This endpoint requires JWT authentication. Please add @RequiresJwt annotation to the method.");
            exceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            loggingService.logDebug("Missing or invalid Authorization header", logContext);
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                    "Missing or invalid Authorization header",
                    "Authorization header must start with 'Bearer '");
            exceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        String jwt = authHeader.substring(7);
        UserDto user = jwtValidationService.getUserFromTokenAndValidate(jwt); 

        if (user == null) {
            loggingService.logDebug("Token may be invalid, expired, or blacklisted", logContext);
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                "Token validation failed", 
                "Token may be invalid, expired, or blacklisted"
            );
            exceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        // Check roles if specified
        if (requiresJwt.roles().length > 0) {
            boolean hasRequiredRole = false;
            for (String role : requiresJwt.roles()) {
                if (user.getRole().equals(role)) {
                    hasRequiredRole = true;
                    break;
                }
            }
            
            if (!hasRequiredRole) {
                String requiredRoles = Arrays.toString(requiresJwt.roles());
                
                loggingService.logDebug("User role '" + user.getRole()
                + "' does not have required role: " + requiredRoles, logContext);

                ForbiddenExceptionHandle exception = new ForbiddenExceptionHandle(
                    "Insufficient role privileges", 
                    "User role '" + user.getRole() + "' does not have required role",
                    requiredRoles
                );
                exceptionResolver.resolveException(request, response, handler, exception);
                return false;
            }
        }

        // Check permissions if specified
        if (requiresJwt.permissions().length > 0) {
            boolean hasRequiredPermission = false;
            List<String> userPermissions = user.getPermissions(); // Lấy từ UserDto thay vì parse token
            
            for (String permission : requiresJwt.permissions()) {
                if (userPermissions != null && userPermissions.contains(permission)) {
                    hasRequiredPermission = true;
                    break;
                }
            }
            
            if (!hasRequiredPermission) {
                String requiredPermissions = Arrays.toString(requiresJwt.permissions());
                
                loggingService.logDebug("User permissions " + userPermissions
                + " do not include required permissions: " + requiredPermissions, logContext);

                ForbiddenExceptionHandle exception = new ForbiddenExceptionHandle(
                    "Insufficient permission privileges", 
                    "User does not have required permissions",
                    requiredPermissions
                );
                exceptionResolver.resolveException(request, response, handler, exception);
                return false;
            }
        }

        // Add user info to request attributes for use in controllers
        request.setAttribute("currentUser", user);
        return true;
    }
    
} 