package com.security_shared.services;

import com.model_shared.models.Response;
import com.model_shared.models.UserDto;
import com.handle_exceptions.UnauthorizedExceptionHandle;
import com.handle_exceptions.ForbiddenExceptionHandle;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SecurityService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoggingService loggingService;

    @Value("${security.base-url:http://localhost:8083}")
    private String securityBaseUrl;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security_shared")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    /**
     * Validate JWT token và lấy thông tin user từ Security module
     * @param token JWT token
     * @return UserDto nếu token hợp lệ, null nếu không hợp lệ
     */
    public UserDto validateTokenAndGetUser(String token) {
        LogContext logContext = getLogContext("validateTokenAndGetUser");
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);

            loggingService.logDebug("Calling security module with URL: " + securityBaseUrl + "/auth/validate" 
                    + " to validate token", logContext);

            ResponseEntity<Response<UserDto>> response = restTemplate.exchange(
                securityBaseUrl + "/auth/validate",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Response<UserDto> responseBody = response.getBody();
                if (responseBody != null && responseBody.getData() != null) {
                    UserDto user = responseBody.getData();
                    loggingService.logDebug("Successfully retrieved user from security module: " 
                            + user.getUserName(), logContext);
                    return user;
                }
            }
            
            loggingService.logWarn("Failed to get valid response from security module. Status: " 
                    + response.getStatusCode() + " Body: " + response.getBody(), logContext);
            return null;
            
        } catch (Exception e) {
            loggingService.logError("Exception during token validation: ", e, logContext);
            return null;
        }
    }

    /**
     * Kiểm tra user có role cần thiết không
     * @param user UserDto
     * @param requiredRoles Danh sách roles cần thiết
     * @return true nếu user có ít nhất 1 role cần thiết
     */
    public boolean hasRequiredRole(UserDto user, String[] requiredRoles) {
        if (requiredRoles == null || requiredRoles.length == 0) {
            return true;
        }
        
        String userRole = user.getRole();
        for (String requiredRole : requiredRoles) {
            if (requiredRole.equals(userRole)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra user có permission cần thiết không
     * @param user UserDto
     * @param requiredPermissions Danh sách permissions cần thiết
     * @return true nếu user có ít nhất 1 permission cần thiết
     */
    public boolean hasRequiredPermission(UserDto user, String[] requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.length == 0) {
            return true;
        }
        
        List<String> userPermissions = user.getPermissions();
        if (userPermissions == null) {
            return false;
        }
        
        for (String requiredPermission : requiredPermissions) {
            if (userPermissions.contains(requiredPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate token và kiểm tra authorization
     * @param authHeader Authorization header
     * @param requiredRoles Roles cần thiết
     * @param requiredPermissions Permissions cần thiết
     * @return UserDto nếu authorized
     * @throws UnauthorizedExceptionHandle nếu không authorized
     */
    public UserDto validateAndAuthorize(String authHeader, String[] requiredRoles, String[] requiredPermissions) {
        LogContext logContext = getLogContext("validateAndAuthorize");
        
        // Kiểm tra Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            loggingService.logDebug("Missing or invalid Authorization header", logContext);
            throw new UnauthorizedExceptionHandle(
                    "Missing or invalid Authorization header",
                    "Authorization header must start with 'Bearer '");
        }

        String token = authHeader.substring(7);
        
        // Validate token và lấy user
        UserDto user = validateTokenAndGetUser(token);
        if (user == null) {
            loggingService.logDebug("Token validation failed - invalid, expired, or blacklisted", logContext);
            throw new UnauthorizedExceptionHandle(
                "Token validation failed", 
                "Token may be invalid, expired, or blacklisted");
        }

        // Kiểm tra roles
        if (!hasRequiredRole(user, requiredRoles)) {
            String requiredRolesStr = String.join(", ", requiredRoles);
            loggingService.logDebug("User role " + user.getRole() 
                    + " does not match required roles: " + requiredRolesStr, logContext);
            throw new ForbiddenExceptionHandle(
                "Insufficient role privileges", 
                "User does not have required role: " + requiredRolesStr);
        }

        // Kiểm tra permissions
        if (!hasRequiredPermission(user, requiredPermissions)) {
            String requiredPermissionsStr = String.join(", ", requiredPermissions);
            loggingService.logDebug("User permissions " + user.getPermissions() 
                    + " do not include required permissions: " + requiredPermissionsStr, logContext);
            throw new ForbiddenExceptionHandle(
                "Insufficient permission privileges", 
                "User does not have required permissions: " + requiredPermissionsStr);
        }

        loggingService.logDebug("User " + user.getUserName() + " successfully authorized", logContext);
        return user;
    }
}
