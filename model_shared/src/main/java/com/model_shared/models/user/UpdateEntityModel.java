package com.model_shared.models.user;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class UpdateEntityModel {
    
    private Integer id;

    @NotNull(message = "{validate.user.notNull}")
    @Valid
    private UpdateUserDto user;
}

