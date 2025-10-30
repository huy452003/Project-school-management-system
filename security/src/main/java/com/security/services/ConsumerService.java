package com.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.security.repositories.UserRepo;
import com.security.entities.UserEntity;
import com.model_shared.enums.Status;
import com.kafka_shared.models.UserEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.Acknowledgment;    
import org.springframework.transaction.annotation.Transactional;
import com.model_shared.enums.Type;

@Service
public class ConsumerService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    LoggingService loggingService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }
    
    @KafkaListener(topics = "profile-events")
    @Transactional
    public void handleUserCreateWithProfile(@Payload UserEvent profile,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                               @Header(KafkaHeaders.OFFSET) long offset,
                               Acknowledgment acknowledgment) {
        LogContext logContext = getLogContext("handleUserCreateWithProfile");
        
        try {
            if(profile.getUser().getType().equals(Type.STUDENT)){
                UserEntity user = userRepo.findByUserId(profile.getUser().getUserId())
                    .orElse(null);
                
                if (user == null) {
                    loggingService.logWarn(
                        "User not found in profile-events handler, userId: " + profile.getUser().getUserId() + 
                        ". User may have been deleted. Skipping.", 
                        logContext
                    );
                    acknowledgment.acknowledge();
                    return;
                }
                
                user.setStatus(Status.ENABLED);
                userRepo.save(user);
                
                loggingService.logInfo(
                    "Successfully processed user create profile with user id: " + profile.getUser().getUserId()
                    , logContext);
                
                // TODO: Gửi notification đến client (WebSocket/SSE)
                // Notification: "Tài khoản đã được kích hoạt thành công!"
                    
            } else {
                loggingService.logInfo(
                    "User is not a student, skipping with user id: " + profile.getUser().getUserId()
                    , logContext);
            }
            
            // Acknowledge nếu xử lý thành công
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            loggingService.logError(
                "Failed to handle user create profile with user id: " + profile.getUser().getUserId()
                , e
                , logContext);
            // Throw exception để trigger retry cho profile-events
            // KHÔNG acknowledge → Kafka sẽ retry
            throw e;
        }
    }

    @KafkaListener(topics = "user-events-dlq")
    @Transactional
    public void handleUserEventDLQ(@Payload UserEvent userEvent,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Header(KafkaHeaders.OFFSET) long offset,
                                   Acknowledgment acknowledgment) {
        LogContext logContext = getLogContext("handleUserEventDLQ"); 
        try {
            if(userEvent.getUser().getType().equals(Type.STUDENT)){
                UserEntity user = userRepo.findByUserId(userEvent.getUser().getUserId())
                    .orElse(null);
                
                if (user == null) {
                    loggingService.logWarn(
                        "User not found in DLQ handler, userId: " + userEvent.getUser().getUserId() + 
                        ". User may have been deleted. Skipping.", 
                        logContext
                    );
                    acknowledgment.acknowledge();
                    return;
                }
                
                user.setStatus(Status.FAILED);
                userRepo.save(user);
                
                loggingService.logInfo(
                    "Successfully processed user event DLQ and set status to FAILED for user id: " 
                        + userEvent.getUser().getUserId(), logContext);
                
                // TODO: Gửi notification đến client (WebSocket/SSE)
                // Notification: "Đăng ký thất bại. Vui lòng thử lại!"
                        
            } else {
                loggingService.logInfo(
                    "User is not a student, skipping rollback with user id: " + userEvent.getUser().getUserId()
                    , logContext);
            }
            
        } catch (Exception e) {
            loggingService.logError(
                "Failed to handle user event DLQ with user id: " + userEvent.getUser().getUserId() + 
                ". Acknowledging message to prevent infinite loop!", 
                e, 
                logContext
            );
        } finally {
            // ALWAYS acknowledge, dù có lỗi hay không
            // Để tránh vòng lặp vô tận
            acknowledgment.acknowledge();
        }
    }

}
