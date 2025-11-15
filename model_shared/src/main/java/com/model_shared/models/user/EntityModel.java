package com.model_shared.models.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class EntityModel {
    
    private Integer id;

    @NotNull(message = "{validate.user.notNull}")
    private UserDto user;
}
