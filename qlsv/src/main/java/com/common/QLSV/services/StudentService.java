package com.common.QLSV.services;

import com.model_shared.models.pages.PagedResponseModel;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.student.StudentModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.model_shared.models.user.UserDto;

import java.util.List;

public interface StudentService {
    List<StudentModel> gets();
    void createByUserId(UserDto user);
    StudentModel update(UpdateEntityModel req);
    // Boolean deletes(List<StudentModel> StudentModel);
    // PagedResponseModel<StudentModel> getsPaged(PagedRequestModel paginationRequest);
    // List<StudentModel> filter(Integer id, String firstName, String lastName, Integer age, Gender gender, Boolean graduate);
}
