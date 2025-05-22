package com.fdp.datareport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdp.datareport.entity.ScrumArea;
import com.fdp.datareport.entity.Velocity;
import com.fdp.datareport.service.VelocityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VelocityController.class)
class VelocityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VelocityService velocityService;

    @Autowired
    private ObjectMapper objectMapper;

    private Velocity velocity;

    @BeforeEach
    void setUp() {
        ScrumArea scrumArea = new ScrumArea();
        scrumArea.setId(1L);
        scrumArea.setAreaName("Backend Team");
        scrumArea.setScrumMaster("Alice");
        scrumArea.setScrumTeam("Team A");
        scrumArea.setBoardId("BOARD-123");

        velocity = new Velocity();
        velocity.setId(1L);
        velocity.setSprintName("Sprint X");
        velocity.setVelocity(25.0f);
        velocity.setSprintEndDate(LocalDate.of(2024, 5, 10));
        velocity.setScrumArea(scrumArea);
    }

    @Test
    void testGetAllVelocities() throws Exception {
        Mockito.when(velocityService.getAllVelocities()).thenReturn(List.of(velocity));

        mockMvc.perform(get("/api/velocities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sprintName", is("Sprint X")));
    }

    @Test
    void testGetByScrumArea() throws Exception {
        Mockito.when(velocityService.getVelocitiesByScrumAreaId(1L)).thenReturn(List.of(velocity));

        mockMvc.perform(get("/api/velocities/scrum-area/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].velocity", is(25.0)));
    }

    @Test
    void testGetVelocityChartData() throws Exception {
        Map<String, Object> chartData = Map.of("area", "Backend Team", "avgVelocity", 25.0);
        Mockito.when(velocityService.getVelocityGroupedByScrumArea()).thenReturn(List.of(chartData));

        mockMvc.perform(get("/api/velocities/chart-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].area", is("Backend Team")));
    }

    @Test
    void testCreateVelocitySuccess() throws Exception {
        Mockito.when(velocityService.createVelocity(eq(1L), any(Velocity.class))).thenReturn(velocity);

        mockMvc.perform(post("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(velocity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintName", is("Sprint X")));
    }

    @Test
    void testCreateVelocityFailure() throws Exception {
        Mockito.when(velocityService.createVelocity(eq(1L), any(Velocity.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(velocity)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Failed to create velocity: DB error")));
    }

    @Test
    void testUpdateVelocitySuccess() throws Exception {
        Mockito.when(velocityService.updateVelocity(eq(1L), any(Velocity.class))).thenReturn(velocity);

        mockMvc.perform(put("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(velocity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.velocity", is(25.0)));
    }

    @Test
    void testUpdateVelocityFailure() throws Exception {
        Mockito.when(velocityService.updateVelocity(eq(1L), any(Velocity.class)))
                .thenThrow(new RuntimeException("Update error"));

        mockMvc.perform(put("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(velocity)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Failed to update velocity: Update error")));
    }

    @Test
    void testDeleteVelocitySuccess() throws Exception {
        mockMvc.perform(delete("/api/velocities/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteVelocityFailure() throws Exception {
        Mockito.doThrow(new RuntimeException("Delete failed")).when(velocityService).deleteVelocity(1L);

        mockMvc.perform(delete("/api/velocities/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Failed to delete velocity: Delete failed")));
    }
}
