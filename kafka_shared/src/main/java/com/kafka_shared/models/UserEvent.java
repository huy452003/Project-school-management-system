package com.kafka_shared.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.model_shared.models.user.UserDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserEvent extends KafkaMessage implements EventMetadata {
    private UserDto user;
    private String action; // REGISTERED, LOGIN, LOGOUT
    
    public UserEvent(String eventType, String source, String destination) {
        super(eventType, source, destination);
    }

    @Override
    public String getEntityId() {
        return this.user.getUserId().toString();
    }

    @Override
    public String getEntityDisplayName() {
        return this.user.getUserName();
    }

    @Override
    public String getEventAction() {
        return this.action;
    }

    public static UserEvent userRegistered(UserDto user) {
        UserEvent userEvent = new UserEvent(
            "USER_REGISTERED", 
            "security", 
            "all");
        userEvent.setUser(user);
        userEvent.setAction("REGISTERED");
        return userEvent;
    }

    public static UserEvent ProfileCreated(UserEvent profile) {
        UserEvent userEvent = new UserEvent(
            "PROFILE_CREATED", 
            "security", 
            "all");
        userEvent.setUser(profile.getUser());
        userEvent.setAction("PROFILE_CREATED");
        return userEvent;
    }

    public static UserEvent userLogin(UserDto user) {
        UserEvent userEvent = new UserEvent(
            "USER_LOGIN", 
            "security", 
            "all");
        userEvent.setUser(user);
        userEvent.setAction("LOGIN");
        return userEvent;
    }
    
    public static UserEvent userLogout(UserDto user) {
        UserEvent userEvent = new UserEvent(
            "USER_LOGOUT", 
            "security", 
            "all");
        userEvent.setUser(user);
        userEvent.setAction("LOGOUT");
        return userEvent;
    }

}
