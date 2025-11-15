package com.model_shared.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response<T>(
    Integer status,
    String message,
    String modelName,
    Map<String,String> errors,
    T data
) {}
