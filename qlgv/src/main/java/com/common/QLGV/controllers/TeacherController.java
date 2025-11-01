package com.common.QLGV.controllers;

import com.common.QLGV.entities.TeacherEntity;
import com.security_shared.annotations.RequiresAuth;
import com.security_shared.annotations.CurrentUser;
import com.model_shared.models.Response;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.model_shared.models.teacher.TeacherModel;
import com.model_shared.models.user.UserDto;
import com.common.QLGV.services.imp.TeacherServiceImp;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.model_shared.enums.Gender;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    @Autowired
    TeacherServiceImp teacherServiceImp;
    @Autowired
    ReloadableResourceBundleMessageSource messageSource;
    @Autowired
    LoggingService loggingService;

    private LogContext getLogContext(String methodName) {
            return LogContext.builder()
                    .module("qlgv")
                    .className(this.getClass().getSimpleName())
                    .methodName(methodName)
                    .build();
    }

//     @GetMapping("")
//     @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
//     ResponseEntity<Response<List<TeacherModel>>> get(
//             @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
//             , @CurrentUser UserDto currentUser)
//     {
//         Locale locale = Locale.forLanguageTag(acceptLanguage);
//         LogContext logContext = getLogContext("get");

//         loggingService.logInfo("Get teachers API called successfully by user: " + currentUser.getUsername()
//                 , logContext);

//         List<TeacherModel> teacherModels = teacherServiceImp.gets();
//         Response<List<TeacherModel>> response = new Response<>(
//                 200,
//                 messageSource.getMessage("response.message.getSuccess", null,locale),
//                 "TeacherModel",
//                 null,
//                 teacherModels
//         );
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     @PostMapping("")
//     @RequiresAuth(roles = {"ADMIN","TEACHER"}, permissions = {"TEACHER_WRITE"})
//     ResponseEntity<Response<List<CreateTeacherModel>>> add(
//             @Valid @RequestBody CreateTeacherModelRequest req
//             , @RequestHeader(value = "Accept-Language"
//             , defaultValue = "en") String acceptLanguage
//             , @CurrentUser UserDto currentUser)
//     {
//         Locale locale = Locale.forLanguageTag(acceptLanguage);
//         LogContext logContext = getLogContext("add");

//         List<CreateTeacherModel> createTeacherModels = req.getTeachers();

//         loggingService.logInfo("Create teachers API called successfully by user : " + currentUser.getUsername()
//                 , logContext);

//         List<TeacherEntity> studentEntities = teacherServiceImp.creates(createTeacherModels);
//         Response<List<CreateTeacherModel>> response = new Response<>(
//                 200
//                 , messageSource.getMessage("response.message.createSuccess", null, locale)
//                 , "TeacherModel"
//                 , null
//                 , createTeacherModels
//         );
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     @PutMapping("")
//     @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_WRITE"})
//     ResponseEntity<Response<List<TeacherModel>>> update(
//             @Valid @RequestBody TeacherModelRequest req
//             , @RequestHeader(value = "Accept-Language"
//             , defaultValue = "en") String acceptLanguage
//             , @CurrentUser UserDto currentUser)
//     {
//         Locale locale = Locale.forLanguageTag(acceptLanguage);
//         LogContext logContext = getLogContext("update");

//         List<TeacherModel> teacherModels = req.getTeachers();

//         loggingService.logInfo("Update teachers API called successfully by user : " + currentUser.getUsername()
//                 , logContext);

//         List<TeacherEntity> studentEntities = teacherServiceImp.updates(teacherModels);
//         Response<List<TeacherModel>> response = new Response<>(
//                 200
//                 , messageSource.getMessage("response.message.updateSuccess", null, locale)
//                 , "TeacherModel"
//                 , null
//                 , teacherModels
//         );
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     @DeleteMapping("")
//     @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_DELETE"})
//     ResponseEntity<Response<List<TeacherModel>>> delete(
//             @RequestBody List<TeacherModel> studentModels
//             , @RequestHeader(value = "Accept-Language"
//             , defaultValue = "en") String acceptLanguage
//             , @CurrentUser UserDto currentUser)
//     {
//         Locale locale = Locale.forLanguageTag(acceptLanguage);
//         LogContext logContext = getLogContext("delete");


//         loggingService.logInfo("Delete teachers API called successfully by user : " + currentUser.getUsername()
//                 , logContext);

//         teacherServiceImp.deletes(studentModels);
//         Response<List<TeacherModel>> response = new Response<>(
//                 200
//                 , messageSource.getMessage("response.message.deleteSuccess", null, locale)
//                 , "TeacherModel",
//                 null,
//                 null
//         );
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     @GetMapping("/paged")
//     @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
//     public ResponseEntity<Response<PagedResponseModel<TeacherModel>>> getPaged(
//                 @RequestParam(defaultValue = "0") int page,
//                 @RequestParam(defaultValue = "3") int size,
//                 @RequestParam(defaultValue = "id") String sortBy,
//                 @RequestParam(defaultValue = "asc") String sortDirection,
//                 @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
//                 @CurrentUser UserDto currentUser)
//     {
//         Locale locale = Locale.forLanguageTag(acceptLanguage);
//         LogContext logContext = getLogContext("getPaged");

//         loggingService.logInfo(String.format(
//                 "Get paged teachers API called successfully by user: %s - Page: %d, Size: %d, Sort: %s : %s", 
//                 currentUser.getUsername(), page, size, sortBy, sortDirection), logContext);
        
//         PagedRequestModel pagedRequest = new PagedRequestModel(page, size, sortBy, sortDirection);
//         PagedResponseModel<TeacherModel> pagedResponse = teacherServiceImp.getsPaged(pagedRequest);

//         Response<PagedResponseModel<TeacherModel>> response = new Response<>(
//                 200,
//                 messageSource.getMessage("response.message.getSuccess", null, locale),
//                 "TeacherModel",
//                 null,
//                 pagedResponse
//         );

//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     @GetMapping("/filter")
//     @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
//     public ResponseEntity<Response<List<TeacherModel>>> filter(
//         @RequestParam(required = false) Integer id,
//         @RequestParam(required = false) String firstName,
//         @RequestParam(required = false) String lastName,
//         @RequestParam(required = false) Integer age,
//         @RequestParam(required = false) Gender gender,
//         @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
//         @CurrentUser UserDto currentUser
//     ){
//         Locale locale = Locale.forLanguageTag(acceptLanguage);
//         LogContext logContext = getLogContext("filter");

//         loggingService.logInfo("Filter teachers API called successfully by user: " + currentUser.getUsername(), logContext);
//         List<TeacherModel> teacherModels = teacherServiceImp.filter(id, firstName, lastName, age, gender);
//         Response<List<TeacherModel>> response = new Response<>(
//                 200,
//                 messageSource.getMessage("response.message.getSuccess", null, locale),
//                 "TeacherModel",
//                 null,
//                 teacherModels
//         );
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

}
