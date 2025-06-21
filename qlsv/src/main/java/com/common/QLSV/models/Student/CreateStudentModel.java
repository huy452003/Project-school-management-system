package com.common.QLSV.models.Student;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.common.QLSV.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CreateStudentModel {
    @NotBlank(message = "{validate.firstName.notBlank}")
    @Size(min = 1, max = 20,message = "{validate.firstName.size}")
    private String firstName;

    @NotBlank(message = "{validate.lastName.notBlank}")
    @Size(min = 1, max = 20,message = "{validate.lastName.size}")
    private String lastName;

    @NotNull(message = "{validate.age.notNull}")
    @Min(value = 1, message = "{validate.age.min}")
    @Max(value = 99, message = "{validate.age.max}")
    private Integer age;

    @NotNull(message = "{validate.gender.invalidType}")
    private Gender gender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @PastOrPresent(message = "{validate.birth.check}")
    @NotNull(message = "{validate.birth.notNull}")
    private LocalDate birth;

    @NotNull(message = "{validate.graduate.notNull}")
    private Boolean graduate;
}

