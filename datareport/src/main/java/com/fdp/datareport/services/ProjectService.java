package com.fdp.datareport.services;

import com.fdp.datareport.entities.Project;
import com.fdp.datareport.repositories.AreaRepository;
import com.fdp.datareport.repositories.ProjectRepository;
import com.fdp.datareport.repositories.StatusRepository;
import com.fdp.datareport.repositories.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private SprintRepository sprintRepository;

    // Create a new project
    public Project createProject(Project project) {
        // Validate or modify before saving if needed (e.g., set Area, Status)
        return projectRepository.save(project);
    }

    // Get all projects
    public List<Project> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        // Initialize lazy-loaded associations
        for (Project project : projects) {
            if (project.getArea() != null) {
                project.getArea().getName();  // Access Area name only if it's not null
            }
            if (project.getStatus() != null) {
                project.getStatus().getStatusName();  // Access Status name only if it's not null
            }
        }
        return projects;
    }


    // Get a project by ID
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    // Update a project
    public Optional<Project> updateProject(Long id, Project projectDetails) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            project.setProjectName(projectDetails.getProjectName());
            project.setDescription(projectDetails.getDescription());
            project.setDeveloper(projectDetails.getDeveloper());
            project.setJira(projectDetails.getJira());
            project.setStartDate(projectDetails.getStartDate());
            project.setEndDate(projectDetails.getEndDate());
            project.setArea(projectDetails.getArea());
            project.setStatus(projectDetails.getStatus());
            return Optional.of(projectRepository.save(project));
        }
        return Optional.empty();
    }

    // Delete a project
//    public boolean deleteProject(Long id) {
//        if (projectRepository.existsById(id)) {
//            sprintRepository.deleteByProject(id);
//
//            projectRepository.deleteById(id);
//            return true;
//        }
//        return false;
//    }
    @Transactional
    public boolean deleteProject(Long id) {
        Optional<Project> projOpt = projectRepository.findById(id);
        if (projOpt.isPresent()) {
            // Delete all projects that belong to this area
            sprintRepository.deleteByProject(projOpt.get());

            // Then delete the area itself
            projectRepository.deleteById(id);
            return true;
        }
        return false;
}}
