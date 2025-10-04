package com.kafka_shared.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TeacherEvent extends KafkaMessage implements EventMetadata {
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

    public static TeacherEvent teacherCreated(String id, String fullName) {
        TeacherEvent teacherEvent = new TeacherEvent();
        teacherEvent.setEventId(UUID.randomUUID().toString());
        teacherEvent.setEventType("TEACHER_EVENT");
        teacherEvent.setId(id);
        teacherEvent.setFullName(fullName);
        teacherEvent.setAction("CREATED");
        teacherEvent.setTimestamp(LocalDateTime.now());
        teacherEvent.setSource("qlgv");
        teacherEvent.setDestination("all");
        return teacherEvent;
    }

    public static TeacherEvent teacherUpdated(String id, String fullName) {
        TeacherEvent teacherEvent = new TeacherEvent();
        teacherEvent.setEventId(UUID.randomUUID().toString());
        teacherEvent.setEventType("TEACHER_EVENT");
        teacherEvent.setId(id);
        teacherEvent.setFullName(fullName);
        teacherEvent.setAction("UPDATED");
        teacherEvent.setTimestamp(LocalDateTime.now());
        teacherEvent.setSource("qlgv");
        teacherEvent.setDestination("all");
        return teacherEvent;
    }

    public static TeacherEvent teacherDeleted(String id, String fullName) {
        TeacherEvent teacherEvent = new TeacherEvent();
        teacherEvent.setEventId(UUID.randomUUID().toString());
        teacherEvent.setEventType("TEACHER_EVENT");
        teacherEvent.setId(id);
        teacherEvent.setFullName(fullName);
        teacherEvent.setAction("DELETED");
        teacherEvent.setTimestamp(LocalDateTime.now());
        teacherEvent.setSource("qlgv");
        teacherEvent.setDestination("all");
        return teacherEvent;
    }
}
