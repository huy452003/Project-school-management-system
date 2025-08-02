package com.common.QLGV.services.imp;

import com.common.QLGV.entities.TeacherEntity;
import com.common.QLGV.repositories.TeacherRepo;
import com.common.QLGV.services.StudentClientService;
import com.common.models.CreateStudentForTeacher;
import com.common.models.CreateTeacherAndStudent;
import com.common.models.Response;
import com.common.models.student.CreateStudentModel;
import com.common.models.student.StudentModel;
import com.common.models.teacher.CreateTeacherModel;
import com.common.models.teacher.TeacherModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class StudentClientServiceImp implements StudentClientService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    TeacherRepo teacherRepo;

    @Cacheable(value = "students")
    @Override
    public List<StudentModel> getAllStudents() {
        String studentApi = "http://localhost:8080/students";
        try {
            ResponseEntity<Response<List<StudentModel>>> response = restTemplate.exchange(
                    studentApi,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody().getData();

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Response<?> errorResponse = objectMapper.readValue(
                        ex.getResponseBodyAsString(),
                        new TypeReference<>() {
                        }
                );

                System.err.println("Lỗi từ QLSV: " + errorResponse.getMessage());
                throw new RuntimeException("QLSV trả về lỗi: " + errorResponse.getMessage());

            } catch (IOException e) {
                System.err.println("Không đọc được body lỗi JSON từ QLSV");
                throw new RuntimeException("QLSV lỗi: " + ex.getStatusCode(), ex);
            }

        } catch (Exception ex) {
            System.err.println("Lỗi kết nối tới QLSV: " + ex.getMessage());
            throw new RuntimeException("Không thể kết nối tới QLSV", ex);
        }
    }

    @Transactional
    @CacheEvict(value = "teachers", allEntries = true)
    @Override
    public void createTeacherAndStudent(CreateTeacherAndStudent createTeacherAndStudent) {
        for (CreateTeacherModel teacherModel : createTeacherAndStudent.getTeachers()) {
            TeacherEntity teacherEntity = modelMapper.map(teacherModel , TeacherEntity.class);
            teacherRepo.save(teacherEntity);
        }
        String studentApi = "http://localhost:8080/students";
        HttpEntity<CreateStudentForTeacher> entity = new HttpEntity<>(
                new CreateStudentForTeacher(createTeacherAndStudent.getStudents())
        );
        ResponseEntity<Response<?>> response = restTemplate.exchange(
                studentApi,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );
    }
}
