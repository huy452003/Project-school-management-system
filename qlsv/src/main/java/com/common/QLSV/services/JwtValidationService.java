package com.common.QLSV.services;

import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.security.entities.Role;
import com.security.services.JwtService;
import com.security.entities.UserEntity;
import com.common.models.Response;
import com.common.models.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;

@Service
public class JwtValidationService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    LoggingService loggingService;

    @Value("http://localhost:8082")
    private String securityBaseUrl;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("QLSV")
                .className(this.getClass().getName())
                .methodName(methodName)
                .build();
    }

    public UserDto getUserFromToken(String token) {
        LogContext logContext = getLogContext("getUserFromToken");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);

            loggingService.logDebug("Calling security module with URL : " + securityBaseUrl + "/auth/validate"
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
                    + response.getStatusCode()
                    + "Body: "+response.getBody(), logContext);
            return null;
        } catch (Exception e) {
            loggingService.logError("Exception during getUserFromToken: ", e, logContext);
            return null;
        }
    }

    public UserDto getUserFromTokenAndValidate(String token) {
        LogContext logContext = getLogContext("getUserFromTokenAndValidate");
        try {
            // 1. Kiểm tra cơ bản JWT
            String username = jwtService.extractUsername(token);
            loggingService.logDebug("Extracted username from JWT: " + username, logContext);
    
            if (username == null) {
                loggingService.logWarn("Failed to extract username from JWT token", logContext);
                return null;
            }
    
            if (jwtService.isTokenExpired(token)) {
                loggingService.logWarn("JWT token is expired for user: " + username, logContext);
                return null;
            }
    
            // 2. Gọi API security để lấy user
            UserDto user = getUserFromToken(token);
    
            if (user == null) {
                loggingService.logWarn("Failed to get user from token for username: " + username, logContext);
                return null;
            }
    
            // 3. Validate token với user
            UserEntity userEntity = UserEntity.builder()
                    .userName(user.getUserName())
                    .role(Role.valueOf(user.getRole()))
                    .build();
    
            boolean isValid = jwtService.isTokenValid(token, userEntity);
            loggingService.logDebug("Final JWT validation result for user " + username + " is " + isValid, logContext);
            
            return isValid ? user : null;
        } catch (Exception e) {
            loggingService.logError("Exception during JWT validation: ", e, logContext);
            return null;
        }
    }

} 