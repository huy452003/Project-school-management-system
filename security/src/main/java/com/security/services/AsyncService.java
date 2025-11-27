package com.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.kafka_shared.services.KafkaProducerService;
import com.kafka_shared.models.UserEvent;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.model_shared.models.user.UserDto;
import com.security.models.SecurityResponse;

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
    public void sendLoginEvent(SecurityResponse securityResponse) {
        try {
            UserDto user = new UserDto();
            user.setUserId(securityResponse.getUserId());
            user.setUsername(securityResponse.getUsername());
            user.setRole(securityResponse.getRole());
            user.setPermissions(securityResponse.getPermissions());
            UserEvent loginEvent = UserEvent.userLogin(user);

            kafkaProducerService.sendUserEvent(loginEvent);

            loggingService.logInfo("Login event sent successfully for user: " + securityResponse.getUsername(), 
                getLogContext("sendLoginEvent"));
        } catch (Exception e) { 
            loggingService.logError("Failed to send login event for user: " + 
                (securityResponse != null ? securityResponse.getUsername() : "unknown"), 
                e, getLogContext("sendLoginEvent"));
        }

    }

}
