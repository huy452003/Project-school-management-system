package com.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.kafka_shared.services.KafkaProducerService;
import com.kafka_shared.models.UserEvent;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;
import java.util.Set;
import com.model_shared.enums.Role;
import com.model_shared.enums.Permission;
import com.model_shared.models.user.UserDto;

@Service
public class AsyncService {
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private LoggingService loggingService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @Async("loginEventExecutor") 
    public void sendLoginEvent(UserDetails userDetails) {
        try {
            
            Role role = Role.valueOf(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.contains("_"))  // ← Tìm authority đơn giản như "STUDENT"
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User role not found")));

            Set<Permission> permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.contains("_"))
                .map(Permission::valueOf)
                .collect(Collectors.toSet());
            
            UserDto user = new UserDto();
            user.setUserName(userDetails.getUsername());
            user.setRole(role);
            user.setPermissions(permissions);
            UserEvent loginEvent = UserEvent.userLogin(user);

            kafkaProducerService.sendUserEvent(loginEvent);

            loggingService.logInfo("Login event sent successfully for user: " + userDetails.getUsername(), 
                getLogContext("sendLoginEvent"));
        } catch (Exception e) { 
            loggingService.logError("Failed to send login event for user: " + userDetails.getUsername(), 
                e, getLogContext("sendLoginEvent"));
        }

    }

}
