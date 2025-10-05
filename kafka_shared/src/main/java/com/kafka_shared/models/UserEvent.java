package com.kafka_shared.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserEvent extends KafkaMessage implements EventMetadata {
    private String id;
    private String userName;
    private String action; // CREATED, LOGIN, LOGOUT, REGISTERED
    
    // Constructor để gọi parent constructor
    public UserEvent(String eventType, String source, String destination) {
        super(eventType, source, destination);
    }

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

    public static UserEvent userRegistered(String id, String userName) {
        UserEvent userEvent = new UserEvent(
            "USER_EVENT", 
            "security", 
            "all");
        userEvent.setId(id);
        userEvent.setUserName(userName);
        userEvent.setAction("REGISTERED");
        return userEvent;
    }

    public static UserEvent userLogin(String id, String userName) {
        UserEvent userEvent = new UserEvent(
            "USER_EVENT", 
            "security", 
            "all");
        userEvent.setId(id);
        userEvent.setUserName(userName);
        userEvent.setAction("LOGIN");
        return userEvent;
    }

    public static UserEvent userLogout(String id, String userName) {
        UserEvent userEvent = new UserEvent(
            "USER_EVENT", 
            "security", 
            "all");
        userEvent.setId(id);
        userEvent.setUserName(userName);
        userEvent.setAction("LOGOUT");
        return userEvent;
    }

}
