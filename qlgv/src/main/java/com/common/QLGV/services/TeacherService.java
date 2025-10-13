package com.common.QLGV.services;

import com.common.QLGV.entities.TeacherEntity;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.model_shared.models.teacher.CreateTeacherModel;
import com.model_shared.models.teacher.TeacherModel;


import java.util.List;

public interface TeacherService {
    public List<TeacherModel> gets();
    public PagedResponseModel<TeacherModel> getsPaged(PagedRequestModel pagedRequest);
    public List<TeacherEntity> creates( List<CreateTeacherModel> createTeacherModels);
    public List<TeacherEntity>  updates(List<TeacherModel> teacherModels);
    public boolean deletes(List<TeacherModel> teacherModels);
}
