package com.common.QLSV.services;

import com.common.QLSV.entities.StudentEntity;
import com.common.QLSV.models.Student.CreateStudentModel;
import com.common.QLSV.models.Student.StudentModel;

import java.util.List;

public interface StudentService {
    List<StudentModel> gets();
    List<StudentEntity> creates(List<CreateStudentModel> studentModels);
    List<StudentEntity> updates(List<StudentModel> studentModels);
    Boolean deletes(List<StudentModel> StudentModel);
}
