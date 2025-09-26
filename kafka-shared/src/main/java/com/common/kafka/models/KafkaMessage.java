package com.common.kafka.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessage {
    
    private String eventId;
    private String eventType; // CREATE, UPDATE, DELETE, etc.
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String source; // QLSV, QLGV, Security, etc.
    private String destination; // Target module (optional)
    private String correlationId; // For tracking related messages
    
    private Object data; // Event-specific data
    
    // Constructor for easy creation
    public KafkaMessage(String eventType, String source) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.source = source;
    }
}