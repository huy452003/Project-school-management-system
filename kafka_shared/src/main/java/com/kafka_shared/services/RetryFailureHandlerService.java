package com.kafka_shared.services;

import com.kafka_shared.models.KafkaMessage;
import com.kafka_shared.models.EventMetadata;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Service;

/**
 * Service to handle retry failures and send messages to Dead Letter Queue
 */
@Service
public class RetryFailureHandlerService implements ConsumerRecordRecoverer {

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private NotificationService notificationService;

    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("kafka_shared")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    /**
     * Called when all retry attempts are exhausted
     * This method is called by Spring Kafka's DefaultErrorHandler
     */
    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception exception) {
        try {
            loggingService.logError(
                String.format("All retry attempts exhausted for record: topic=%s, partition=%d, offset=%d, key=%s", 
                    record.topic(), record.partition(), record.offset(), record.key()),
                exception,
                getLogContext("accept")
            );

            // Handle the failed record based on its type
            if (record.value() instanceof KafkaMessage && record.value() instanceof EventMetadata) {
                KafkaMessage event = (KafkaMessage) record.value();
                EventMetadata eventMetadata = (EventMetadata) record.value();
                
                loggingService.logWarn(
                    String.format("Sending %s event to DLQ after retry failure: %s", 
                        eventMetadata.getEntityType(), event.getEventId()),
                    getLogContext("accept")
                );
                
                // Send to DLQ using the generic method
                kafkaProducerService.sendToDeadLetterQueue((KafkaMessage & EventMetadata) record.value(), exception);
                
                // Send failure notification
                notificationService.sendEventFailureNotification(
                    (KafkaMessage & EventMetadata) record.value(), 
                    record.topic(), 
                    record.partition(), 
                    record.offset(), 
                    "kafka_shared", 
                    exception.getMessage()
                );
                
                loggingService.logWarn(
                    String.format("Successfully sent %s event to DLQ: %s", 
                        eventMetadata.getEntityType(), event.getEventId()),
                    getLogContext("accept")
                );
                
            } else {
                // Handle non-KafkaMessage records (e.g., deserialization failures)
                String recordInfo = String.format("topic=%s, partition=%d, offset=%d, key=%s", 
                    record.topic(), record.partition(), record.offset(), record.key());
                
                if (record.value() == null) {
                    loggingService.logWarn(
                        String.format("Record with null value cannot be sent to DLQ: %s", recordInfo),
                        getLogContext("accept")
                    );
                } else {
                    loggingService.logWarn(
                        String.format("Unknown record type, cannot send to DLQ: %s, type=%s", 
                            recordInfo, record.value().getClass().getSimpleName()),
                        getLogContext("accept")
                    );
                }
                
                // Log deserialization failure details
                if (exception instanceof RecordDeserializationException ||
                    exception.getMessage() != null && exception.getMessage().contains("deserialization")) {
                    loggingService.logError(
                        String.format("Deserialization failure for record: %s, error: %s", 
                            recordInfo, exception.getMessage()),
                        exception,
                        getLogContext("accept")
                    );
                }
            }
            
        } catch (Exception e) {
            loggingService.logError(
                String.format("Error in retry failure handler: %s", e.getMessage()),
                e,
                getLogContext("accept")
            );
        }
    }
}
