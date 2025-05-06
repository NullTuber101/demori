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

@RestController
@RequestMapping("/api/areas")
@CrossOrigin(origins = "*")
public class AreaController {

    @Autowired
    private AreaService areaService;

    // Public - anyone can view areas
    @GetMapping
    public ResponseEntity<List<Area>> getAllAreas() {
        List<Area> areas = areaService.getAllAreas();
        return ResponseEntity.ok(areas);
    }

    // Public - anyone can view area by ID
    @GetMapping("/{id}")
    public ResponseEntity<Area> getAreaById(@PathVariable Long id) {
        return areaService.getAreaById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Restricted - EDITOR and SUPER_USER can create
    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Area> addArea(@Valid @RequestBody Area area) {
        Area createdArea = areaService.addArea(area);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArea);
    }

    // Restricted - EDITOR and SUPER_USER can update
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Area> updateArea(@PathVariable Long id, @Valid @RequestBody Area area) {
        Area updatedArea = areaService.updateArea(id, area);
        return updatedArea != null
                ? ResponseEntity.ok(updatedArea)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // Restricted - EDITOR and SUPER_USER can delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'SUPER_USER')")
    public ResponseEntity<Void> deleteArea(@PathVariable Long id) {
        if (areaService.deleteArea(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
