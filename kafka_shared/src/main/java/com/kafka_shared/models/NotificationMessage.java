package com.kafka_shared.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class NotificationMessage {
    
    private String notificationId;
    
    private String type; // SUCCESS, FAILURE, WARNING, INFO
    
    private String title;
    
    private String message;
    
    private String entityType;
    
    private String entityId;
    
    private String originalEventId; // ID của event gốc được gửi từ producer
    
    private String entityDisplayName;
    
    private String eventAction;
    
    private String sourceTopic;
    
    private int partition;
    
    private long offset;
    
    private String moduleName;
    
    private String errorMessage;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    
    private String source;
    
    private String destination;
    
    public NotificationMessage(String type, String title, String message) {
        this.notificationId = UUID.randomUUID().toString();
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.source = "kafka_shared";
        this.destination = "all";
    }
    
    // các dạng notification message tùy chọn
    public static NotificationMessage success(String title, String message) {
        return new NotificationMessage("SUCCESS", title, message);
    }
    
    public static NotificationMessage failure(String title, String message) {
        return new NotificationMessage("FAILURE", title, message);
    }
    
    public static NotificationMessage warning(String title, String message) {
        return new NotificationMessage("WARNING", title, message);
    }
    
    public static NotificationMessage info(String title, String message) {
        return new NotificationMessage("INFO", title, message);
    }
}
