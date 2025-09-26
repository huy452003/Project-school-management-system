package com.common.kafka.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentEvent extends KafkaMessage {
    
    private String studentId;
    private String studentName;
    private String action; // CREATED, UPDATED, DELETED
    
    // Constructor
    public StudentEvent() {
        super();
    }
    
    // Static factory methods for easy creation
    public static StudentEvent studentCreated(String studentId, String studentName) {
        StudentEvent event = new StudentEvent();
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventType("CREATE");
        event.setStudentId(studentId);
        event.setStudentName(studentName);
        event.setAction("CREATED");
        event.setSource("QLSV");
        event.setTimestamp(java.time.LocalDateTime.now());
        
        return event;
    }
    
    public static StudentEvent studentUpdated(String studentId, String studentName) {
        StudentEvent event = new StudentEvent();
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventType("UPDATE");
        event.setStudentId(studentId);
        event.setStudentName(studentName);
        event.setAction("UPDATED");
        event.setSource("QLSV");
        event.setTimestamp(java.time.LocalDateTime.now());
        
        return event;
    }
    
    public static StudentEvent studentDeleted(String studentId, String studentName) {
        StudentEvent event = new StudentEvent();
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventType("DELETE");
        event.setStudentId(studentId);
        event.setStudentName(studentName);
        event.setAction("DELETED");
        event.setSource("QLSV");
        event.setTimestamp(java.time.LocalDateTime.now());
        
        return event;
    }
}