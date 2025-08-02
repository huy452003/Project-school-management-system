package com.common.QLGV.services;

import com.common.models.CreateTeacherAndStudent;
import com.common.models.student.StudentModel;

import java.util.List;

public interface StudentClientService {
    List<StudentModel> getAllStudents();
    void createTeacherAndStudent(CreateTeacherAndStudent createTeacherAndStudent);
}
