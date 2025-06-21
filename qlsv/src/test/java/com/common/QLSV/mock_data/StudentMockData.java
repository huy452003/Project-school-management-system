//package com.common.QLSV.mock_data;
//
//import com.common.QLSV.entities.StudentEntity;
//import com.common.QLSV.enums.Gender;
//import com.common.QLSV.models.Student.CreateStudentModel;
//import com.common.QLSV.models.Student.StudentModel;
//import org.modelmapper.ModelMapper;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//public class StudentMockData {
//
//    public static ModelMapper modelMapper = new ModelMapper();
//
//    public static List<StudentModel> mockStudentModels(){
//        StudentModel student = new StudentModel();
//        student.setId(1);
//        student.setFirstName("Test");
//        student.setLastName("Student");
//        student.setAge(20);
//        student.setGender(Gender.NAM);
//        student.setBirth(LocalDate.parse("04-05-2021", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//        student.setGraduate(false);
//        return List.of(student);
//    }
//
//    public static List<CreateStudentModel> mockStudentModelCreates(){
//        CreateStudentModel student = new CreateStudentModel();
//        student.setFirstName("Test");
//        student.setLastName("Student");
//        student.setAge(20);
//        student.setGender(Gender.NAM);
//        student.setBirth(LocalDate.parse("04-05-2021", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//        student.setGraduate(true);
//        return List.of(student);
//    }
//
//    public static List<StudentEntity> mockStudentEntities(){
//        StudentEntity studentEntity = modelMapper.map(mockStudentModels().get(0), StudentEntity.class);
//        return List.of(studentEntity);
//    }
//}
