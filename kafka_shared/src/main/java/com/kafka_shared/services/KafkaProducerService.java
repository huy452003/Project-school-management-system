package com.kafka_shared.services;

import com.kafka_shared.models.EventMetadata;
import com.kafka_shared.models.KafkaMessage;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Generic Kafka producer service supporting multiple event types
 */
@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private LoggingService loggingService;

    // Topic configurations
    @Value("${kafka.topics.student-events:student-events}")
    private String studentEventsTopic;

    @Value("${kafka.topics.student-events-dlq:student-events-dlq}")
    private String studentEventsDLQTopic;

    @Value("${kafka.topics.teacher-events:teacher-events}")
    private String teacherEventsTopic;

    @Value("${kafka.topics.teacher-events-dlq:teacher-events-dlq}")
    private String teacherEventsDLQTopic;

    @Value("${kafka.topics.user-events:user-events}")
    private String userEventsTopic;

    @Value("${kafka.topics.user-events-dlq:user-events-dlq}")
    private String userEventsDLQTopic;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("kafka_shared")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    /**
     * Generic method to send any message to Kafka
     */
    public void sendMessage(String topic, String key, Object message) {
        try {
            kafkaTemplate.send(topic, key, message);
            loggingService.logInfo(
                String.format("Message sent to topic: %s with key(student id): [%s]", topic, key),
                getLogContext("sendMessage")
            );
        } catch (Exception e) {
            loggingService.logError(
                String.format("Error sending message to topic: %s - error: %s", topic, e.getMessage()),
                e,
                getLogContext("sendMessage")
            );
        }
    }

    /**
     * Generic method to send any event type
     */
    public <T extends KafkaMessage & EventMetadata> void sendEvent(T event, String topic) {
        try {            

            String key = event.getEntityId();
            sendMessage(topic, key, event);
            
            loggingService.logInfo(
                String.format("%s sent to topic: %s - with student id: [%s]", event.getEntityType(), topic, event.getEntityId()),
                getLogContext("sendEvent")
            );
            
        } catch (Exception e) {
            loggingService.logError(
                String.format("Error sending %s to topic %s with student id: [%s]: %s", event.getEntityType(), topic, event.getEntityId(), e.getMessage()),
                e,
                getLogContext("sendEvent")
            );
            sendToDeadLetterQueue(event, e);
        }
    }

    /**
     * Gửi event đến Dead Letter Queue khi hết số lần retry
     */
    public <T extends KafkaMessage & EventMetadata> void sendToDeadLetterQueue(T event, Exception error) {
        try {
            String dlqTopic = getDLQTopic(event);
            
            loggingService.logWarn(
                String.format("Sending %s to DLQ topic %s with student id: [%s]", event.getEntityType(), dlqTopic, event.getEntityId()),
                getLogContext("sendToDeadLetterQueue")
            );
            
            String key = event.getEntityId();
            sendMessage(dlqTopic, key, event);
            
            loggingService.logWarn(
                String.format("%s sent to DLQ with student id: [%s] - error: %s", 
                    event.getEntityType(), event.getEntityId(), error.getMessage()),
                getLogContext("sendToDeadLetterQueue")
            );
            
        } catch (Exception e) {
            loggingService.logError(
                String.format("Failed to send %s to DLQ with student id: [%s]: %s", event.getEntityType(), event.getEntityId(), e.getMessage()),
                e,
                getLogContext("sendToDeadLetterQueue")
            );
        }
    }

    /**
     * Gửi Event đến Dead Letter Queue trực tiếp
     */
    public <T extends KafkaMessage & EventMetadata> void sendEventToDLQ(T event, String reason) {
        try {
            String dlqTopic = getDLQTopic(event);
            
            loggingService.logWarn(
                String.format("Sending %s to DLQ topic %s with student id: [%s] - reason: %s", 
                    event.getEntityType(), dlqTopic, event.getEntityId(), reason),
                getLogContext("sendEventToDLQ")
            );
            
            String key = event.getEntityId();
            sendMessage(dlqTopic, key, event);
            
            loggingService.logWarn(
                String.format("%s sent to DLQ with student id: [%s] - reason: %s", 
                    event.getEntityType(), event.getEntityId(), reason),
                getLogContext("sendEventToDLQ")
            );
            
        } catch (Exception e) {
            loggingService.logError(
                String.format("Failed to send %s to DLQ with student id: [%s]: %s", event.getEntityType(), event.getEntityId(), e.getMessage()),
                e,
                getLogContext("sendEventToDLQ")
            );
        }
    }

    /**
     * Get DLQ topic based on event type
     */
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
                    String.format("Unknown entity type: %s, using student DLQ topic with student id: [%s]", entityType, event.getEntityId()),
                    getLogContext("getDLQTopic")
                );
                return studentEventsDLQTopic;
        }
    }

}