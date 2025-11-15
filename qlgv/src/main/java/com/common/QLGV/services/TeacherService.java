package com.common.QLGV.services;

import com.model_shared.models.user.EntityModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.model_shared.models.user.UserDto;


import java.util.List;

public interface TeacherService {
    // public PagedResponseModel<TeacherModel> getsPaged(PagedRequestModel pagedRequest);
    // public List<TeacherModel> filter(Integer id, String firstName, String lastName, Integer age, Gender gender);
    List<EntityModel> gets();
    void createByUserId(UserDto user);
    EntityModel update(UpdateEntityModel teacherUpdate);
    Boolean deletes(List<Integer> userIds);
}
