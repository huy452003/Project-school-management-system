package com.common.QLGV.services;

import com.model_shared.models.CreateTeacherAndStudent;
import com.model_shared.models.student.StudentModel;

import java.util.List;

public interface StudentClientService {
    List<StudentModel> getAllStudents();
//    void createTeacherAndStudent(CreateTeacherAndStudent createTeacherAndStudent);
}
