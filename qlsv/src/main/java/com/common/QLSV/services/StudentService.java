package com.common.QLSV.services;

import com.model_shared.models.user.EntityModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.model_shared.models.user.UserDto;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.model_shared.enums.Gender;

import java.util.List;

public interface StudentService {
    List<EntityModel> gets();
    EntityModel getByUserId(Integer userId);
    List<EntityModel> getBySchoolClass(String schoolClass);
    void createByUserId(UserDto user);
    EntityModel update(UpdateEntityModel req, UserDto currentUser);
    Boolean deletes(List<Integer> userIds);
    void clearCache();
    PagedResponseModel<EntityModel> getsPaged(PagedRequestModel paginationRequest);
    List<EntityModel> filter(
        Integer id, String firstName, String lastName,
        Integer age, Gender gender, String email, 
        String phoneNumber,Double score, String schoolClass,
        String major, Boolean graduate 
    );
}
