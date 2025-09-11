package com.logging.services.imp;

import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import com.logging.models.LogContext;
import com.logging.services.LoggingService; 

@Service
@Log4j2
public class LoggingServiceImp implements LoggingService{
    @Override
    public void logError(String message, Exception exception, LogContext context) {
        log.error(
            "[{}] {} - {}: {}",
            context.getModule(),
            context.getClassName(),
            message,
            exception != null ? exception.getMessage() : "No exception");
    }
    
    @Override
    public void logInfo(String message, LogContext context) {
        log.info(
            "[{}] {} - {}",
            context.getModule(),
            context.getClassName(),
            message
        );
    }
    
    @Override
    public void logWarn(String message, LogContext context) {
        log.warn(
            "[{}] {} - {}",
            context.getModule(),
            context.getClassName(),
            message
        );
    }
    
    @Override
    public void logDebug(String message, LogContext context) {
        log.debug(
            "[{}] {} - {}",
            context.getModule(),
            context.getClassName(),
            message
        );
    }

    @Override
    public void logTeacherOperation(String operation, String teacherId, LogContext context) {
        log.info(
            "[{}] {} - Teacher ID: {}, Operation: {}",
            context.getModule(),
            context.getClassName(),
            teacherId,
            operation
        );
    }

    @Override
    public void logStudentOperation(String operation, String studentId, LogContext context) {
        log.info(
            "[{}] {} - Student ID: {}, Operation: {}",
            context.getModule(),
            context.getClassName(),
            studentId,
            operation
        );
    }
}

