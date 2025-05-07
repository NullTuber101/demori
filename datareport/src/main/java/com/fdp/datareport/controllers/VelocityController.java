package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Velocity;
import com.fdp.datareport.services.VelocityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/velocities")
@CrossOrigin(origins = "*")
public class VelocityController {

    @Autowired
    private VelocityService velocityService;

    // Public GETs — no login required
    @GetMapping
    public ResponseEntity<List<Velocity>> getAllVelocities() {
        return ResponseEntity.ok(velocityService.getAllVelocities());
    }

    @GetMapping("/scrum-area/{areaId}")
    public ResponseEntity<List<Velocity>> getByScrumArea(@PathVariable Long areaId) {
        return ResponseEntity.ok(velocityService.getVelocitiesByScrumAreaId(areaId));
    }

    @GetMapping("/chart-data")
    public ResponseEntity<List<Map<String, Object>>> getVelocityChartData() {
        return ResponseEntity.ok(velocityService.getVelocityGroupedByScrumArea());
    }

    // Protected POST/PUT/DELETE
    @PostMapping("/{areaId}")
    @PreAuthorize("hasAnyRole('SUPER_USER', 'EDITOR')")
    public ResponseEntity<Object> createVelocity(@PathVariable Long areaId, @Valid @RequestBody Velocity velocity) {
        try {
            Velocity created = velocityService.createVelocity(areaId, velocity);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create velocity: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_USER', 'EDITOR')")
    public ResponseEntity<Object> updateVelocity(@PathVariable Long id, @Valid @RequestBody Velocity velocity) {
        try {
            Velocity updated = velocityService.updateVelocity(id, velocity);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update velocity: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_USER', 'EDITOR')")
    public ResponseEntity<Object> deleteVelocity(@PathVariable Long id) {
        try {
            velocityService.deleteVelocity(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete velocity: " + e.getMessage()));
        }
    }
}
