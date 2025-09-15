package com.security.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Register {
    @NotBlank(message = "{validate.userName.notBlank}")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "{validate.password.notBlank}")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "{validate.firstName.notBlank}")
    @Size(max = 50, message = "{validate.firstName.size}")
    private String firstName;
    
    @NotBlank(message = "{validate.lastName.notBlank}")
    @Size(max = 50, message = "{validate.lastName.size}")
    private String lastName;
    
    @NotBlank(message = "{validate.role.notBlank}")
    @Pattern(regexp = "^(TEACHER|STUDENT)$", message = "{validate.role.invalidType}")
    private String role;
    
    private List<String> permissions;
}
