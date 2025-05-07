package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.services.ScrumAreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/scrum-areas")
@CrossOrigin(origins = "*")
public class ScrumAreaController {

    @Autowired
    private ScrumAreaService scrumAreaService;

    @GetMapping
    public ResponseEntity<List<ScrumArea>> getAllScrumAreas() {
        return ResponseEntity.ok(scrumAreaService.getAllScrumAreas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getScrumAreaById(@PathVariable Long id) {
        Optional<ScrumArea> optionalArea = scrumAreaService.getScrumAreaById(id);
        if (optionalArea.isPresent()) {
            return ResponseEntity.ok(optionalArea.get());
        } else {
            return ResponseEntity.status(404)
                    .body(java.util.Map.of("error", "ScrumArea with ID " + id + " not found"));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<ScrumArea> createScrumArea(@Valid @RequestBody ScrumArea scrumArea) {
        return ResponseEntity.ok(scrumAreaService.createScrumArea(scrumArea));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<ScrumArea> updateScrumArea(@PathVariable Long id, @Valid @RequestBody ScrumArea updated) {
        return ResponseEntity.ok(scrumAreaService.updateScrumArea(id, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Void> deleteScrumArea(@PathVariable Long id) {
        scrumAreaService.deleteScrumArea(id);
        return ResponseEntity.noContent().build();
    }
}
