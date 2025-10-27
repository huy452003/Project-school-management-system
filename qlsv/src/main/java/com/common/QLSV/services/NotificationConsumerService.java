package com.common.QLSV.services;

import com.kafka_shared.models.NotificationMessage;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumerService {

    @Autowired
    private LoggingService loggingService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlsv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @KafkaListener(topics = "notifications")
    public void handleNotification(@Payload NotificationMessage notification,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  @Header(KafkaHeaders.OFFSET) long offset,
                                  Acknowledgment acknowledgment) {
        
        LogContext logContext = getLogContext("handleNotification");
        
        try {
            // Chỉ xử lý notifications liên quan đến student events từ QLSV
            if (isRelevantNotification(notification)) {
                logNotificationFeedback(notification, logContext);
            }
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            loggingService.logError("Failed to process notification", e, logContext);
        }
    }

    
    // Kiểm tra notification có liên quan đến QLSV không
    private boolean isRelevantNotification(NotificationMessage notification) {
        return "studentEvent".equals(notification.getEntityType()) && 
               "qlsv".equals(notification.getSource());
    }

    private void logNotificationFeedback(NotificationMessage notification, LogContext logContext) {
        switch (notification.getType()) {
            case "SUCCESS":
                loggingService.logInfo(        
                    String.format("✅ Student event processed successfully: %s (ID: %s) by %s - Event ID: %s", 
                        notification.getEntityDisplayName(), 
                        notification.getEntityId(), 
                        notification.getModuleName(),
                        notification.getOriginalEventId()),
                    logContext
                );
                break;
                
            case "FAILURE":
                loggingService.logError(
                    String.format("❌ Student event processing failed: %s (ID: %s) by %s - Error: %s - Event ID: %s", 
                        notification.getEntityDisplayName(), 
                        notification.getEntityId(), 
                        notification.getModuleName(),
                        notification.getErrorMessage(),
                        notification.getOriginalEventId()),
                    new Exception("Event processing failed"),
                    logContext
                );
                break;
                
            case "WARNING":
                loggingService.logWarn(
                    String.format("⚠️ Student event warning: %s (ID: %s) by %s - Event ID: %s", 
                        notification.getEntityDisplayName(), 
                        notification.getEntityId(), 
                        notification.getModuleName(),
                        notification.getOriginalEventId()),
                    logContext
                );
                break;
                
            case "INFO":
                loggingService.logInfo(
                    String.format("ℹ️ Student event info: %s (ID: %s) by %s - Event ID: %s", 
                        notification.getEntityDisplayName(), 
                        notification.getEntityId(), 
                        notification.getModuleName(),
                        notification.getOriginalEventId()),
                    logContext
                );
                break;
                
            default:
                loggingService.logWarn("Unknown notification type: " + notification.getType(), logContext);
        }
    }
}