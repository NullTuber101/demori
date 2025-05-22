package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Status;
import com.fdp.datareport.service.StatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
@CrossOrigin(origins = "*")
public class StatusController {

    @Autowired
    private StatusService statusService;

    // Public: Get all statuses
    @GetMapping
    public ResponseEntity<List<Status>> getAllStatuses() {
        return ResponseEntity.ok(statusService.getAllStatuses());
    }

    // Public: Get a status by ID
    @GetMapping("/{id}")
    public ResponseEntity<Status> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(statusService.getStatusById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Status with ID " + id + " not found")));
    }

    // Restricted: Add a new status (EDITOR or SUPER_USER)
    @PostMapping
    public ResponseEntity<Status> addStatus(@Valid @RequestBody Status status) {
        Status createdStatus = statusService.createStatus(status);
        return ResponseEntity.status(201).body(createdStatus);
    }

    // Restricted: Update an existing status (EDITOR or SUPER_USER)
    @PutMapping("/{id}")
    public ResponseEntity<Status> updateStatus(@PathVariable Long id, @Valid @RequestBody Status status) {
        return ResponseEntity.ok(statusService.updateStatus(id, status));
    }

    // Restricted: Delete a status (EDITOR or SUPER_USER)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        statusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}
