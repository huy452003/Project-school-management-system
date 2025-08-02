package com.common.models;

import com.common.models.student.CreateStudentModel;
import com.common.models.teacher.CreateTeacherModel;
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
