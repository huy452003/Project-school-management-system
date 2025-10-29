package com.model_shared.models.student;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.model_shared.models.user.UserDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class StudentModel {
    @NotNull(message = "{validate.id.notNull}")
    @Min(value = 1, message = "{validate.id.min}")
    private Integer id;

    private UserDto user;
}
