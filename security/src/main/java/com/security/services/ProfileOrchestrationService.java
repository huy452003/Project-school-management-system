package com.security.services;

import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.model_shared.models.user.UserDto;
import com.security.enums.Type;
import com.security.entities.UserEntity;
import com.security.models.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProfileOrchestrationService {

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${qlsv.base-url:http://localhost:8080}")
    private String qlsvBaseUrl;

    @Value("${qlgv.base-url:http://localhost:8081}")
    private String qlgvBaseUrl;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    public void createProfile(UserEntity user, Register request) {
        LogContext logContext = getLogContext("createProfile");
        logContext.setUserId(user.getUsername());

        Type userType = request.getType();
        
        if (userType == null) {
            loggingService.logWarn("User type is null, skipping profile creation", logContext);
            return;
        }

        loggingService.logInfo("Creating profile for type: " + userType, logContext);

        switch (userType) {
            case STUDENT:
                createStudentProfile(user, request, logContext);
                break;
            case TEACHER:
                createTeacherProfile(user, request, logContext);
                break;
            default:
                loggingService.logWarn("Unknown user type: " + userType, logContext);
        }
    }

    private void createStudentProfile(UserEntity user, Register request, LogContext logContext) {
        Map<String, Object> body = new HashMap<>();         
        
        // Extract graduate from profileData
        Boolean graduate = extractBooleanFromProfileData(request, "graduate", false);
        
        // Convert UserEntity to UserDto with profileData
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("graduate", graduate);
        
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUserName(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setAge(user.getAge());
        userDto.setGender(user.getGender());
        userDto.setBirth(user.getBirth());
        userDto.setRole(user.getRole().name());
        userDto.setPermissions(user.getPermissions().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        userDto.setEnabled(user.isEnabled());
        userDto.setProfileData(profileData);
        body.put("user", userDto);
        
        try {
            restTemplate.postForEntity(
                qlsvBaseUrl + "/students/internal/create-by-user-id", 
                body, 
                Void.class
            );
            loggingService.logInfo("Student profile created for userId: " + user.getUserId() + " with graduate: " + graduate, logContext);
        } catch (RestClientException e) {
            loggingService.logError("Failed to create student profile for userId: " + user.getUserId(), e, logContext);
            throw new RuntimeException("Failed to create student profile: " + e.getMessage(), e);
        }
    }

    private void createTeacherProfile(UserEntity user, Register request, LogContext logContext) {
        Map<String, Object> body = new HashMap<>();

        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUserName(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setAge(user.getAge());
        userDto.setGender(user.getGender());
        userDto.setBirth(user.getBirth());
        userDto.setRole(user.getRole().name());
        userDto.setPermissions(user.getPermissions().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        userDto.setEnabled(user.isEnabled());
        body.put("user", userDto);
        
        try {
            restTemplate.postForEntity(
                qlgvBaseUrl + "/teachers/internal/create-by-user-id", 
                body, 
                Void.class
            );
            loggingService.logInfo("Teacher profile created for userId: " + user.getUserId(), logContext);
        } catch (RestClientException e) {
            loggingService.logError("Failed to create teacher profile for userId: " + user.getUserId(), e, logContext);
            throw new RuntimeException("Failed to create teacher profile: " + e.getMessage(), e);
        }
    }

    private Boolean extractBooleanFromProfileData(Register request, String key, Boolean defaultValue) {
        if (request.getProfileData() == null || !request.getProfileData().containsKey(key)) {
            return defaultValue;
        }
        Object value = request.getProfileData().get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    private String extractStringFromProfileData(Register request, String key, String defaultValue) {
        if (request.getProfileData() == null || !request.getProfileData().containsKey(key)) {
            return defaultValue;
        }
        Object value = request.getProfileData().get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return defaultValue;
    }

}

