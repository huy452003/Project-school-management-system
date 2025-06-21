//package com.common.QLSV.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.common.QLSV.configuration.StudentTestConfig;
//import com.common.QLSV.entities.StudentEntity;
//import com.common.QLSV.exceptions.NotFoundIDStudentsException;
//import com.common.QLSV.services.imp.StudentServiceImp;
//import static com.common.QLSV.mock_data.StudentMockData.*;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.MessageSource;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import({StudentTestConfig.class})
//@TestPropertySource("/test.properties")
//public class StudentControllerTest {
//    @Autowired
//    private StudentServiceImp studentServiceImp;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    MessageSource messageSource;
//
//
//    @Test
//    public void studentControllerTest_GetSuccess() throws Exception {
//        Mockito.when(studentServiceImp.gets()).thenReturn(mockStudentModels());
//        mockMvc.perform(get("/students")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(200))
//                .andExpect(jsonPath("$.message").value("Get Success."))
//                .andExpect(jsonPath("$.modelName").value("StudentsModel"))
//                .andExpect(jsonPath("$.errors").doesNotExist())
//                .andExpect(jsonPath("$.data[0].id").value(1));
//    }
//
//
//    @Test
//    public void studentControllerTest_GetFail() throws Exception {
//        List<Integer> notFound = List.of(5, 8);
//        Mockito.when(studentServiceImp.gets())
//                .thenThrow(new NotFoundIDStudentsException("", notFound));
//
//        mockMvc.perform(get("/students")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message").value("Not Found."))
//                .andExpect(jsonPath("$.modelName").value("StudentModel"))
//                .andExpect(jsonPath("$.errors.Error").value("ID:" + notFound.toString()))
//                .andExpect(jsonPath("$.data").doesNotExist());
//    }
//
//    @Test
//    public void studentControllerTest_CreateSuccess() throws Exception {
//        List<StudentEntity> saved = mockStudentEntities();
//        Mockito.when(studentServiceImp.creates(Mockito.anyList()))
//                .thenReturn(saved);
//
//        String body = objectMapper.writeValueAsString(
//                Map.of("students", mockStudentModelCreates())
//        );
//        mockMvc.perform(post("/students")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(200))
//                .andExpect(jsonPath("$.message").value("Create Success."))
//                .andExpect(jsonPath("$.modelName").value("StudentsModel"))
//                .andExpect(jsonPath("$.errors").doesNotExist())
//                .andExpect(jsonPath("$.data[0].age").value(20));
//    }
//
////    @Test
////    public void studentControllerTest_CreateFail() throws Exception {
////        Mockito.when(studentServiceImp.creates(Mockito.anyList())).thenReturn(Collections.emptyList());
////
////        String body = objectMapper.writeValueAsString(
////                Map.of("students", mockStudentModelCreates())
////        );
////        mockMvc.perform(post("/students")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(body))
////                .andExpect(status().isBadRequest())
////                .andExpect(jsonPath("$.status").value(400))
////                .andExpect(jsonPath("$.message").value("Validation Failed."))
////                .andExpect(jsonPath("$.modelName").value("StudentsModel"))
////                .andExpect(jsonPath("$.errors.Error").value("Validation Failed."))
////                .andExpect(jsonPath("$.data").doesNotExist());
////    }
//
//    @Test
//    public void studentControllerTest_UpdateSuccess() throws Exception {
//        List<StudentEntity> saved = mockStudentEntities();
//        Mockito.when(studentServiceImp.updates(Mockito.anyList()))
//                .thenReturn(saved);
//
//        String body = objectMapper.writeValueAsString(
//                Map.of("students", mockStudentModels())
//        );
//        mockMvc.perform(put("/students")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(200))
//                .andExpect(jsonPath("$.message").value("Update Success."))
//                .andExpect(jsonPath("$.modelName").value("StudentsModel"))
//                .andExpect(jsonPath("$.errors").doesNotExist())
//                .andExpect(jsonPath("$.data[0].id").value(1));
//    }
//
//    @Test
//    public void studentControlerTest_UpdateFail() throws Exception {
//        List<Integer> notFound = List.of(5, 8);
//        Mockito.when(studentServiceImp.updates(mockStudentModels()))
//                .thenThrow(new NotFoundIDStudentsException("", notFound));
//
//        String body = objectMapper.writeValueAsString(
//                Map.of("students", mockStudentModels())
//        );
//        mockMvc.perform(put("/students")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message").value("Not Found."))
//                .andExpect(jsonPath("$.modelName").value("StudentModel"))
//                .andExpect(jsonPath("$.errors.Error").value("ID:" + notFound.toString()))
//                .andExpect(jsonPath("$.data").doesNotExist());
//    }
//
////    @Test
////    public void studentControllerTest_DeleteSuccess() throws Exception {
////        List<Integer> notFound = List.of(5, 8);
////        Mockito.when(studentServiceImp.gets())
////                .thenThrow(new NotFoundIDStudentsException("", notFound));
////        mockMvc.perform(delete("/students")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(objectMapper.writeValueAsString(mockStudentModels())))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.status").value(200))
////                .andExpect(jsonPath("$.message").value("Delete Success."))
////                .andExpect(jsonPath("$.modelName").value("StudentsModel"))
////                .andExpect(jsonPath("$.errors").doesNotExist())
////                .andExpect(jsonPath("$.data").doesNotExist());
////    }
//
//    @Test
//    public void studentControllerTest_DeleteFail() throws Exception {
//        List<Integer> notFound = List.of(5, 8);
//        Mockito.when(studentServiceImp.deletes(mockStudentModels()))
//                .thenThrow(new NotFoundIDStudentsException("", notFound));
//
//        mockMvc.perform(delete("/students")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(mockStudentModels())))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message").value("Not Found."))
//                .andExpect(jsonPath("$.modelName").value("StudentModel"))
//                .andExpect(jsonPath("$.errors.Error").value("ID:" + notFound.toString()))
//                .andExpect(jsonPath("$.data").doesNotExist());
//    }
//
//}
