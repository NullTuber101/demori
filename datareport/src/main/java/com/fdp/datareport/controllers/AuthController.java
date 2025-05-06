package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.User;
import com.fdp.datareport.services.UserService;
import com.fdp.datareport.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.CredentialHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String brid = loginRequest.get("brid");
        String password = loginRequest.get("password");

        User user = userService.getByBrid(brid);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid BRID or password");
        }

        String token = jwtUtil.generateToken(user.getBrid(), user.getRole().getRoleName());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", user.getRole().getRoleName(),
                "name", user.getName()
        ));
    }
}
