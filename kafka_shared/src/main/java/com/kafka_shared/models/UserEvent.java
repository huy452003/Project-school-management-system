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
public class UserEvent extends KafkaMessage implements EventMetadata {
    private String id;
    private String userName;
    private String action; // CREATED, LOGIN, LOGOUT, REGISTERED

    @Override
    public String getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityDisplayName() {
        return this.userName;
    }

    @Override
    public String getEventAction() {
        return this.action;
    }

    public static UserEvent userCreated(String id, String userName) {
        UserEvent userEvent = new UserEvent();
        userEvent.setEventId(UUID.randomUUID().toString());
        userEvent.setEventType("USER_EVENT");
        userEvent.setId(id);
        userEvent.setUserName(userName);
        userEvent.setAction("CREATED");
        userEvent.setTimestamp(LocalDateTime.now());
        userEvent.setSource("security");
        userEvent.setDestination("all");
        return userEvent;
    }

    public static UserEvent userLogin(String id, String userName) {
        UserEvent userEvent = new UserEvent();
        userEvent.setEventId(UUID.randomUUID().toString());
        userEvent.setEventType("USER_EVENT");
        userEvent.setId(id);
        userEvent.setUserName(userName);
        userEvent.setAction("LOGIN");
        userEvent.setTimestamp(LocalDateTime.now());
        userEvent.setSource("security");
        userEvent.setDestination("all");
        return userEvent;
    }

    public static UserEvent userLogout(String id, String userName) {
        UserEvent userEvent = new UserEvent();
        userEvent.setEventId(UUID.randomUUID().toString());
        userEvent.setEventType("USER_EVENT");
        userEvent.setId(id);
        userEvent.setUserName(userName);
        userEvent.setAction("LOGOUT");
        userEvent.setTimestamp(LocalDateTime.now());
        userEvent.setSource("security");
        userEvent.setDestination("all");
        return userEvent;
    }

    public static UserEvent userRegistered(String id, String userName) {
        UserEvent userEvent = new UserEvent();
        userEvent.setEventId(UUID.randomUUID().toString());
        userEvent.setEventType("USER_EVENT");
        userEvent.setId(id);
        userEvent.setUserName(userName);
        userEvent.setAction("REGISTERED");
        userEvent.setTimestamp(LocalDateTime.now());
        userEvent.setSource("security");
        userEvent.setDestination("all");
        return userEvent;
    }
}
