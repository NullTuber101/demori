package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Project;
import com.fdp.datareport.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

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
    public ResponseEntity<Object> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(p -> ResponseEntity.ok((Object) p))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "Project not found")));
    }


    // Restricted - Create project (EDITOR, SUPER_USER)
    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Object> createProject(@Valid @RequestBody Project project) {
        return ResponseEntity.status(201).body(projectService.createProject(project));
    }

    // Restricted - Update project (EDITOR, SUPER_USER)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Object> updateProject(@PathVariable Long id, @Valid @RequestBody Project project) {
        Optional<Project> updatedProject = projectService.updateProject(id, project);
        return updatedProject.map(p -> ResponseEntity.ok((Object) p))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "Project not found")));
    }

    // Restricted - Delete project (EDITOR, SUPER_USER)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Object> deleteProject(@PathVariable Long id) {
        if (projectService.deleteProject(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Project not found"));
        }
    }
}
