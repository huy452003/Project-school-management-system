//package com.common.QLSV.services;
//
//import com.common.QLSV.entities.StudentEntity;
//import com.common.QLSV.exceptions.NotFoundIDStudentsException;
//import com.common.QLSV.models.Student.CreateStudentModel;
//import com.common.QLSV.models.Student.StudentModel;
//import com.common.QLSV.repositories.StudentRepo;
//import com.common.QLSV.services.imp.StudentServiceImp;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static com.common.QLSV.mock_data.StudentMockData.*;
//
//@ExtendWith(MockitoExtension.class)
//public class StudentServiceTest {
//
//    @InjectMocks
//    private StudentServiceImp studentServiceImp;
//
//    @Mock
//    private StudentRepo studentRepo;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @Test
//    public void studentServiceTest_getsSuccess() {
//        Mockito.when(studentRepo.findAll()).thenReturn(mockStudentEntities());
//        Mockito.when(modelMapper.map(Mockito.any(), Mockito.eq(StudentModel.class)))
//                .thenReturn(mockStudentModels().get(0));
//
//        Assertions.assertNotNull(studentServiceImp.gets());
//    }
//
//    @Test
//    public void studentServiceTest_getsFail() {
//        Mockito.when(studentRepo.findAll()).thenReturn(Collections.emptyList());
//        Assertions.assertThrows(NotFoundIDStudentsException.class , () -> studentServiceImp.gets());
//    }
//
//    @Test
//    public void studentServiceTest_createsSuccess() {
//        Mockito.when(modelMapper.map(Mockito.any(), Mockito.eq(StudentEntity.class)))
//                .thenReturn(mockStudentEntities().get(0));
//        Mockito.when(studentRepo.saveAll(Mockito.any())).thenReturn(mockStudentEntities());
//
//        List<StudentEntity> studentEntityList = studentServiceImp.creates(mockStudentModelCreates());
//        Assertions.assertNotNull(studentEntityList);
//        Assertions.assertEquals(studentEntityList.get(0).getLastName()
//                , mockStudentEntities().get(0).getLastName());
//    }
//
//    @Test
//    public void studentServiceTest_createsFail() {
//        List<CreateStudentModel> empty = Collections.emptyList();
//        List<StudentEntity> result = studentServiceImp.creates(empty);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertTrue(result.isEmpty());
//    }
//
//    @Test
//    public void studentServiceTest_updatesSuccess() {
//        Mockito.when(studentRepo.findById(mockStudentEntities().get(0).getId()))
//                .thenReturn(Optional.of(mockStudentEntities().get(0)));
//
//        Mockito.when(modelMapper.map(Mockito.any(), Mockito.eq(StudentEntity.class)))
//                .thenReturn(mockStudentEntities().get(0));
//
//        Mockito.when(studentRepo.saveAll(Mockito.any())).thenReturn(mockStudentEntities());
//
//        List<StudentEntity> studentEntities = studentServiceImp.updates(mockStudentModels());
//        Assertions.assertNotNull(studentEntities);
//        Assertions.assertEquals(studentEntities.get(0).getId(), mockStudentEntities().get(0).getId());
//    }
//
//    @Test
//    public void studentServiceTest_updatesFail() {
//        StudentModel fake = new StudentModel();
//        fake.setId(999);
//
//        StudentEntity fakeEntity = new StudentEntity();
//        fakeEntity.setId(999);
//
//        Mockito.when(modelMapper.map(fake, StudentEntity.class))
//                .thenReturn(fakeEntity);
//        Mockito.when(studentRepo.findById(999)).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(NotFoundIDStudentsException.class
//                , () -> studentServiceImp.updates(List.of(fake)));
//    }
//
//    @Test
//    public void studentServiceTest_deletesSuccess() {
//        Mockito.when(modelMapper.map(Mockito.any(), Mockito.eq(StudentEntity.class)))
//                        .thenReturn(mockStudentEntities().get(0));
//
//        Mockito.when(studentRepo.findById(mockStudentEntities().get(0).getId()))
//                .thenReturn(Optional.of(mockStudentEntities().get(0)));
//
//        Assertions.assertTrue(studentServiceImp.deletes(mockStudentModels()));
//    }
//
//    @Test
//    public void studentServiceTest_deletesFail() {
//        StudentModel fake = new StudentModel();
//        fake.setId(999);
//
//        StudentEntity fakeEntity = new StudentEntity();
//        fakeEntity.setId(999);
//
//        Mockito.when(modelMapper.map(fake, StudentEntity.class))
//                .thenReturn(fakeEntity);
//        Mockito.when(studentRepo.findById(999)).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(NotFoundIDStudentsException.class
//        , () -> studentServiceImp.deletes(List.of(fake)));
//    }
//
//}
