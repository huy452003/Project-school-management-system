package com.common.QLSV.services;

import com.common.QLSV.entities.StudentEntity;
import com.model_shared.models.pages.PagedResponse;
import com.model_shared.models.pages.PaginationRequest;
import com.model_shared.models.student.CreateStudentModel;
import com.model_shared.models.student.StudentModel;

import java.util.List;

public interface StudentService {
    List<StudentModel> gets();
    PagedResponse<StudentModel> getsPaged(PaginationRequest paginationRequest);
    List<StudentEntity> creates(List<CreateStudentModel> studentModels);
    List<StudentEntity> updates(List<StudentModel> studentModels);
    Boolean deletes(List<StudentModel> StudentModel);
}
