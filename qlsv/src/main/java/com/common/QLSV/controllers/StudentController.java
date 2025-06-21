package com.common.QLSV.controllers;

import com.common.QLSV.entities.StudentEntity;
import com.common.QLSV.models.Response;
import com.common.QLSV.models.Student.CreateStudentModel;
import com.common.QLSV.models.Student.request.CreateStudentModelRequest;
import com.common.QLSV.models.Student.request.StudentModelRequest;
import com.common.QLSV.models.Student.StudentModel;
import com.common.QLSV.services.imp.StudentServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    StudentServiceImp studentServiceImp;
    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @GetMapping("")
    ResponseEntity<Response<List<StudentModel>>> get(
            @RequestHeader(value = "Accept-Language"
                    , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<StudentModel> studentModels = studentServiceImp.gets();
            Response<List<StudentModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.getSuccess", null, locale)
                    , "StudentsModel"
                    , null
                    , studentModels
            );
            return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("")
    ResponseEntity<Response<List<CreateStudentModel>>> add(
            @Valid @RequestBody CreateStudentModelRequest req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<CreateStudentModel> studentModels = req.getStudents();
        List<StudentEntity> studentEntities = studentServiceImp.creates(studentModels);
            Response<List<CreateStudentModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.createSuccess", null, locale)
                    , "StudentsModel"
                    , null
                    , studentModels
            );
            return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("")
    ResponseEntity<Response<List<StudentModel>>> update(
            @Valid @RequestBody StudentModelRequest req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        List<StudentModel> studentModels = req.getStudents();
        List<StudentEntity> studentEntities = studentServiceImp.updates(studentModels);
            Response<List<StudentModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.updateSuccess", null, locale)
                    , "StudentsModel"
                    , null
                    , studentModels
            );
            return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("")
    ResponseEntity<Response<List<StudentModel>>> delete(
            @RequestBody List<StudentModel> studentModels
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        studentServiceImp.deletes(studentModels);
            Response<List<StudentModel>> response = new Response<>(
                    200
                    , messageSource.getMessage("response.message.deleteSuccess", null, locale)
                    , "StudentsModel",
                    null,
                    null
            );
            return ResponseEntity.status(response.getStatus()).body(response);
    }
}
