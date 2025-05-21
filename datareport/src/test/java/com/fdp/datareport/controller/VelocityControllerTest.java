package com.fdp.datareport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdp.datareport.entity.ScrumArea;
import com.fdp.datareport.entity.Velocity;
import com.fdp.datareport.service.VelocityService;
import com.fdp.datareport.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VelocityControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtUtil jwtUtil;

    @MockBean private VelocityService velocityService;

    private String editorToken;
    private Velocity testVelocity;

    @BeforeEach
    void setUp() {
        editorToken = "Bearer " + jwtUtil.generateToken("editor123", "EDITOR");

        ScrumArea scrumArea = new ScrumArea(1L, "Area A", "SM", "Team A", "BOARD123");

        testVelocity = new Velocity();
        testVelocity.setId(1L);
        testVelocity.setSprintName("Sprint 1");
        testVelocity.setVelocity(40.5f);
        testVelocity.setSprintEndDate(LocalDate.now());
        testVelocity.setScrumArea(scrumArea);
    }

    // ✅ GET endpoints (public)
    @Test
    void shouldGetAllVelocitiesWithoutAuth() throws Exception {
        when(velocityService.getAllVelocities()).thenReturn(List.of(testVelocity));

        mockMvc.perform(get("/api/velocities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetVelocitiesByScrumAreaId() throws Exception {
        when(velocityService.getVelocitiesByScrumAreaId(1L)).thenReturn(List.of(testVelocity));

        mockMvc.perform(get("/api/velocities/scrum-area/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sprintName").value("Sprint 1"));
    }

    @Test
    void shouldGetChartData() throws Exception {
        when(velocityService.getVelocityGroupedByScrumArea())
                .thenReturn(List.of(Map.of("areaName", "Area A", "averageVelocity", 50)));

        mockMvc.perform(get("/api/velocities/chart-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].areaName").value("Area A"));
    }

    // ✅ POST (create)
    @Test
    void shouldCreateVelocityWithEditorToken() throws Exception {
        when(velocityService.createVelocity(eq(1L), any(Velocity.class))).thenReturn(testVelocity);

        mockMvc.perform(post("/api/velocities/1")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVelocity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintName").value("Sprint 1"));
    }

    @Test
    void shouldReturn403WithoutTokenOnCreate() throws Exception {
        mockMvc.perform(post("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVelocity)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn500OnCreateVelocityError() throws Exception {
        when(velocityService.createVelocity(anyLong(), any(Velocity.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/velocities/1")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVelocity)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to create velocity: DB error"));
    }

    // ✅ PUT (update)
    @Test
    void shouldUpdateVelocityWithEditorToken() throws Exception {
        when(velocityService.updateVelocity(eq(1L), any(Velocity.class))).thenReturn(testVelocity);

        mockMvc.perform(put("/api/velocities/1")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVelocity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.velocity").value(40.5));
    }

    @Test
    void shouldReturn500OnUpdateVelocityError() throws Exception {
        when(velocityService.updateVelocity(eq(1L), any(Velocity.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/velocities/1")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVelocity)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to update velocity: Update failed"));
    }

    @Test
    void shouldReturn403WithoutTokenOnUpdate() throws Exception {
        mockMvc.perform(put("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVelocity)))
                .andExpect(status().isForbidden());
    }

    // ✅ DELETE
    @Test
    void shouldDeleteVelocityWithEditorToken() throws Exception {
        doNothing().when(velocityService).deleteVelocity(1L);

        mockMvc.perform(delete("/api/velocities/1")
                        .header("Authorization", editorToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn500OnDeleteError() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(velocityService).deleteVelocity(1L);

        mockMvc.perform(delete("/api/velocities/1")
                        .header("Authorization", editorToken))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to delete velocity: Delete failed"));
    }

    @Test
    void shouldReturn403WithoutTokenOnDelete() throws Exception {
        mockMvc.perform(delete("/api/velocities/1"))
                .andExpect(status().isForbidden());
    }
}
