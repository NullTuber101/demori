package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Sprint;
import com.fdp.datareport.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
@Validated
public class SprintController {

    @Autowired
    private SprintService sprintService;

    // Create Sprint - Only EDITOR or SUPER_USER
    @PostMapping("/{projectId}/sprints")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Object> createSprint(@PathVariable Long projectId, @Valid @RequestBody Sprint sprint) {
        try {
            Sprint created = sprintService.createSprint(projectId, sprint);
            return ResponseEntity.status(201).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Failed to create sprint"));
        }
    }

    // Get All Sprints - Public access
    @GetMapping("/{projectId}/sprints")
    public ResponseEntity<List<Sprint>> getSprintsByProject(@PathVariable Long projectId) {
        List<Sprint> sprints = sprintService.getSprintsByProject(projectId);
        return ResponseEntity.ok(sprints);
    }

    // Get Sprint by ID - Public access
    @GetMapping("/sprints/{sprintId}")
    public ResponseEntity<Object> getSprintById(@PathVariable Long sprintId) {
        try {
            Sprint sprint = sprintService.getSprintById(sprintId);
            return ResponseEntity.ok(sprint);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Sprint with ID " + sprintId + " not found"));
        }
    }

    // Update Sprint - Only EDITOR or SUPER_USER
    @PutMapping("/sprints/{sprintId}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Object> updateSprint(@PathVariable Long sprintId, @Valid @RequestBody Sprint sprint) {
        try {
            Sprint updated = sprintService.updateSprint(sprintId, sprint);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Sprint with ID " + sprintId + " not found"));
        }
    }

    // Delete Sprint - Only EDITOR or SUPER_USER
    @DeleteMapping("/sprints/{sprintId}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Object> deleteSprint(@PathVariable Long sprintId) {
        try {
            sprintService.deleteSprint(sprintId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Sprint with ID " + sprintId + " not found"));
        }
    }
}
