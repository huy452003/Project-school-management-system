package com.model_shared.models.student.request;

import com.model_shared.models.student.StudentModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentModelRequest {
    @NotEmpty(message = "{validate.list.notEmpty}")
    @Valid
    private List<StudentModel> students;
}
