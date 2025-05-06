package com.fdp.datareport.repositories;

import com.fdp.datareport.entities.Sprint;
import com.fdp.datareport.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    int deleteByProject(Project p);

}

