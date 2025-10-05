package com.kafka_shared.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// class chung cho tất cả các event entity
@Data
@NoArgsConstructor
public abstract class KafkaMessage {
    
    private String eventId;
    private String eventType;
    @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    private String source;
    private String destination;
    private Object data;
    
    public KafkaMessage(String eventType, String source, String destination) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.source = source;
        this.destination = destination;
    }
}