package com.fdp.datareport.repository;

import com.fdp.datareport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByBrid(String brid);
    Optional<User> findByEmail(String email);
    boolean existsByBrid(String brid);
    boolean existsByEmail(String email);
}
