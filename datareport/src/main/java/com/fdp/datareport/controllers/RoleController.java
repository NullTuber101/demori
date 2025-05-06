package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Role;
import com.fdp.datareport.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // Accessible by anyone
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    // Accessible only by authenticated users
    @GetMapping("/{roleName}")
    @PreAuthorize("hasAnyRole('SUPER_USER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getRoleByName(@PathVariable String roleName) {
        return roleService.findByName(roleName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body((Role) Map.of("error", "Role '" + roleName + "' not found")));
    }
}
