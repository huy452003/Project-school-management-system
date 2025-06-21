package com.common.QLGV.services;

import com.common.QLGV.entities.TeacherEntity;
import com.common.models.teacher.CreateTeacherModel;
import com.common.models.teacher.TeacherModel;


import java.util.List;

public interface TeacherService {
    public List<TeacherModel> gets();
    public List<TeacherEntity> creates( List<CreateTeacherModel> createTeacherModels);
    public List<TeacherEntity>  updates(List<TeacherModel> teacherModels);
    public boolean deletes(List<TeacherModel> teacherModels);
}
