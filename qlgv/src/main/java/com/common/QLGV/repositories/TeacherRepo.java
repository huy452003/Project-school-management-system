package com.common.QLGV.repositories;

import com.common.QLGV.entities.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepo extends JpaRepository<TeacherEntity, Integer> {
    Optional<TeacherEntity> findByUserId(Integer userId);
    boolean existsByUserId(Integer userId);
}
