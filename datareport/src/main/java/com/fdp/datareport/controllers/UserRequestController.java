package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.User;
import com.fdp.datareport.entities.UserRequest;
import com.fdp.datareport.enums.RequestStatus;
import com.fdp.datareport.services.UserRequestService;
import com.fdp.datareport.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class UserRequestController {

    private final UserRequestService userRequestService;
    private final UserService userService;

    // Publicly accessible - no login required
    @PostMapping("/signup")
    public ResponseEntity<?> signupRequest(@RequestBody UserRequest request) {
        boolean existsInUsers = userService.isDuplicate(request.getBrid(), request.getEmail());
        boolean existsInRequests = userRequestService.isDuplicate(request.getBrid(), request.getEmail());

        if (existsInUsers || existsInRequests) {
            return ResponseEntity.status(409).body(Map.of("error", "BRID or Email already exists or is under review."));
        }

        try {
            UserRequest savedRequest = userRequestService.createRequest(request);
            return ResponseEntity.ok(Map.of("message", "Signup request submitted successfully.", "requestId", savedRequest.getId()));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(409).body(Map.of("error", "Duplicate BRID or Email detected."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Signup failed: " + e.getMessage()));
        }
    }

    // Only SUPER_USER can view pending requests
    @GetMapping("/pending")
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<List<UserRequest>> getPendingRequests() {
        return ResponseEntity.ok(userRequestService.getPendingRequests());
    }

    // Only SUPER_USER can approve
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, @RequestParam String roleName) {
        return userRequestService.getRequestById(id)
                .map(request -> {
                    if (request.getStatus() != RequestStatus.PENDING) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Request already reviewed."));
                    }
                    try {
                        User user = userService.approveRequest(request, roleName);
                        return ResponseEntity.ok(Map.of("message", "User approved successfully.", "userId", user.getId()));
                    } catch (DataIntegrityViolationException ex) {
                        return ResponseEntity.status(409).body(Map.of("error", "BRID or Email already exists."));
                    } catch (Exception e) {
                        return ResponseEntity.status(500).body(Map.of("error", "Approval failed: " + e.getMessage()));
                    }
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Request not found")));
    }

    // Only SUPER_USER can reject
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestParam String reason) {
        return userRequestService.getRequestById(id)
                .map(req -> {
                    if (req.getStatus() != RequestStatus.PENDING) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Request already reviewed."));
                    }
                    userRequestService.rejectRequest(id, reason);
                    return ResponseEntity.ok(Map.of("message", "Request rejected successfully."));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Request not found")));
    }
}
