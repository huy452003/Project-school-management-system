package com.security.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.model_shared.enums.Gender;
import com.security.enums.Type;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Register {

    @NotNull(message = "{validate.type.notBlank}")
    private Type type;

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
    
    @NotNull(message = "{validate.age.notNull}")
    @Min(value = 1, message = "{validate.age.min}")
    @Max(value = 99, message = "{validate.age.max}")
    private Integer age;

    @NotNull(message = "{validate.gender.notNull}")
    private Gender gender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @PastOrPresent(message = "{validate.birth.check}")
    @NotNull(message = "{validate.birth.notNull}")
    private LocalDate birth;
    
    @NotBlank(message = "{validate.role.notBlank}")
    @Pattern(regexp = "^(ADMIN|TEACHER|STUDENT)$", message = "{validate.role.invalidType}")
    private String role;
    
    private List<String> permissions;
    
    private Map<String, Object> profileData;
}
