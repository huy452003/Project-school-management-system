package com.common.kafka.services;

import com.common.kafka.models.KafkaMessage;
import com.common.kafka.models.StudentEvent;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    private LoggingService loggingService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("kafka-shared")
                .className(this.getClass().getName())
                .methodName(methodName)
                .build();
    }

    @KafkaListener(topics = "${kafka.topics.student-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenStudentEvents(StudentEvent event) {
        LogContext logContext = getLogContext("listenStudentEvents");
        loggingService.logInfo("Received StudentEvent: " + event, logContext);

        switch (event.getEventType()) {
            case "CREATE":
                loggingService.logInfo("Processing Student CREATE event for student: " + event.getStudentName(), logContext);
                break;
            case "UPDATE":
                loggingService.logInfo("Processing Student UPDATE event for student: " + event.getStudentName(), logContext);
                break;
            case "DELETE":
                loggingService.logInfo("Processing Student DELETE event for student: " + event.getStudentName(), logContext);
                break;
            default:
                loggingService.logWarn("Unknown StudentEvent type: " + event.getEventType(), logContext);
        }
    }

    // Generic listener for other Kafka messages if needed
    // @KafkaListener(topics = "${kafka.topics.student-notifications}", groupId = "${spring.kafka.consumer.group-id}")
    // public void listenNotifications(KafkaMessage message) {
    //     LogContext logContext = getLogContext("listenNotifications");
    //     loggingService.logInfo("Received KafkaMessage from notifications topic: " + message, logContext);
    //     // Process generic notification messages
    // }
}