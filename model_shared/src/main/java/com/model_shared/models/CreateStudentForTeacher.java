package com.model_shared.models;

import com.model_shared.models.student.CreateStudentModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudentForTeacher {
    private List<CreateStudentModel> students;
}
