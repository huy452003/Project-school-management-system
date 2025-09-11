package com.common.QLGV.configurations;

import com.common.QLGV.services.JwtValidationService;
import com.common.models.UserDto;
import com.handle_exceptions.ForbiddenExceptionHandle;
import com.handle_exceptions.UnauthorizedExceptionHandle;
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

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    JwtValidationService jwtValidationService;

    @Autowired
    @Lazy
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresJwt requiresJwt = handlerMethod.getMethodAnnotation(RequiresJwt.class);
        if (requiresJwt == null) {
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                    "Missing @RequiresJwt annotation",
                    "This endpoint requires JWT authentication. Please add @RequiresJwt annotation to the method.");
            handlerExceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                    "Missing or invalid Authorization header",
                    "Authorization header must start with 'Bearer '");
            handlerExceptionResolver.resolveException(request,response,handler,exception);
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtValidationService.isValidToken(token)){
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle(
                    "User not found",
                    "User associated with token not found in database");
            handlerExceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

        UserDto userDto = jwtValidationService.getUserFromToken(token);
        if (userDto == null) {
            UnauthorizedExceptionHandle exception = new UnauthorizedExceptionHandle("User not found", "User associated with token not found in database");
            handlerExceptionResolver.resolveException(request, response, handler, exception);
            return false;
        }

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
                ForbiddenExceptionHandle exception = new ForbiddenExceptionHandle(
                        "Insufficient privileges",
                        "User role '" + userDto.getRole() + "' does not have required permissions",
                        requiredRoles
                );
                handlerExceptionResolver.resolveException(request, response, handler, exception);
                return false;
            }
        }
        request.setAttribute("currentUser", userDto);
        return true;
    }
}
