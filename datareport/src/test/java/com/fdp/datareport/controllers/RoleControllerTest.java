package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Role;
import com.fdp.datareport.services.RoleService;
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

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private RoleService roleService;

    private String viewerToken, editorToken, superUserToken;

    @BeforeEach
    void setup() {
        viewerToken = "Bearer " + jwtUtil.generateToken("viewer123", "VIEWER");
        editorToken = "Bearer " + jwtUtil.generateToken("editor123", "EDITOR");
        superUserToken = "Bearer " + jwtUtil.generateToken("superuser", "SUPER_USER");
    }

    @Test
    void shouldAllowPublicAccessToGetAllRoles() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of(
                new Role(1L, "VIEWER", false, false, false),
                new Role(2L, "EDITOR", false, true, false)
        ));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldAllowAccessToGetRoleByNameWithValidRole() throws Exception {
        Role role = new Role(1L, "EDITOR", false, true, false);
        when(roleService.findByName("EDITOR")).thenReturn(Optional.of(role));

        mockMvc.perform(get("/api/roles/EDITOR")
                        .header("Authorization", editorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("EDITOR"));
    }

    @Test
    void shouldReturn403IfNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/roles/EDITOR"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403IfInvalidTokenProvided() throws Exception {
        mockMvc.perform(get("/api/roles/EDITOR")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404IfRoleNotFound() throws Exception {
        when(roleService.findByName("UNKNOWN")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/roles/UNKNOWN")
                        .header("Authorization", superUserToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Role 'UNKNOWN' not found"));
    }
}
