package com.security.repositories;

import com.security.entities.Role;
import com.security.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUserName(String userName);
    boolean existsByUserName(String userName);
    long countByRole(Role role);
}
