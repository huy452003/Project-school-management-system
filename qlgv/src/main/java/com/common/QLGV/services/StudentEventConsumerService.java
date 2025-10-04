package com.common.QLGV.services;

import com.kafka_shared.models.StudentEvent;
import com.kafka_shared.services.KafkaConsumerService;
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
public class StudentEventConsumerService extends KafkaConsumerService<StudentEvent> {

    @Autowired
    private LoggingService loggingService;

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
        // Use base class method for common handling
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
        
        // TODO: Implement business logic for student creation
        // Ví dụ: Tạo record trong database teacher, gửi email thông báo, etc.
    }   

    private void handleStudentUpdated(StudentEvent studentEvent) {
        LogContext logContext = getLogContext("handleStudentUpdated");
        loggingService.logInfo("Processing student UPDATED event: " + studentEvent.getEntityDisplayName()
        + " (ID: " + studentEvent.getEntityId() + ")", logContext);
        
        // TODO: Implement business logic for student update
        // Ví dụ: Cập nhật thông tin trong database teacher, sync data, etc.
    }

    private void handleStudentDeleted(StudentEvent studentEvent) {
        LogContext logContext = getLogContext("handleStudentDeleted");
        loggingService.logInfo("Processing student DELETED event: " + studentEvent.getEntityDisplayName()
        + " (ID: " + studentEvent.getEntityId() + ")", logContext);
        
        // TODO: Implement business logic for student deletion
        // Ví dụ: Xóa record trong database teacher, cleanup related data, etc.
    }
    
}