package com.security.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CountryCode(
    @NotBlank(message = "{validate.countryCode.notBlank}")
    @NotNull(message = "{validate.countryCode.notNull}")
    String countryCode
) {}
