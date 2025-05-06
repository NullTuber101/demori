package com.fdp.datareport.repositories;

import com.fdp.datareport.entities.ScrumArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrumAreaRepository extends JpaRepository<ScrumArea, Long> {
}
