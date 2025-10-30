package com.kafka_shared.models;

public interface EventMetadata {
    
    // lấy id của entity
    String getEntityId();
    
    // lấy name của entity
    String getEntityDisplayName();
    
    // lấy action của event
    String getEventAction();
    
    // lấy type của entity
    default String getEntityType() {
        String className = this.getClass().getSimpleName();
        return className.substring(0, className.length() - 5).toLowerCase() + "Event";
    }
}
