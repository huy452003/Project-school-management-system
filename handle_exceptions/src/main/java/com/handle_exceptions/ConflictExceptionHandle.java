package com.handle_exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConflictExceptionHandle extends RuntimeException {
    private List<String> conflictList;
    public ConflictExceptionHandle(String message, List<String> conflictList) {
        super(message);
        this.conflictList = conflictList;
    }
}
