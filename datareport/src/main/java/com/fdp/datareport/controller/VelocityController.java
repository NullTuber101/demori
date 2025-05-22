package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Velocity;
import com.fdp.datareport.service.VelocityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Velocity> createVelocity(@PathVariable Long areaId, @Valid @RequestBody Velocity velocity) {
        Velocity created = velocityService.createVelocity(areaId, velocity);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Velocity> updateVelocity(@PathVariable Long id, @Valid @RequestBody Velocity velocity) {
        Velocity updated = velocityService.updateVelocity(id, velocity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVelocity(@PathVariable Long id) {
        velocityService.deleteVelocity(id);
        return ResponseEntity.noContent().build();
    }
}
