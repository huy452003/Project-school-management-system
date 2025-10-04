package com.security_shared.aspects;

import com.security_shared.annotations.RequiresAuth;
import com.security_shared.annotations.CurrentUser;
import com.security_shared.services.SecurityService;
import com.model_shared.models.UserDto;
import com.handle_exceptions.UnauthorizedExceptionHandle;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@Order(1) // High priority để chạy trước các aspect khác
public class RequiresAuthAspect {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private LoggingService loggingService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security_shared")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @Around("@annotation(com.security_shared.annotations.RequiresAuth)")
    public Object validateAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        LogContext logContext = getLogContext("validateAuth");
        
        // Lấy HttpServletRequest
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            loggingService.logError("Cannot get HttpServletRequest from RequestContextHolder", null, logContext);
            throw new UnauthorizedExceptionHandle("Cannot get request context", "Request context not available");
        }
        
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        
        // Lấy annotation
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresAuth requiresAuth = method.getAnnotation(RequiresAuth.class);
        
        if (requiresAuth == null || !requiresAuth.requireAuth()) {
            // Không cần authentication, tiếp tục
            return joinPoint.proceed();
        }
        
        // Validate và authorize
        UserDto currentUser = securityService.validateAndAuthorize(
            authHeader,
            requiresAuth.roles(),
            requiresAuth.permissions()
        );
        
        // Inject currentUser vào method parameters
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(CurrentUser.class)) {
                args[i] = currentUser;
                break;
            }
        }
        
        loggingService.logDebug("User " + currentUser.getUserName() + " successfully authorized for method: " + method.getName(), logContext);
        
        // Tiếp tục thực hiện method với args đã được inject
        return joinPoint.proceed(args);
    }
}
