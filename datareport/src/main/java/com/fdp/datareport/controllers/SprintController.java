package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Sprint;
import com.fdp.datareport.services.SprintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Sprint> createSprint(@PathVariable Long projectId, @Valid @RequestBody Sprint sprint) {
        try {
            Sprint created = sprintService.createSprint(projectId, sprint);
            return ResponseEntity.status(201).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
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
    public ResponseEntity<Sprint> getSprintById(@PathVariable Long sprintId) {
        try {
            Sprint sprint = sprintService.getSprintById(sprintId);
            return ResponseEntity.ok(sprint);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update Sprint - Only EDITOR or SUPER_USER
    @PutMapping("/sprints/{sprintId}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Sprint> updateSprint(@PathVariable Long sprintId, @Valid @RequestBody Sprint sprint) {
        try {
            Sprint updated = sprintService.updateSprint(sprintId, sprint);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete Sprint - Only EDITOR or SUPER_USER
    @DeleteMapping("/sprints/{sprintId}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long sprintId) {
        try {
            sprintService.deleteSprint(sprintId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
