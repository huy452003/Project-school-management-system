package com.common.QLGV.services;

import com.common.kafka.models.StudentEvent;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StudentEventConsumerService {

    @Autowired
    private LoggingService loggingService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getName())
                .methodName(methodName)
                .build();
    }

    @KafkaListener(topics = "student-events")
    public void handleStudentEvent(StudentEvent studentEvent) {
        LogContext logContext = getLogContext("handleStudentEvent");

        try {
            loggingService.logInfo("Received student event: " + studentEvent.getAction()
            + " for student: " + studentEvent.getStudentName()
            + " (ID: " + studentEvent.getStudentId() + ")", logContext);

            // Xử lý logic khi nhận được student event
            processStudentEvent(studentEvent);

            loggingService.logInfo("Successfully processed student event for student: " + studentEvent.getStudentName(), logContext);

        } catch (Exception e) {
            loggingService.logError("Error processing student event for student: "
            + studentEvent.getStudentName() + ". Error: " + e.getMessage(), e, logContext);
        }
    }

    private void processStudentEvent(StudentEvent studentEvent) {
        switch (studentEvent.getAction()) {
            case "CREATED":
                handleStudentCreated(studentEvent);
                break;
            case "UPDATED":
                handleStudentUpdated(studentEvent);
                break;
            case "DELETED":
                handleStudentDeleted(studentEvent);
                break;
            default:
                break;
        }
    }

    private void handleStudentCreated(StudentEvent studentEvent) {
        LogContext logContext = getLogContext("handleStudentCreated");

        loggingService.logInfo("New student created: " + studentEvent.getStudentName()
        + " (ID: " + studentEvent.getStudentId()
        + "). Teachers received event need to do something...", logContext);
    }

    private void handleStudentUpdated(StudentEvent studentEvent) {
        LogContext logContext = getLogContext("handleStudentUpdated");

        loggingService.logInfo("Student updated: " + studentEvent.getStudentName()
        + " (ID: " + studentEvent.getStudentId()
        + "). Teachers received event need to do something...", logContext);
    }

    private void handleStudentDeleted(StudentEvent studentEvent) {
        LogContext logContext = getLogContext("handleStudentDeleted");

        loggingService.logInfo("Student deleted: " + studentEvent.getStudentName()
        + " (ID: " + studentEvent.getStudentId()
        + "). Teachers received event need to do something...", logContext);
    }
}