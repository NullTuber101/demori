package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Role;
import com.fdp.datareport.entities.User;
import com.fdp.datareport.services.UserService;
import com.fdp.datareport.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private String superToken;

    private User mockUser;

    @BeforeEach
    void setup() {
        superToken = "Bearer " + jwtUtil.generateToken("super123", "SUPER_USER");

        Role role = Role.builder().roleName("SUPER_USER").build();
        mockUser = User.builder()
                .id(1L)
                .brid("super123")
                .name("Test User")
                .email("test@example.com")
                .password("securepass")
                .role(role)
                .build();
    }

    @Test
    void shouldGetAllUsersWithSuperUser() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/api/users")
                        .header("Authorization", superToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brid").value("super123"));
    }

    @Test
    void shouldCreateUserWithSuperUser() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/users")
                        .header("Authorization", superToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void shouldGetUserByIdWithSuperUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", superToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldReturnNotFoundForNonExistingUser() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99")
                        .header("Authorization", superToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void shouldDeleteUserWithSuperUser() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1")
                        .header("Authorization", superToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void shouldUpdateUserRoleWithSuperUser() throws Exception {
        mockMvc.perform(put("/api/users/1/role")
                        .header("Authorization", superToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleName\":\"EDITOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Role updated successfully"));
    }
}
