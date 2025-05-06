package com.fdp.datareport.services;

import com.fdp.datareport.entities.Project;
import com.fdp.datareport.entities.Status;
import com.fdp.datareport.repositories.StatusRepository;
import com.fdp.datareport.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Get all statuses
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    // Get a status by ID
    public Optional<Status> getStatusById(Long id) {
        return statusRepository.findById(id);
    }

    // Create a new status
    public Status createStatus(Status status) {
        return statusRepository.save(status);
    }

    // Update an existing status
    public Status updateStatus(Long id, Status updatedStatus) {
        if (statusRepository.existsById(id)) {
            updatedStatus.setId(id);
            return statusRepository.save(updatedStatus);
        } else {
            throw new RuntimeException("Status not found with id " + id);
        }
    }

    // Delete a status
    @Transactional
    public boolean deleteStatus(Long id) {
        Optional<Status> statusOpt = statusRepository.findById(id);
        if (statusOpt.isPresent()) {
            Status status = statusOpt.get();

            // Set status to null for all projects using this status
            List<Project> projects = projectRepository.findByStatus(status);
            for (Project project : projects) {
                project.setStatus(null);
                projectRepository.save(project);
            }

            // Now delete the status
            statusRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
