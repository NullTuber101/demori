package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.User;
import com.fdp.datareport.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Only SUPER_USER can view all users
    @GetMapping
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Only SUPER_USER can create users manually
    @PostMapping
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    // SUPER_USER or EDITOR can fetch user by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_USER')")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }

    // Only SUPER_USER can change user roles
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<Object> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String roleName = body.get("roleName");
            userService.updateUserRole(id, roleName);
            return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Role update failed: " + e.getMessage()));
        }
    }

    // Only SUPER_USER can delete a user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }
}
