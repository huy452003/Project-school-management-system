package com.common.QLGV.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.common.models.Response;
import com.common.models.teacher.TeacherModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class CustomExceptionHandler {
    @Autowired
    MessageSource messageSource;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundTeacherException.class)
    ResponseEntity<Response<TeacherModel>> notFoundExceptionHandler(NotFoundTeacherException e) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> error = new HashMap<>();
        error.put("Error", "ID:" + e.getNotFoundIDTeacher().toString());
        Response<TeacherModel> response = new Response<>(
                404,
                messageSource.getMessage("response.error.notFoundError", null, locale),
                "TeacherModel",
                error,
                null
        );
        return ResponseEntity.status(404).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<?>> handleBodyValidation(MethodArgumentNotValidException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fe -> {
            String key = fe.getDefaultMessage();
            if (key.startsWith("{") && key.endsWith("}")) {
                key = key.substring(1, key.length() - 1);
            }
            String msg = messageSource.getMessage(key, null, key, locale);
            errors.put(fe.getField(), msg);
        });

        Response<?> resp = new Response<>(
                400,
                messageSource.getMessage("response.error.validate", null, locale),
                "TeacherModel",
                errors,
                null
        );
        return ResponseEntity.badRequest().body(resp);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<TeacherModel>> handleInvalidFormat(HttpMessageNotReadableException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> errors = new HashMap<>();
        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof InvalidFormatException || cause instanceof MismatchedInputException) {
            List<JsonMappingException.Reference> path = ((JsonMappingException) cause).getPath();
            if (!path.isEmpty()) {
                String field = path.get(path.size() - 1).getFieldName();
                String key = String.format("validate.%s.invalidType", field);
                String msg = messageSource.getMessage(key, null,
                        field + " không đúng định dạng.", locale);
                errors.put(field, msg);
            }
        } else {
            errors.put("error", messageSource.getMessage(
                    "response.error.validate", null, locale));
        }

        Response<TeacherModel> response = new Response<>(
                400,
                messageSource.getMessage("response.error.validate", null, locale),
                "TeacherModel",
                errors,
                null
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    ResponseEntity<Response<TeacherModel>> exceptionHandler(Exception e) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> error = new HashMap<>();
        error.put("Error", "INTERNAL_SERVER_ERROR");
        Response<TeacherModel> response = new Response<>(
                500,
                messageSource.getMessage("response.error.internalServerError", null, locale),
                "TeacherModel",
                error,
                null
        );
        return ResponseEntity.status(500).body(response);
    }
}
