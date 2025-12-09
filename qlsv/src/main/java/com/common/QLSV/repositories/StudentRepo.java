package com.common.QLSV.repositories;

import com.common.QLSV.entities.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<StudentEntity, Integer> {
    Optional<StudentEntity> findByUserId(Integer userId);
    boolean existsByUserId(Integer userId);
    List<StudentEntity> findBySchoolClass(String schoolClass);
}
