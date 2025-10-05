package com.kafka_shared.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TeacherEvent extends KafkaMessage implements EventMetadata {
    private String id;
    private String fullName;
    private String action; // CREATED, UPDATED, DELETED
    
    // Constructor để gọi parent constructor
    public TeacherEvent(String eventType, String source, String destination) {
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

    public static TeacherEvent teacherCreated(String id, String fullName) {
        TeacherEvent teacherEvent = new TeacherEvent(
            "TEACHER_EVENT", 
            "qlgv", 
            "all");
        teacherEvent.setId(id);
        teacherEvent.setFullName(fullName);
        teacherEvent.setAction("CREATED");
        return teacherEvent;
    }

    public static TeacherEvent teacherUpdated(String id, String fullName) {
        TeacherEvent teacherEvent = new TeacherEvent(
            "TEACHER_EVENT", 
            "qlgv", 
            "all");
        teacherEvent.setId(id);
        teacherEvent.setFullName(fullName);
        teacherEvent.setAction("UPDATED");
        return teacherEvent;
    }

    public static TeacherEvent teacherDeleted(String id, String fullName) {
        TeacherEvent teacherEvent = new TeacherEvent(
            "TEACHER_EVENT", 
            "qlgv", 
            "all");
        teacherEvent.setId(id);
        teacherEvent.setFullName(fullName);
        teacherEvent.setAction("DELETED");
        return teacherEvent;
    }
}
