package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Sprint;
import com.fdp.datareport.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Sprint> createSprint(@PathVariable Long projectId, @Valid @RequestBody Sprint sprint) {
        Sprint created = sprintService.createSprint(projectId, sprint);
        return ResponseEntity.status(201).body(created);
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
        Sprint sprint = sprintService.getSprintById(sprintId);
        return ResponseEntity.ok(sprint);
    }

    // Update Sprint - Only EDITOR or SUPER_USER
    @PutMapping("/sprints/{sprintId}")
    public ResponseEntity<Sprint> updateSprint(@PathVariable Long sprintId, @Valid @RequestBody Sprint sprint) {
        Sprint updated = sprintService.updateSprint(sprintId, sprint);
        return ResponseEntity.ok(updated);
    }

    // Delete Sprint - Only EDITOR or SUPER_USER
    @DeleteMapping("/sprints/{sprintId}")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long sprintId) {
        sprintService.deleteSprint(sprintId);
        return ResponseEntity.noContent().build();
    }
}
