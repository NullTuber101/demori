package com.fdp.datareport.repositories;


import com.fdp.datareport.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
}

