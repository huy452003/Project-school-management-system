package com.common.QLGV.controllers;

import com.common.QLGV.entities.TeacherEntity;
import com.common.QLGV.services.imp.StudentClientServiceImp;
import com.common.models.CreateTeacherAndStudent;
import com.common.models.Response;
import com.common.models.student.StudentModel;
import com.common.models.teacher.CreateTeacherModel;
import com.common.models.teacher.TeacherModel;
import com.common.models.teacher.request.CreateTeacherModelRequest;
import com.common.models.teacher.request.TeacherModelRequest;
import com.common.QLGV.services.imp.TeacherServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    @Autowired
    TeacherServiceImp teacherServiceImp;
    @Autowired
    ReloadableResourceBundleMessageSource messageSource;
    @Autowired
    StudentClientServiceImp studentClientServiceImp;

    @GetMapping("/studentsByTeacher")
    public Response<List<StudentModel>> getStudentsFromQLSV() {
        try {
            List<StudentModel> studentModels = studentClientServiceImp.getAllStudents();
            return new Response<>(
                    200,
                    "Lấy danh sách sinh viên",
                    "StudentModel",
                    null,
                    studentModels
            );
        } catch (RuntimeException ex) {
            return new Response<>(
                    500,
                    "Lỗi khi gọi đến QLSV",
                    "StudentModel",
                    Map.of("error", ex.getMessage()),
                    null
            );
        }
    }

    @PostMapping("/teachersAndStudents")
    ResponseEntity<Response<?>> createTeachersAndStudents(
            @RequestBody @Valid CreateTeacherAndStudent request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language
    ) {
        Locale locale = Locale.forLanguageTag(language);
        try {
            studentClientServiceImp.createTeacherAndStudent(request);
            return ResponseEntity.status(200).body(new Response<>(
                    200,
                    messageSource.getMessage("response.message.createSuccess",null,locale),
                    "TeacherModel-StudentModel",
                    null,
                    new CreateTeacherAndStudent(
                            request.getTeachers(),
                            request.getStudents()
                    )
            ));
        }catch (RuntimeException e){
            return ResponseEntity.status(500).body(new Response<>(
                    500,
                    messageSource.getMessage(
                            "response.error.createTeacherAndStudentFail",null,locale
                    ),
                    "TeacherModel-StudentModel",
                    Map.of(
                            "errors: " , e.getClass().getSimpleName()
                    ),
                    null
            ));
        }

    }

    @GetMapping("")
    ResponseEntity<Response<List<TeacherModel>>> get(
            @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<TeacherModel> teacherModels = teacherServiceImp.gets();
        Response<List<TeacherModel>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.getSuccess", null,locale),
                "TeacherModel",
                null,
                teacherModels
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("")
    ResponseEntity<Response<List<CreateTeacherModel>>> add(
            @Valid @RequestBody CreateTeacherModelRequest req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<CreateTeacherModel> createTeacherModels = req.getTeachers();
        List<TeacherEntity> studentEntities = teacherServiceImp.creates(createTeacherModels);
        Response<List<CreateTeacherModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.createSuccess", null, locale)
                , "TeacherModel"
                , null
                , createTeacherModels
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("")
    ResponseEntity<Response<List<TeacherModel>>> update(
            @Valid @RequestBody TeacherModelRequest req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<TeacherModel> teacherModels = req.getTeachers();
        List<TeacherEntity> studentEntities = teacherServiceImp.updates(teacherModels);
        Response<List<TeacherModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.updateSuccess", null, locale)
                , "TeacherModel"
                , null
                , teacherModels
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("")
    ResponseEntity<Response<List<TeacherModel>>> delete(
            @RequestBody List<TeacherModel> studentModels
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        teacherServiceImp.deletes(studentModels);
        Response<List<TeacherModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.deleteSuccess", null, locale)
                , "TeacherModel",
                null,
                null
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
