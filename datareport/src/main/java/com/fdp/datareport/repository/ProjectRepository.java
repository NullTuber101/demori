package com.fdp.datareport.repository;


import com.fdp.datareport.entity.Area;
import com.fdp.datareport.entity.Project;
import com.fdp.datareport.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByArea(Area area);
    List<Project> findByStatus(Status status);
    void deleteByArea(Area area);

}
