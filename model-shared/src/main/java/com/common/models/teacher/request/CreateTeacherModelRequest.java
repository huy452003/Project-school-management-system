package com.common.models.teacher.request;

import com.common.models.teacher.CreateTeacherModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeacherModelRequest {
    @NotEmpty(message = "{validate.list.notEmpty}")
    @Valid
    private List<CreateTeacherModel> teachers;

}
