package com.fdp.datareport.repositories;


import com.fdp.datareport.entities.UserRequest;
import com.fdp.datareport.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {
    Optional<UserRequest> findByBrid(String brid);
    Optional<UserRequest> findByEmail(String email);
    boolean existsByBrid(String brid);
    boolean existsByEmail(String email);

    List<UserRequest> findAllByStatus(RequestStatus status);

    void deleteAllByStatusAndCreatedAtBefore(RequestStatus status, LocalDateTime cutoff);
}
