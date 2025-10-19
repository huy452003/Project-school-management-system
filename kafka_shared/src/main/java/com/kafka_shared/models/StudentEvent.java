package com.kafka_shared.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StudentEvent extends KafkaMessage implements EventMetadata {
    private String id;
    private String fullName;
    private String action; // CREATED, UPDATED, DELETED
    
    public StudentEvent(String eventType, String source, String destination) {
        super(eventType, source, destination);
    }
    
    @Override
    public String getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityDisplayName() {
        return this.fullName;
    }

    @Override
    public String getEventAction() {
        return this.action;
    }

    public static StudentEvent studentCreated(String id, String fullName) {
        StudentEvent studentEvent = new StudentEvent(
            "STUDENT_EVENT", 
            "qlsv", 
            "all");
        studentEvent.setId(id); 
        studentEvent.setFullName(fullName);
        studentEvent.setAction("CREATED");
        return studentEvent;
    }

    public static StudentEvent studentUpdated(String id, String fullName) {
        StudentEvent studentEvent = new StudentEvent(
            "STUDENT_EVENT", 
            "qlsv", 
            "all");
        studentEvent.setId(id);
        studentEvent.setFullName(fullName);
        studentEvent.setAction("UPDATED");
        return studentEvent;
    }

    public static StudentEvent studentDeleted(String id, String fullName) {
        StudentEvent studentEvent = new StudentEvent(
            "STUDENT_EVENT", 
            "qlsv", 
            "all");
        studentEvent.setId(id);
        studentEvent.setFullName(fullName);
        studentEvent.setAction("DELETED");
        return studentEvent;
    }
              
}