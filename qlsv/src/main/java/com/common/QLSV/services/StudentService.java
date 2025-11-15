package com.common.QLSV.services;

import com.model_shared.models.user.EntityModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.model_shared.models.user.UserDto;

import java.util.List;

public interface StudentService {
    List<EntityModel> gets();
    void createByUserId(UserDto user);
    EntityModel update(UpdateEntityModel req);
    Boolean deletes(List<Integer> userIds);
    // PagedResponseModel<StudentModel> getsPaged(PagedRequestModel paginationRequest);
    // List<StudentModel> filter(Integer id, String firstName, String lastName, Integer age, Gender gender, Boolean graduate);
}
