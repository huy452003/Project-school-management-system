package com.common.kafka.services;

import com.common.kafka.models.KafkaMessage;
import com.common.kafka.models.StudentEvent;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    @Autowired
    private LoggingService loggingService;

    @Value("${kafka.topics.student-events}")
    private String studentEventsTopic;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("kafka-shared")
                .className(this.getClass().getName())
                .methodName(methodName)
                .build();
    }

    public void sendStudentEvent(StudentEvent event) {
        LogContext logContext = getLogContext("sendStudentEvent");
        try {
            kafkaTemplate.send(studentEventsTopic, event.getStudentId(), event);
            loggingService.logInfo("Sent StudentEvent to topic " + studentEventsTopic + ": " + event, logContext);
        } catch (Exception e) {
            loggingService.logError("Failed to send StudentEvent to topic " + studentEventsTopic + ": " + event, e, logContext);
        }
    }

    // Generic method to send any KafkaMessage
    public void sendMessage(String topic, String key, KafkaMessage message) {
        LogContext logContext = getLogContext("sendMessage");
        try {
            kafkaTemplate.send(topic, key, message);
            loggingService.logInfo("Sent message to topic " + topic + " with key " + key + ": " + message, logContext);
        } catch (Exception e) {
            loggingService.logError("Failed to send message to topic " + topic + " with key " + key + ": " + message, e, logContext);
        }
    }
}