package com.security.models;

import jakarta.validation.constraints.NotBlank;


public record Login(
    @NotBlank(message = "{validate.userName.notBlank}")
    String username,
    @NotBlank(message = "{validate.password.notBlank}")
    String password
) {}
