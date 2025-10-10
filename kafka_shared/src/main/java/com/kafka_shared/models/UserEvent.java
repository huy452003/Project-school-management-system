package com.kafka_shared.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserEvent extends KafkaMessage implements EventMetadata {
    private String userName;
    private String action; // REGISTERED, LOGIN, LOGOUT
    private String role;
    private List<String> permissions;
    
    // Constructor để gọi parent constructor
    public UserEvent(String eventType, String source, String destination) {
        super(eventType, source, destination);
    }

    @Override
    public String getEntityId() {
        return this.userName;
    }

    @Override
    public String getEntityDisplayName() {
        return this.userName;
    }

    @Override
    public String getEventAction() {
        return this.action;
    }

    public static UserEvent userRegistered(String userName, String role, List<String> permissions) {
        UserEvent userEvent = new UserEvent(
            "USER_EVENT", 
            "security", 
            "all");
        userEvent.setUserName(userName);
        userEvent.setAction("REGISTERED");
        userEvent.setRole(role);
        userEvent.setPermissions(permissions);
        return userEvent;
    }

    public static UserEvent userLogin(String userName, String role, List<String> permissions) {
        UserEvent userEvent = new UserEvent(
            "USER_EVENT", 
            "security", 
            "all");
        userEvent.setUserName(userName);
        userEvent.setAction("LOGIN");
        userEvent.setRole(role);
        userEvent.setPermissions(permissions);
        return userEvent;
    }

    public static UserEvent userLogout(String userName, String role, List<String> permissions) {
        UserEvent userEvent = new UserEvent(
            "USER_EVENT", 
            "security", 
            "all");
        userEvent.setUserName(userName);
        userEvent.setAction("LOGOUT");
        userEvent.setRole(role);
        userEvent.setPermissions(permissions);
        return userEvent;
    }

}
