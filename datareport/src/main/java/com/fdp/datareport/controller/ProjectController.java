package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Project;
import com.fdp.datareport.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Validated
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Public - Get all projects
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // Public - Get project by ID
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with ID " + id + " not found"));
        return ResponseEntity.ok(project);
    }

    // Restricted - Create project (EDITOR, SUPER_USER)
    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
        return ResponseEntity.status(201).body(projectService.createProject(project));
    }

    // Restricted - Update project (EDITOR, SUPER_USER)
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody Project project) {
        return ResponseEntity.ok(projectService.updateProject(id, project)
                .orElseThrow(() -> new EntityNotFoundException("Project with ID " + id + " not found")));
    }

    // Restricted - Delete project (EDITOR, SUPER_USER)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (!projectService.deleteProject(id)) {
            throw new EntityNotFoundException("Project with ID " + id + " not found");
        }
        return ResponseEntity.noContent().build();
    }
}
