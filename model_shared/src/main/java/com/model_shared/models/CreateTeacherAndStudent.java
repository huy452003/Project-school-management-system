package com.model_shared.models;

import com.model_shared.models.student.CreateStudentModel;
import com.model_shared.models.teacher.CreateTeacherModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeacherAndStudent {
    private List<CreateTeacherModel> teachers;
    private List<CreateStudentModel> students;
}
