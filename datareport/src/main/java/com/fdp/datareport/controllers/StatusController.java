package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Status;
import com.fdp.datareport.services.StatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        List<Status> statuses = statusService.getAllStatuses();
        return ResponseEntity.ok(statuses);
    }

    // Public: Get a status by ID
    @GetMapping("/{id}")
    public ResponseEntity<Status> getStatusById(@PathVariable Long id) {
        return statusService.getStatusById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Restricted: Add a new status (EDITOR or SUPER_USER)
    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Status> addStatus(@Valid @RequestBody Status status) {
        Status createdStatus = statusService.createStatus(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    // Restricted: Update an existing status (EDITOR or SUPER_USER)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Status> updateStatus(@PathVariable Long id, @Valid @RequestBody Status status) {
        Status updatedStatus = statusService.updateStatus(id, status);
        return updatedStatus != null
                ? ResponseEntity.ok(updatedStatus)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // Restricted: Delete a status (EDITOR or SUPER_USER)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        if (statusService.deleteStatus(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
