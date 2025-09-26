package com.common.QLGV.configurations;

import com.common.QLGV.services.JwtValidationService;
import com.common.models.UserDto;
import com.handle_exceptions.ForbiddenExceptionHandle;
import com.handle_exceptions.UnauthorizedExceptionHandle;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    JwtValidationService jwtValidationService;

    @Autowired
    private LoggingService loggingService;

    @Autowired
    @Lazy
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getName())
                .methodName(methodName)
                .build();
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        LogContext logContext = getLogContext("preHandle");

        String requestURI = request.getRequestURI();
        if (requestURI.contains("/public/") || requestURI.contains("/public") || requestURI.contains("/error/")) {
            return true; 
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresJwt requiresJwt = handlerMethod.getMethodAnnotation(RequiresJwt.class);
        if (requiresJwt == null) {
            loggingService.logDebug("Missing @RequiresJwt annotation", logContext);
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                    "Missing @RequiresJwt annotation",
                    "This endpoint requires JWT authentication. Please add @RequiresJwt annotation to the method.");
            handlerExceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            loggingService.logDebug("Missing or invalid Authorization header", logContext);
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                    "Missing or invalid Authorization header",
                    "Authorization header must start with 'Bearer '");
            handlerExceptionResolver.resolveException(request,response,handler,exception);
            return false;
        }

        String jwt = authHeader.substring(7);
        
        UserDto userDto = jwtValidationService.getUserFromTokenAndValidate(jwt); 

        if (userDto == null) {
            loggingService.logDebug("Token validation failed - invalid, expired, or blacklisted", logContext);
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                "Token validation failed", 
                "Token may be invalid, expired, or blacklisted"
            );
            handlerExceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        // Check roles if specified
        if (requiresJwt.roles().length > 0){
            boolean hasRequiredRole = false;
            for (String role : requiresJwt.roles()) {
                if (userDto.getRole().equals(role)) {
                    hasRequiredRole = true;
                    break;
                }
            }
            if (!hasRequiredRole) {
                String requiredRoles = Arrays.toString(requiresJwt.roles());

                loggingService.logDebug("User role '" + userDto.getRole()
                + "' does not have required role: " + requiredRoles, logContext);
                
                ForbiddenExceptionHandle exception = new ForbiddenExceptionHandle(
                        "Insufficient role privileges",
                        "User role '" + userDto.getRole() + "' does not have required role",
                        requiredRoles
                );
                handlerExceptionResolver.resolveException(request, response, handler, exception);
                return false;
            }
        }

        // Check permissions if specified
        if (requiresJwt.permissions().length > 0) {
            boolean hasRequiredPermission = false;
            List<String> userPermissions = userDto.getPermissions(); // Lấy từ UserDto
            
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
                handlerExceptionResolver.resolveException(request, response, handler, exception);
                return false;
            }
        }
        request.setAttribute("currentUser", userDto);
        return true;
    }
}
