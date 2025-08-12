package com.logging.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogContext {
    private String module;
    private String className;
    private String methodName;
    private String userId;
    private String message;
}
