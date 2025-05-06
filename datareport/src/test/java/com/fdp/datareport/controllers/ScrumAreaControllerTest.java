package com.fdp.datareport.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.services.ScrumAreaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScrumAreaController.class)
public class ScrumAreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScrumAreaService scrumAreaService;

    @Autowired
    private ObjectMapper objectMapper;

    private ScrumArea sampleArea;

    @BeforeEach
    void setup() {
        sampleArea = new ScrumArea(1L, "Platform", "John Doe", "Team A", "BOARD-001");
    }

    @Test
    void testGetAllScrumAreas() throws Exception {
        Mockito.when(scrumAreaService.getAllScrumAreas()).thenReturn(List.of(sampleArea));

        mockMvc.perform(get("/api/scrum-areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].scrumMaster").value("John Doe"));
    }

    @Test
    void testGetScrumAreaById_Found() throws Exception {
        Mockito.when(scrumAreaService.getScrumAreaById(1L)).thenReturn(Optional.of(sampleArea));

        mockMvc.perform(get("/api/scrum-areas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.areaName").value("Platform"));
    }

    @Test
    void testGetScrumAreaById_NotFound() throws Exception {
        Mockito.when(scrumAreaService.getScrumAreaById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/scrum-areas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateScrumArea() throws Exception {
        Mockito.when(scrumAreaService.createScrumArea(any(ScrumArea.class))).thenReturn(sampleArea);

        mockMvc.perform(post("/api/scrum-areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleArea)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value("BOARD-001"));
    }

    @Test
    void testUpdateScrumArea() throws Exception {
        Mockito.when(scrumAreaService.updateScrumArea(eq(1L), any(ScrumArea.class))).thenReturn(sampleArea);

        mockMvc.perform(put("/api/scrum-areas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleArea)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scrumTeam").value("Team A"));
    }

    @Test
    void testDeleteScrumArea() throws Exception {
        mockMvc.perform(delete("/api/scrum-areas/1"))
                .andExpect(status().isNoContent());
    }
}
