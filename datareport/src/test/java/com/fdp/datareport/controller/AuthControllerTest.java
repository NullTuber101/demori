package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Role;
import com.fdp.datareport.entity.User;
import com.fdp.datareport.service.UserService;
import com.fdp.datareport.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role = Role.builder().roleName("EDITOR").build();
        testUser = User.builder()
                .brid("test123")
                .email("test@example.com")
                .password("hashedPass")
                .name("Test User")
                .role(role)
                .build();
    }

    @Test
    void loginSuccess() {
        Map<String, String> request = Map.of("brid", "test123", "password", "plainPass");

        when(userService.getByBrid("test123")).thenReturn(testUser);
        when(passwordEncoder.matches("plainPass", "hashedPass")).thenReturn(true);
        when(jwtUtil.generateToken("test123", "EDITOR")).thenReturn("mock.jwt.token");

        ResponseEntity<Object> response = authController.login(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("token", "mock.jwt.token");
        assertThat(body).containsEntry("role", "EDITOR");
        assertThat(body).containsEntry("name", "Test User");
    }

    @Test
    void loginInvalidPassword() {
        Map<String, String> request = Map.of("brid", "test123", "password", "wrongPass");

        when(userService.getByBrid("test123")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongPass", "hashedPass")).thenReturn(false);

        ResponseEntity<Object> response = authController.login(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Invalid BRID or password"));
    }

    @Test
    void loginUserNotFound() {
        Map<String, String> request = Map.of("brid", "unknown", "password", "anything");

        when(userService.getByBrid("unknown")).thenReturn(null);

        ResponseEntity<Object> response = authController.login(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Invalid BRID or password"));
    }
}
