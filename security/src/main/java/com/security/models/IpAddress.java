package com.security.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IpAddress(
    @NotBlank(message = "{validate.ipAddress.notBlank}")
    @NotNull(message = "{validate.ipAddress.notNull}")
    String ipAddress
) {}
