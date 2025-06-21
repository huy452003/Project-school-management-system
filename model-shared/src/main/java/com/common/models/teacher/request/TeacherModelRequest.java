package com.common.models.teacher.request;

import com.common.models.teacher.TeacherModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherModelRequest {
    @NotEmpty(message = "{validate.list.notEmpty}")
    @Valid
    private List<TeacherModel> teachers;
}
