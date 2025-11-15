package com.common.QLGV.controllers;

import com.security_shared.annotations.RequiresAuth;
import com.security_shared.annotations.CurrentUser;
import com.model_shared.models.Response;
import com.model_shared.models.pages.PagedRequestModel;
import com.model_shared.models.pages.PagedResponseModel;
import com.model_shared.models.user.UserDto;
import com.model_shared.models.user.EntityModel;
import com.model_shared.models.user.UpdateEntityModel;
import com.common.QLGV.services.imp.TeacherServiceImp;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_READ"})
    ResponseEntity<Response<List<EntityModel>>> get(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage
            , @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("get");

        loggingService.logInfo("Get teachers API called successfully by user: " + currentUser.getUsername()
                , logContext);

        List<EntityModel> teacherModels = teacherServiceImp.gets();
        Response<List<EntityModel>> response = new Response<>(
                200,
                messageSource.getMessage("response.message.getSuccess", null,locale),
                "TeacherModel",
                null,
                teacherModels
        );
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/{id}")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_WRITE"})
    ResponseEntity<Response<EntityModel>> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateEntityModel req
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage
            , @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("update");

        loggingService.logInfo("Update teachers API called successfully by user : " + currentUser.getUsername()
                , logContext);
        req.setId(id);

        EntityModel teacherModel = teacherServiceImp.update(req);
        Response<EntityModel> response = new Response<>(
                200
                , messageSource.getMessage("response.message.updateSuccess", null, locale)
                , "TeacherModel"
                , null
                , teacherModel
        );
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("")
    @RequiresAuth(roles = {"ADMIN", "TEACHER"}, permissions = {"TEACHER_DELETE"})
    ResponseEntity<Response<List<EntityModel>>> delete(
            @RequestBody List<Integer> userIds
            , @RequestHeader(value = "Accept-Language"
            , defaultValue = "en") String acceptLanguage
            , @CurrentUser UserDto currentUser)
    {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("delete");

        loggingService.logInfo("Delete teachers API called successfully by user : " + currentUser.getUsername()
                , logContext);

        teacherServiceImp.deletes(userIds);
        Response<List<EntityModel>> response = new Response<>(
                200
                , messageSource.getMessage("response.message.deleteSuccess", null, locale)
                , "TeacherModel",
                null,
                null
        );
        return ResponseEntity.status(response.status()).body(response);
    }

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
