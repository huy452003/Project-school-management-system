package com.common.QLGV.services;

import com.model_shared.models.user.EntityModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.model_shared.models.user.UserDto;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.model_shared.enums.Gender;


import java.util.List;

public interface TeacherService {
    void clearCache();
    List<EntityModel> gets();
    EntityModel getByUserId(Integer userId);
    List<EntityModel> getStudentsByClassManaging(String classManaging, String authToken);
    void createByUserId(UserDto user);
    EntityModel update(UpdateEntityModel teacherUpdate);
    Boolean deletes(List<Integer> userIds);
    public PagedResponseModel<EntityModel> getsPaged(PagedRequestModel pagedRequest);
    List<EntityModel> filter(
        Integer id, String firstName, String lastName,
        Integer age, Gender gender, String email, String phoneNumber,
        String classManaging, String department
    );
}
