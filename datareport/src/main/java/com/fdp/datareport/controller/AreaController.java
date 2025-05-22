package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Area;
import com.fdp.datareport.service.AreaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
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
    public ResponseEntity<Area> getAreaById(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.getAreaById(id)
                .orElseThrow(() -> new EntityNotFoundException("Area with ID " + id + " not found")));
    }

    // Restricted - EDITOR and SUPER_USER can create
    @PostMapping
    public ResponseEntity<Area> addArea(@Valid @RequestBody Area area) {
        Area createdArea = areaService.addArea(area);
        return ResponseEntity.status(201).body(createdArea);
    }

    // Restricted - EDITOR and SUPER_USER can update
    @PutMapping("/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable Long id, @Valid @RequestBody Area area) {
        return ResponseEntity.ok(areaService.updateArea(id, area));
    }

    // Restricted - EDITOR and SUPER_USER can delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable Long id) {
        areaService.deleteArea(id);
        return ResponseEntity.noContent().build();
    }
}
