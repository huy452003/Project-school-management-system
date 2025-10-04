package com.kafka_shared.services;

import com.kafka_shared.models.EventMetadata;
import com.kafka_shared.models.KafkaMessage;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.handle_exceptions.NotFoundExceptionHandle;
import com.handle_exceptions.ConflictExceptionHandle;
import com.handle_exceptions.UnauthorizedExceptionHandle;
import com.handle_exceptions.ForbiddenExceptionHandle;
import org.springframework.beans.factory.annotation.Autowired;      
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public abstract class KafkaConsumerService <T extends KafkaMessage & EventMetadata> {

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("kafka_shared")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    public void handleEvent(T event, String topic, int partition, long offset, 
                              Acknowledgment acknowledgment, String moduleName) {
        try {
            // 1. Process business logic
            processEvent(event);
            
            // 2. Commit offset
            acknowledgment.acknowledge();
            
            // 3. Send success notification
            notificationService.sendEventSuccessNotification(event, topic, partition, offset, moduleName);
            
            loggingService.logInfo(
                String.format("Event processed successfully: %s %s (ID: %s)", 
                    event.getEntityType(), event.getEntityDisplayName(), event.getEntityId()),
                getLogContext("handleEvent")
            );

        } catch (NotFoundExceptionHandle | ConflictExceptionHandle | UnauthorizedExceptionHandle | ForbiddenExceptionHandle e) {
            // Non-retryable error - send to DLQ directly
            handleNonRetryableError(event, topic, partition, offset, moduleName, e, acknowledgment);
            
        } catch (Exception e) {
            // Retryable error - send failure notification and re-throw for retry
            handleRetryableError(event, topic, partition, offset, moduleName, e);
            throw e; // Trigger retry mechanism
        }
    }

    protected abstract void processEvent(T event);
    
    /**
     * Handle non-retryable errors - send to DLQ directly
     */
    private void handleNonRetryableError(T event, String topic, int partition, long offset, 
                                        String moduleName, Exception e, Acknowledgment acknowledgment) {
        try {
            // Send to DLQ directly
            kafkaProducerService.sendEventToDLQ(event, e.getMessage());
            
            // Send failure notification
            notificationService.sendEventFailureNotification(event, topic, partition, offset, moduleName, e.getMessage());
            
            // Commit offset to avoid reprocessing
            acknowledgment.acknowledge();
            
            loggingService.logWarn(
                String.format("Non-retryable error - sent to DLQ: %s %s (ID: %s) - error: %s", 
                    event.getEntityType(), event.getEntityDisplayName(), event.getEntityId(), e.getMessage()),
                getLogContext("handleNonRetryableError")
            );
            
        } catch (Exception dlqException) {
            loggingService.logError(
                String.format("Failed to send non-retryable error to DLQ: %s %s (ID: %s) - error: %s", 
                    event.getEntityType(), event.getEntityDisplayName(), event.getEntityId(), dlqException.getMessage()),
                dlqException,
                getLogContext("handleNonRetryableError")
            );
            // Still commit to avoid infinite loop
            acknowledgment.acknowledge();
        }
    }
    
    /**
     * Handle retryable errors - log only, notification will be sent by RetryFailureHandlerService
     */
    private void handleRetryableError(T event, String topic, int partition, long offset, 
                                     String moduleName, Exception e) {
        // Only log - notification will be sent by RetryFailureHandlerService when retry exhausted
        loggingService.logWarn(
            String.format("Retryable error - will retry: %s %s (ID: %s) - error: %s", 
                event.getEntityType(), event.getEntityDisplayName(), event.getEntityId(), e.getMessage()),
            getLogContext("handleRetryableError")
        );
    }
    
}
