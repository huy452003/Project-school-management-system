package com.common.QLGV.services;

import com.common.QLGV.services.imp.TeacherServiceImp;
import com.kafka_shared.models.UserEvent;
import com.kafka_shared.services.KafkaConsumerService;
import com.kafka_shared.services.KafkaProducerService;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.model_shared.enums.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class UserConsumerService extends KafkaConsumerService<UserEvent> {

    @Autowired
    private LoggingService loggingService;
    @Autowired
    TeacherServiceImp teacherService;
    @Autowired
    KafkaProducerService kafkaProducerService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    @KafkaListener(topics = "user-events")
    public void handleUserEvent(@Payload UserEvent userEvent,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                @Header(KafkaHeaders.OFFSET) long offset,
                                Acknowledgment acknowledgment) {
        LogContext logContext = getLogContext("handleUserEvent");
        try {
            if (userEvent.getUser() == null || userEvent.getUser().getType() == null) {
                loggingService.logWarn("User or user type is null in event. Skipping.", logContext);
                acknowledgment.acknowledge();
                return;
            }
            
            if(userEvent.getUser().getType().equals(Type.TEACHER)){
                // handleEvent() sẽ tự động acknowledge, không cần acknowledge lại
                handleEvent(userEvent, topic, partition, offset, acknowledgment,"qlgv");
            }else {
                loggingService.logWarn("user event is not a teacher", logContext);
                acknowledgment.acknowledge();
                return;
            }
            loggingService.logInfo("Successfully handled user event", logContext);
        }
        catch (Exception e) {
            loggingService.logError("Failed to process user event", e, logContext);
            throw e;
        }
    }

    @Override
    protected void processEvent(UserEvent event) {
        LogContext logContext = getLogContext("processEvent");
        switch (event.getAction()) {
            case "REGISTERED":
                handleUserRegistered(event);
                break;
            default:
                loggingService.logWarn("Unknown user event action: " + event.getAction(), logContext);
                throw new IllegalArgumentException("Unknown event action: " + event.getAction());
        }
    }

    private void handleUserRegistered(UserEvent event) {
        LogContext logContext = getLogContext("handleUserRegistered");
        loggingService.logInfo("Processing user REGISTERED event: " + event.getEntityDisplayName()
                + " (ID: " + event.getEntityId() + ")", logContext);

        try{
            teacherService.createByUserId(event.getUser());
            loggingService.logInfo("Successfully processed user REGISTERED event", logContext);

            UserEvent profile = UserEvent.ProfileCreated(event);
            kafkaProducerService.sendProfileEvent(profile);
            loggingService.logInfo("Successfully sending PROFILE_CREATED event to profile-events topic"
                    , logContext);

        } catch (Exception e) {
            loggingService.logError("Failed to process user REGISTERED event", e, logContext);
            throw e;
        }
    }
}
