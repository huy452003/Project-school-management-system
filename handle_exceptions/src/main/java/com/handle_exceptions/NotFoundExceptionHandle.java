package com.handle_exceptions;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class NotFoundExceptionHandle extends RuntimeException {
    private final List<String> listNotFounds;
    private final String modelName;
    public NotFoundExceptionHandle(String message, List<String> listNotFounds, String modelName) {
        super(message);
        this.listNotFounds = listNotFounds;
        this.modelName = modelName;
    }
} 