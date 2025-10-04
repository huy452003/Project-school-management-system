package com.kafka_shared.models;

/**
 * Interface for event metadata extraction
 * Replaces reflection-based helper methods with a clean contract
 */
public interface EventMetadata {
    
    /**
     * Get the entity ID from the event
     * @return entity ID
     */
    String getEntityId();
    
    /**
     * Get the entity display name from the event
     * @return entity display name
     */
    String getEntityDisplayName();
    
    /**
     * Get the event action from the event
     * @return event action
     */
    String getEventAction();
    
    /**
     * Get the entity type based on class name
     * @return entity type (e.g., "student", "teacher", "user")
     */
    default String getEntityType() {
        String className = this.getClass().getSimpleName();
        if (className.endsWith("Event")) {
            return className.substring(0, className.length() - 5).toLowerCase() + "Event";
        }
        return className.toLowerCase();
    }
}
