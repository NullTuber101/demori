package com.fdp.datareport.controller;

import com.fdp.datareport.entity.ScrumArea;
import com.fdp.datareport.service.ScrumAreaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ScrumAreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScrumAreaService scrumAreaService;

    private ScrumArea mockArea;
    private String editorToken;
    private String viewerToken;

    @BeforeEach
    void setup() {
        mockArea = new ScrumArea(1L, "AreaX", "John Doe", "Team Rocket", "BRD-123");
    }

    @Test
    void getAllScrumAreasShouldSucceedWithoutAuth() throws Exception {
        when(scrumAreaService.getAllScrumAreas()).thenReturn(List.of(mockArea));

        mockMvc.perform(get("/api/scrum-areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getScrumAreaByIdFound() throws Exception {
        when(scrumAreaService.getScrumAreaById(1L)).thenReturn(Optional.of(mockArea));

        mockMvc.perform(get("/api/scrum-areas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.areaName").value("AreaX"));
    }

    @Test
    void getScrumAreaByIdNotFound() throws Exception {
        when(scrumAreaService.getScrumAreaById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/scrum-areas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ScrumArea with ID 999 not found"));
    }

}
