package com.logging.services;

import com.logging.models.LogContext;

public interface LoggingService {
    void logError(String message, Exception exception, LogContext context);
    void logInfo(String message, LogContext context);
    void logWarn(String message, LogContext context);
    void logDebug(String message, LogContext context);

    void logTeacherOperation(String operation, int teacherId, LogContext context);
    void logStudentOperation(String operation, int studentId, LogContext context);
}
