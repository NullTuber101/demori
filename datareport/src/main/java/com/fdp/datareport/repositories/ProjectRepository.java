package com.fdp.datareport.repositories;


import com.fdp.datareport.entities.Area;
import com.fdp.datareport.entities.Project;
import com.fdp.datareport.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByArea(Area area);
    List<Project> findByStatus(Status status);
    void deleteByArea(Area area);

}
