package com.common.QLGV.services;

import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.model_shared.models.Response;
import com.model_shared.models.user.EntityModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class StudentServiceFromTeacher {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private LoggingService loggingService;
    
    @Value("${qlsv.base-url:http://localhost:8081}")
    private String qlsvBaseUrl;
    
    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("qlgv")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }
    
    public List<EntityModel> getStudentsBySchoolClass(String schoolClass, String authToken) {
        LogContext logContext = getLogContext("getStudentsBySchoolClass");
        
        if (schoolClass == null || schoolClass.trim().isEmpty()) {
            loggingService.logWarn("School class is null or empty", logContext);
            return Collections.emptyList();
        }
        
        try {
            String url = qlsvBaseUrl + "/students/by-class/" + schoolClass.trim();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            loggingService.logInfo("Calling QLSV API: " + url + " for schoolClass: " + schoolClass, logContext);
            
            ResponseEntity<Response<List<EntityModel>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new org.springframework.core.ParameterizedTypeReference<Response<List<EntityModel>>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Response<List<EntityModel>> responseBody = response.getBody();
                if (responseBody != null && responseBody.data() != null) {
                    List<EntityModel> students = responseBody.data();
                    loggingService.logInfo("Successfully retrieved " + students.size() + 
                        " students for schoolClass: " + schoolClass, logContext);
                    return students;
                }
            }
            loggingService.logWarn("QLSV API returned non-2xx status or empty body: " + 
                (response.getBody() != null ? response.getStatusCode() : "null body"), logContext);
            return Collections.emptyList();
        } catch (Exception e) {
            loggingService.logError("Failed to get students by schoolClass: " + schoolClass, e, logContext);
            return Collections.emptyList();
        }
    }
}

