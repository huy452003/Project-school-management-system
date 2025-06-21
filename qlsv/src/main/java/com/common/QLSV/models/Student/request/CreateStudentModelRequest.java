package com.common.QLSV.models.Student.request;

import com.common.QLSV.models.Student.CreateStudentModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudentModelRequest {
    @NotEmpty(message = "{validate.list.notEmpty}")
    @Valid
    private List<CreateStudentModel> students;
}
