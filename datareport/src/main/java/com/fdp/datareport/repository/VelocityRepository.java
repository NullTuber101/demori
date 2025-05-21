package com.fdp.datareport.repository;

import com.fdp.datareport.entity.Velocity;
import com.fdp.datareport.entity.ScrumArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VelocityRepository extends JpaRepository<Velocity, Long> {
    List<Velocity> findByScrumAreaId(Long areaId);


    List<Velocity> findTop3ByScrumAreaOrderBySprintEndDateDesc(ScrumArea scrumArea);
}
