package com.handle_exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForbiddenExceptionHandle extends RuntimeException {
    private String message;
    private String details;
    private String requiredRoles;
    
    public ForbiddenExceptionHandle(String message) {
        this.message = message;
    }
    
    public ForbiddenExceptionHandle(String message, String requiredRoles) {
        this.message = message;
        this.requiredRoles = requiredRoles;
    }
} 