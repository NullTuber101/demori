package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Role;
import com.fdp.datareport.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{roleName}")
    @PreAuthorize("hasAnyRole('SUPER_USER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<Object> getRoleByName(@PathVariable String roleName) {
        Optional<Role> optionalRole = roleService.findByName(roleName);
        if (optionalRole.isPresent()) {
            return ResponseEntity.ok(optionalRole.get());
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Role '" + roleName + "' not found"));
        }
    }
}
