package com.handle_exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class ValidationExceptionHandle extends RuntimeException {
    private String message;
    private String details;
    private List<String> invalidFields;
    private String modelName;
    
    public ValidationExceptionHandle(String message, List<String> invalidFields, String modelName) {
        super(message);
        this.message = message;
        this.invalidFields = invalidFields;
        this.modelName = modelName;
    }
    
    public ValidationExceptionHandle(String message, String details) {
        super(message);
        this.message = message;
        this.details = details;
    }
}

