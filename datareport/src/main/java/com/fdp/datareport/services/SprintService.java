package com.fdp.datareport.services;

import com.fdp.datareport.entities.Project;
import com.fdp.datareport.entities.Sprint;
import com.fdp.datareport.entities.Status;
import com.fdp.datareport.repositories.ProjectRepository;
import com.fdp.datareport.repositories.SprintRepository;
import com.fdp.datareport.repositories.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SprintService {

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StatusRepository statusRepository;

    // 🔹 Create
    public Sprint createSprint(Long projectId, Sprint sprint) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Status status = statusRepository.findById(sprint.getSprintFor().getId())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        sprint.setProject(project);
        sprint.setSprintFor(status);

        return sprintRepository.save(sprint);
    }

    // 🔹 Read all sprints for a project
    public List<Sprint> getSprintsByProject(Long projectId) {
        return sprintRepository.findAll().stream()
                .filter(s -> s.getProject().getId().equals(projectId))
                .toList();
    }

    // 🔹 Read one sprint
    public Sprint getSprintById(Long sprintId) {
        return sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));
    }

    // 🔹 Update
    public Sprint updateSprint(Long sprintId, Sprint updatedSprint) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        Status status = statusRepository.findById(updatedSprint.getSprintFor().getId())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        sprint.setSprintName(updatedSprint.getSprintName());
        sprint.setSprintStartDate(updatedSprint.getSprintStartDate());
        sprint.setSprintEndDate(updatedSprint.getSprintEndDate());
        sprint.setSprintJira(updatedSprint.getSprintJira());
        sprint.setSprintDescription(updatedSprint.getSprintDescription());
        sprint.setAssignedTo(updatedSprint.getAssignedTo());
        sprint.setSprintFor(status);

        return sprintRepository.save(sprint);
    }

    // 🔹 Delete
    public void deleteSprint(Long sprintId) {
        if (!sprintRepository.existsById(sprintId)) {
            throw new RuntimeException("Sprint not found");
        }
        sprintRepository.deleteById(sprintId);
    }
}
