package com.fdp.datareport.repository;

import com.fdp.datareport.entity.ScrumArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrumAreaRepository extends JpaRepository<ScrumArea, Long> {
}
