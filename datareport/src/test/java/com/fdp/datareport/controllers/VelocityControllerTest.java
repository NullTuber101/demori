package com.fdp.datareport.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.entities.Velocity;
import com.fdp.datareport.services.VelocityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VelocityController.class)
public class VelocityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VelocityService velocityService;

    @Autowired
    private ObjectMapper objectMapper;

    private Velocity sampleVelocity;
    private ScrumArea sampleScrumArea;

    @BeforeEach
    void setup() {
        sampleScrumArea = new ScrumArea(1L, "DevOps", "John Doe", "Alpha Team", "BOARD-1");
        sampleVelocity = new Velocity(1L, "Sprint 1", 20.0f, LocalDate.of(2024, 5, 4), sampleScrumArea);
    }

    @Test
    void testGetAllVelocities() throws Exception {
        Mockito.when(velocityService.getAllVelocities()).thenReturn(List.of(sampleVelocity));

        mockMvc.perform(get("/api/velocities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sprintName").value("Sprint 1"));
    }

    @Test
    void testGetByScrumArea() throws Exception {
        Mockito.when(velocityService.getVelocitiesByScrumAreaId(1L)).thenReturn(List.of(sampleVelocity));

        mockMvc.perform(get("/api/velocities/scrum-area/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].scrumArea.areaName").value("DevOps"));
    }

    @Test
    void testCreateVelocity() throws Exception {
        Mockito.when(velocityService.createVelocity(eq(1L), any(Velocity.class))).thenReturn(sampleVelocity);

        mockMvc.perform(post("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleVelocity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintName").value("Sprint 1"));
    }

    @Test
    void testUpdateVelocity() throws Exception {
        Mockito.when(velocityService.updateVelocity(eq(1L), any(Velocity.class))).thenReturn(sampleVelocity);

        mockMvc.perform(put("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleVelocity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.velocity").value(20.0));
    }

    @Test
    void testDeleteVelocity() throws Exception {
        mockMvc.perform(delete("/api/velocities/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetChartData() throws Exception {
        Map<String, Object> chartPoint = new HashMap<>();
        chartPoint.put("areaName", "DevOps");
        chartPoint.put("velocity", 20.0);

        Mockito.when(velocityService.getVelocityGroupedByScrumArea())
                .thenReturn(List.of(chartPoint));

        mockMvc.perform(get("/api/velocities/chart-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].areaName").value("DevOps"));
    }
}
