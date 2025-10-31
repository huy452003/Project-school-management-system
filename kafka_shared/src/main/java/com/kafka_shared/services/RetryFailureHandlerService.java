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

    // gọi khi tất cả các lần retry đều hết
    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception exception) {
        try {
            loggingService.logError(
                String.format("All retry attempts exhausted for record: topic=%s, partition=%d, offset=%d, key=%s", 
                    record.topic(), record.partition(), record.offset(), record.key()),
                exception,
                getLogContext("accept")
            );

            // xử lý record thất bại dựa trên type của nó
            if (record.value() instanceof KafkaMessage && record.value() instanceof EventMetadata) {
                KafkaMessage event = (KafkaMessage) record.value();
                EventMetadata eventMetadata = (EventMetadata) record.value();
                
                loggingService.logWarn(
                    String.format("Sending %s event to DLQ after retry failure: %s", 
                        eventMetadata.getEntityType(), event.getEventId()),
                    getLogContext("accept")
                );
                
                // gửi đến DLQ
                kafkaProducerService.sendToDeadLetterQueue((KafkaMessage & EventMetadata) record.value(), exception);
                
                // gửi thông báo lỗi
                notificationService.sendEventFailureNotification(
                    (KafkaMessage & EventMetadata) record.value(), 
                    record.topic(), 
                    record.partition(), 
                    record.offset(), 
                    "kafka_shared", 
                    exception.getMessage()
                );
                
                loggingService.logWarn(
                    String.format("Successfully sent %s event to DLQ: %s. Message will be skipped (offset will advance).", 
                        eventMetadata.getEntityType(), event.getEventId()),
                    getLogContext("accept")
                );
                
                // ✅ Return bình thường → Kafka auto-acknowledge → Skip message
                // Đây là behavior CHUẨN: Message lỗi vào DLQ, offset tăng, consumer tiếp tục
                
            } else {
                // xử lý record không phải KafkaMessage (ví dụ: lỗi deserialization)
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
                
                // log lỗi deserialization
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
                String.format("Error in retry failure handler: %s. Acknowledging anyway to prevent consumer blocking.", 
                    e.getMessage()),
                e,
                getLogContext("accept")
            );
            // ⚠️ KHÔNG throw exception ra ngoài
            // → Kafka sẽ auto-acknowledge
            // → Consumer tiếp tục process message tiếp theo
            // → Message lỗi đã ở trong DLQ hoặc notification
        }
        
        // ✅ Return bình thường → Kafka auto-acknowledge offset
        // Điều này đảm bảo consumer KHÔNG bị stuck ở message lỗi
    }
}
