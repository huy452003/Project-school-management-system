package com.common.QLSV.exceptions;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class NotFoundIDStudentsException extends RuntimeException{
    private final List<Integer> notFoundIDStudents;
    public NotFoundIDStudentsException(String message , List<Integer> notFoundIDStudents){
        super(message);
        this.notFoundIDStudents = notFoundIDStudents;
    }

}
