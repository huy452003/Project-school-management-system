package com.kafka_shared.services;

import com.kafka_shared.models.EventMetadata;
import com.kafka_shared.models.KafkaMessage;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private LoggingService loggingService;

    // cấu hình topic
    @Value("${kafka.topics.student-events:student-events}")
    private String studentEventsTopic;

    @Value("${kafka.topics.teacher-events:teacher-events}")
    private String teacherEventsTopic;

    @Value("${kafka.topics.user-events:user-events}")
    private String userEventsTopic;

    @Value("${kafka.topics.user-events-profile:profile-events}")
    private String profileEventsTopic;

    @Value("${kafka.topics.notifications:notifications}")
    private String notificationsTopic;

    // DLQ topics
    @Value("${kafka.topics.student-events-dlq:student-events-dlq}")
    private String studentEventsDLQTopic;

    @Value("${kafka.topics.teacher-events-dlq:teacher-events-dlq}")
    private String teacherEventsDLQTopic;

    @Value("${kafka.topics.user-events-dlq:user-events-dlq}")
    private String userEventsDLQTopic;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("kafka_shared")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    // gửi message đến Kafka với callback
    public void sendMessage(String topic, String key, Object message) {
        try {
            kafkaTemplate.send(topic, key, message).whenComplete((result, throwable) -> {
                if (throwable == null) {
                    loggingService.logInfo(
                        String.format("Message sent successfully to topic: %s, partition: %d, offset: %d, key: [%s]", 
                            topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), key),
                        getLogContext("sendMessage")
                    );
                } else {
                    loggingService.logError(
                        String.format("Failed to send message to topic: %s, key: [%s] - error: %s", 
                            topic, key, throwable.getMessage()),
                        (Exception) throwable,
                        getLogContext("sendMessage")
                    );
                }
            });
        } catch (Exception e) {
            loggingService.logError(
                String.format("Error sending message to topic: %s - error: %s", topic, e.getMessage()),
                e,
                getLogContext("sendMessage")
            );
        }
    }

    // gửi event đến Kafka thông qua message
    public <T extends KafkaMessage & EventMetadata> void sendEvent(T event, String topic) {
        try {            
            String key = event.getEntityId();
            sendMessage(topic, key, event);
            
            loggingService.logInfo(
                String.format("%s sent to topic: %s - with entity id: [%s]", event.getEntityType(), topic, event.getEntityId()),
                getLogContext("sendEvent")
            );
            
        } catch (Exception e) {
            loggingService.logError(
                String.format("Error sending %s to topic %s with entity id: [%s]: %s", event.getEntityType(), topic, event.getEntityId(), e.getMessage()),
                e,
                getLogContext("sendEvent")
            );
            sendToDeadLetterQueue(event, e);
        }
    }

    // gửi student event đến student-events topic
    public <T extends KafkaMessage & EventMetadata> void sendStudentEvent(T event) {
        sendEvent(event, studentEventsTopic);
    }

    // gửi teacher event đến teacher-events topic
    public <T extends KafkaMessage & EventMetadata> void sendTeacherEvent(T event) {
        sendEvent(event, teacherEventsTopic);
    }

    // gửi user event đến user-events topic
    public <T extends KafkaMessage & EventMetadata> void sendUserEvent(T event) {
        sendEvent(event, userEventsTopic);
    }

    public <T extends KafkaMessage & EventMetadata> void sendProfileEvent(T event) {
        sendEvent(event, profileEventsTopic);
    }

    // gửi notification đến notifications topic
    public void sendNotification(Object notification, String key) {
        sendMessage(notificationsTopic, key, notification);
    }

    // gửi event đến Dead Letter Queue khi hết số lần retry
    public <T extends KafkaMessage & EventMetadata> void sendToDeadLetterQueue(T event, Exception error) {
        try {
            String dlqTopic = getDLQTopic(event);
            
            loggingService.logWarn(
                String.format("Sending %s to DLQ topic %s with entity id: [%s]", event.getEntityType(), dlqTopic, event.getEntityId()),
                getLogContext("sendToDeadLetterQueue")
            );
            
            String key = event.getEntityId();
            sendMessage(dlqTopic, key, event);
            
            loggingService.logWarn(
                String.format("%s sent to DLQ with entity id: [%s] - error: %s", 
                    event.getEntityType(), event.getEntityId(), error.getMessage()),
                getLogContext("sendToDeadLetterQueue")
            );
            
        } catch (Exception e) {
            loggingService.logError(
                String.format("Failed to send %s to DLQ with entity id: [%s]: %s", event.getEntityType(), event.getEntityId(), e.getMessage()),
                e,
                getLogContext("sendToDeadLetterQueue")
            );
        }
    }

    // gửi event đến Dead Letter Queue trực tiếp
    public <T extends KafkaMessage & EventMetadata> void sendEventToDLQ(T event, String reason) {
        try {
            String dlqTopic = getDLQTopic(event);
            
            loggingService.logWarn(
                String.format("Sending %s to DLQ topic %s with entity id: [%s] - reason: %s", 
                    event.getEntityType(), dlqTopic, event.getEntityId(), reason),
                getLogContext("sendEventToDLQ")
            );
            
            String key = event.getEntityId();
            sendMessage(dlqTopic, key, event);
            
            loggingService.logWarn(
                String.format("%s sent to DLQ with entity id: [%s] - reason: %s", 
                    event.getEntityType(), event.getEntityId(), reason),
                getLogContext("sendEventToDLQ")
            );
            
        } catch (Exception e) {
            loggingService.logError(
                String.format("Failed to send %s to DLQ with entity id: [%s]: %s", event.getEntityType(), event.getEntityId(), e.getMessage()),
                e,
                getLogContext("sendEventToDLQ")
            );
        }
    }

    // lấy topic DLQ dựa trên type của event
    private <T extends KafkaMessage & EventMetadata> String getDLQTopic(T event) {
        String entityType = event.getEntityType();
        switch (entityType) {
            case "studentEvent":
                return studentEventsDLQTopic;
            case "teacherEvent":
                return teacherEventsDLQTopic;
            case "userEvent":
                return userEventsDLQTopic;
            default:
                loggingService.logWarn(
                    String.format("Unknown entity type: %s, using student DLQ topic with entity id: [%s]", entityType, event.getEntityId()),
                    getLogContext("getDLQTopic")
                );
                return studentEventsDLQTopic;
        }
    }

}