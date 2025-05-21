package com.fdp.datareport.repository;

import com.fdp.datareport.entity.Sprint;
import com.fdp.datareport.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    int deleteByProject(Project p);

}

