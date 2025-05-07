package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Area;
import com.fdp.datareport.services.AreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/areas")
@CrossOrigin(origins = "*")
public class AreaController {

    @Autowired
    private AreaService areaService;

    // Public - anyone can view areas
    @GetMapping
    public ResponseEntity<List<Area>> getAllAreas() {
        return ResponseEntity.ok(areaService.getAllAreas());
    }

    // Public - anyone can view area by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAreaById(@PathVariable Long id) {
        try {
            return areaService.getAreaById(id)
                    .map(area -> ResponseEntity.ok((Object) area))  // force cast to Object
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Area not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    // Restricted - EDITOR and SUPER_USER can create
    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Object> addArea(@Valid @RequestBody Area area) {
        try {
            Area createdArea = areaService.addArea(area);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArea);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create area: " + e.getMessage()));
        }
    }

    // Restricted - EDITOR and SUPER_USER can update
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Object> updateArea(@PathVariable Long id, @Valid @RequestBody Area area) {
        try {
            Area updatedArea = areaService.updateArea(id, area);
            return updatedArea != null
                    ? ResponseEntity.ok(updatedArea)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Area not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update area: " + e.getMessage()));
        }
    }

    // Restricted - EDITOR and SUPER_USER can delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Object> deleteArea(@PathVariable Long id) {
        try {
            if (areaService.deleteArea(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Area not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete area: " + e.getMessage()));
        }
    }
}
