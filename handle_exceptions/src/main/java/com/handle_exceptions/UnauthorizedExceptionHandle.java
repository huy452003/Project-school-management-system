package com.handle_exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnauthorizedExceptionHandle extends RuntimeException {
    private String message;
    private String details;
    
    public UnauthorizedExceptionHandle(String message) {
        this.message = message;
    }
} 