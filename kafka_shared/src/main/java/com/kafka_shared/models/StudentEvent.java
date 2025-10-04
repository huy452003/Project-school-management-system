package com.kafka_shared.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StudentEvent extends KafkaMessage implements EventMetadata {
    private String id;
    private String fullName;
    private String action; // CREATED, UPDATED, DELETED
    
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
        StudentEvent studentEvent = new StudentEvent();
        studentEvent.setEventId(UUID.randomUUID().toString());
        studentEvent.setEventType("STUDENT_EVENT");
        studentEvent.setId(id); 
        studentEvent.setFullName(fullName);
        studentEvent.setAction("CREATED");
        studentEvent.setTimestamp(LocalDateTime.now());
        studentEvent.setSource("qlsv");
        studentEvent.setDestination("all");
        return studentEvent;
    }

    public static StudentEvent studentUpdated(String id, String fullName) {
        StudentEvent studentEvent = new StudentEvent();
        studentEvent.setEventId(UUID.randomUUID().toString());
        studentEvent.setEventType("STUDENT_EVENT");
        studentEvent.setId(id);
        studentEvent.setFullName(fullName);
        studentEvent.setAction("UPDATED");
        studentEvent.setTimestamp(LocalDateTime.now());
        studentEvent.setSource("qlsv");
        studentEvent.setDestination("all");
        return studentEvent;
    }

    public static StudentEvent studentDeleted(String id, String fullName) {
        StudentEvent studentEvent = new StudentEvent();
        studentEvent.setEventId(UUID.randomUUID().toString());
        studentEvent.setEventType("STUDENT_EVENT");
        studentEvent.setId(id);
        studentEvent.setFullName(fullName);
        studentEvent.setAction("DELETED");
        studentEvent.setTimestamp(LocalDateTime.now());
        studentEvent.setSource("qlsv");
        studentEvent.setDestination("all");
        return studentEvent;
    }
              
}