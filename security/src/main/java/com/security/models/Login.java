package com.security.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Login {
    @NotBlank(message = "{validate.userName.notBlank}")
    private String username;
    
    @NotBlank(message = "{validate.password.notBlank}")
    private String password;
}
