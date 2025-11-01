package com.model_shared.models.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import com.model_shared.enums.Gender;
import java.util.Map;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    @NotNull(message = "{validate.user.userId.notNull}")
    private Integer userId;

    @NotBlank(message = "{validate.user.firstName.notBlank}")
    @NotNull(message = "{validate.user.firstName.notNull}")
    private String firstName;

    @NotNull(message = "{validate.user.lastName.notNull}")
    @NotBlank(message = "{validate.user.lastName.notBlank}")
    private String lastName;

    @NotNull(message = "{validate.user.age.notNull}")
    @Min(value = 1, message = "{validate.user.age.min}")
    @Max(value = 99, message = "{validate.user.age.max}")
    private Integer age;

    @NotNull(message = "{validate.user.gender.notNull}")
    private Gender gender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "{validate.user.birth.notNull}")
    @PastOrPresent(message = "{validate.user.birth.pastOrPresent}")
    private LocalDate birth;

    private Map<String, Object> profileData;
}

