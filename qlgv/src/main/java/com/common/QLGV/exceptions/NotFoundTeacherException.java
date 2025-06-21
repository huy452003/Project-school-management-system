package com.common.QLGV.exceptions;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class NotFoundTeacherException extends RuntimeException {
    private final List<Integer> notFoundIDTeacher;
    public NotFoundTeacherException(String message, List<Integer> notFoundIDTeacher) {
        super(message);
        this.notFoundIDTeacher = notFoundIDTeacher;
    }
}
