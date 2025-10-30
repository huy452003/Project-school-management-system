package com.security.repositories;

import com.model_shared.enums.Role;
import com.model_shared.enums.Status;
import com.security.entities.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByUserId(Integer userId);
    boolean existsByUserName(String userName);
    boolean existsByUserId(Integer userId);
    long countByRole(Role role);
    
    // Tìm các user với status cụ thể
    List<UserEntity> findByStatus(Status status);
    
    // Tìm các user PENDING được tạo trước một thời điểm nhất định
    @Query("SELECT u FROM UserEntity u WHERE u.status = :status AND u.userId <= :maxUserId")
    List<UserEntity> findByStatusAndUserIdLessThanEqual(@Param("status") Status status, @Param("maxUserId") Integer maxUserId);
}
