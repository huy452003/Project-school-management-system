package com.common.QLGV.services;

import com.kafka_shared.models.StudentEvent;
import com.kafka_shared.services.KafkaConsumerService;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class StudentConsumerService extends KafkaConsumerService<StudentEvent> {

    @Autowired
    private LoggingService loggingService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @KafkaListener(topics = "student-events")
    public void handleStudentEvent(@Payload StudentEvent studentEvent,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  @Header(KafkaHeaders.OFFSET) long offset,
                                  Acknowledgment acknowledgment) {

        handleEvent(studentEvent, topic, partition, offset, acknowledgment, "qlgv");
    }

    @Override
    protected void processEvent(StudentEvent event) {
        LogContext logContext = getLogContext("processEvent");
        
        switch (event.getAction()) {
            case "CREATED":
                handleStudentCreated(event);
                break;
            case "UPDATED":
                handleStudentUpdated(event);
                break;
            case "DELETED":
                handleStudentDeleted(event);
                break;
            default:
                loggingService.logWarn("Unknown student event action: " + event.getAction(), logContext);
                throw new IllegalArgumentException("Unknown event action: " + event.getAction());
        }
    }

    private void handleStudentCreated(StudentEvent studentEvent) {
        LogContext logContext = getLogContext("handleStudentCreated");
        loggingService.logInfo("Processing student CREATED event: " + studentEvent.getEntityDisplayName()
        + " (ID: " + studentEvent.getEntityId() + ")", logContext);
        
        try {
            clearStudentCacheData();
        
            loggingService.logInfo("Successfully processed student CREATED event for ID: " + studentEvent.getEntityId(), logContext);
            
        } catch (Exception e) {
            loggingService.logError("Failed to process student CREATED event for ID: " + studentEvent.getEntityId(), e, logContext);
            throw e; // Re-throw để trigger retry mechanism
        }
    }   

    private void handleStudentUpdated(StudentEvent studentEvent) {
        LogContext logContext = getLogContext("handleStudentUpdated");
        loggingService.logInfo("Processing student UPDATED event: " + studentEvent.getEntityDisplayName()
        + " (ID: " + studentEvent.getEntityId() + ")", logContext);
        
        try {
            clearStudentCacheData();
            
            loggingService.logInfo("Successfully processed student UPDATED event for ID: " + studentEvent.getEntityId(), logContext);
            
        } catch (Exception e) {
            loggingService.logError("Failed to process student UPDATED event for ID: " + studentEvent.getEntityId(), e, logContext);
            throw e; // Re-throw để trigger retry mechanism
        }
    }

    private void handleStudentDeleted(StudentEvent studentEvent) {
        LogContext logContext = getLogContext("handleStudentDeleted");
        loggingService.logInfo("Processing student DELETED event: " + studentEvent.getEntityDisplayName()
        + " (ID: " + studentEvent.getEntityId() + ")", logContext);
        
        try {
            clearStudentCacheData();
            
            loggingService.logInfo("Successfully processed student DELETED event for ID: " + studentEvent.getEntityId(), logContext);
            
        } catch (Exception e) {
            loggingService.logError("Failed to process student DELETED event for ID: " + studentEvent.getEntityId(), e, logContext);
            throw e; // Re-throw để trigger retry mechanism
        }
    }
    
    private void clearStudentCacheData() {
        LogContext logContext = getLogContext("clearStudentCacheData");
        
        try {
            // Xóa students cache
            redisTemplate.delete("students:all");
            loggingService.logInfo("Successfully clear students cache", logContext);
            
        } catch (Exception e) {
            loggingService.logError("Failed to invalidate students cache", e, logContext);
            // Không nên throw exception ở đây
        }
    }
    
}