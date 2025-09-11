package com.common.QLSV.configurations;

import com.common.QLSV.services.JwtValidationService;
import com.common.models.UserDto;
import com.handle_exceptions.UnauthorizedExceptionHandle;
import com.handle_exceptions.ForbiddenExceptionHandle;
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

import java.util.Arrays;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtValidationService jwtValidationService;
    
    @Autowired
    @Lazy
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Override
    public boolean preHandle(HttpServletRequest request
            , HttpServletResponse response
            , Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresJwt requiresJwt = handlerMethod.getMethodAnnotation(RequiresJwt.class);

        if (requiresJwt == null) {
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                    "Missing @RequiresJwt annotation", 
                    "This endpoint requires JWT authentication. Please add @RequiresJwt annotation to the method.");
            exceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                    "Missing or invalid Authorization header",
                    "Authorization header must start with 'Bearer '");
            exceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtValidationService.isTokenValid(token)) {
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle("Invalid or expired token", "JWT token validation failed");
            exceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        UserDto user = jwtValidationService.getUserFromToken(token);
        if (user == null) {
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle("User not found", "User associated with token not found in database");
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
                ForbiddenExceptionHandle exception = new ForbiddenExceptionHandle(
                    "Insufficient privileges", 
                    "User role '" + user.getRole() + "' does not have required permissions",
                    requiredRoles
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