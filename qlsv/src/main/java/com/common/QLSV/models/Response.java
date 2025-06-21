package com.common.QLSV.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T>{
    private int status;
    private String message;
    private String modelName;
    private Map<String,String> errors;
    private T data;
}
