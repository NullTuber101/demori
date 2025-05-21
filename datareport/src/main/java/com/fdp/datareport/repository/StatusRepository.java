package com.fdp.datareport.repository;


import com.fdp.datareport.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
}

