package com.kafka_shared.services;

import com.kafka_shared.models.KafkaMessage;
import com.kafka_shared.models.EventMetadata;
import com.kafka_shared.models.NotificationMessage;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Value("${kafka.topics.notifications:notifications}")
    private String notificationTopic;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("kafka_shared")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    // gửi thông báo thành công cho event processing
    public <T extends KafkaMessage & EventMetadata> void sendEventSuccessNotification(
            T event, String topic, int partition, long offset, String moduleName) {
        
        try {
            String title = String.format("✅ %s Event Processed Successfully", 
                event.getEntityType().substring(0, 1).toUpperCase() + event.getEntityType().substring(1));
            
            String message = String.format(
                "%s %s (ID: %s) - Action: %s - Topic: %s - Partition: %d - Offset: %d - Module: %s",
                event.getEntityType(),
                event.getEntityDisplayName(),
                event.getEntityId(),
                event.getEventAction(),
                topic,
                partition,
                offset,
                moduleName
            );

            NotificationMessage notification = NotificationMessage.success(title, message);
            notification.setEntityType(event.getEntityType());
            notification.setEntityId(event.getEntityId());
            notification.setOriginalEventId(event.getEventId()); // Set original event ID
            notification.setEntityDisplayName(event.getEntityDisplayName());
            notification.setEventAction(event.getEventAction());
            notification.setSourceTopic(topic);
            notification.setPartition(partition);
            notification.setOffset(offset);
            notification.setModuleName(moduleName);
            notification.setSource(event.getSource()); // Set source từ event gốc

            // gửi thông báo đến Kafka
            String key = event.getEntityId() != null ? event.getEntityId() : event.getEventId();
            kafkaProducerService.sendNotification(notification, key);

            loggingService.logInfo(
                String.format("Success notification sent for %s with entity id : [%s] to topic: %s", 
                    event.getEntityType(), event.getEntityId(), notificationTopic),
                getLogContext("sendEventSuccessNotification")
            );

        } catch (Exception e) {
            loggingService.logError(
                String.format("Failed to send success notification for %s with entity id : [%s] to topic: %s - error: %s", 
                    event.getEntityType(), event.getEntityId(), notificationTopic, e.getMessage()),
                e,
                getLogContext("sendEventSuccessNotification")
            );
        }
    }

    // gửi thông báo lỗi cho event processing
    public <T extends KafkaMessage & EventMetadata> void sendEventFailureNotification(
            T event, String topic, int partition, long offset, String moduleName, String errorMessage) {
        
        try {
            String title = String.format("❌ %s Event Processing Failed", 
                event.getEntityType().substring(0, 1).toUpperCase() + event.getEntityType().substring(1));
            
            String message = String.format(
                "%s %s (ID: %s) - Action: %s - Topic: %s - Partition: %d - Offset: %d - Module: %s - Error: %s",
                event.getEntityType(),
                event.getEntityDisplayName(),
                event.getEntityId(),
                event.getEventAction(),
                topic,
                partition,
                offset,
                moduleName,
                errorMessage
            );

            NotificationMessage notification = NotificationMessage.failure(title, message);
            notification.setEntityType(event.getEntityType());
            notification.setEntityId(event.getEntityId());
            notification.setOriginalEventId(event.getEventId()); // Set original event ID
            notification.setEntityDisplayName(event.getEntityDisplayName());
            notification.setEventAction(event.getEventAction());
            notification.setSourceTopic(topic);
            notification.setPartition(partition);
            notification.setOffset(offset);
            notification.setModuleName(moduleName);
            notification.setErrorMessage(errorMessage);
            notification.setSource(event.getSource()); // Set source từ event gốc

            // gửi thông báo đến Kafka
            String key = event.getEntityId() != null ? event.getEntityId() : event.getEventId();
            kafkaProducerService.sendNotification(notification, key);

            loggingService.logWarn(
                String.format("Failure notification sent for %s event: %s - error: %s to topic: %s", 
                    event.getEntityType(), event.getEntityId(), errorMessage, notificationTopic),
                getLogContext("sendEventFailureNotification")
            );

        } catch (Exception e) {
            loggingService.logError(
                String.format("Failed to send failure notification for %s event: %s - error: %s to topic: %s", 
                    event.getEntityType(), event.getEntityId(), e.getMessage(), notificationTopic),
                e,
                getLogContext("sendEventFailureNotification")
            );
        }
    }

    // gửi các loại thông báo khác nhau , mặc định là info
    public void sendNotification(String type, String title, String message, String key) {
        try {
            NotificationMessage notification;
            
            switch (type.toUpperCase()) {
                case "SUCCESS":
                    notification = NotificationMessage.success(title, message);
                    break;
                case "FAILURE":
                    notification = NotificationMessage.failure(title, message);
                    break;
                case "WARNING":
                    notification = NotificationMessage.warning(title, message);
                    break;
                case "INFO":
                default:
                    notification = NotificationMessage.info(title, message);
                    break;
            }

            kafkaProducerService.sendNotification(notification, key);

            loggingService.logInfo(
                String.format("Notification sent: %s - %s", type, title),
                getLogContext("sendNotification")
            );

        } catch (Exception e) {
            loggingService.logError(
                String.format("Failed to send notification: %s - %s - error: %s", type, title, e.getMessage()),
                e,
                getLogContext("sendNotification")
            );
        }
    }
}
