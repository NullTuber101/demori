package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.services.ScrumAreaService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ScrumAreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

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
        editorToken = "Bearer " + jwtUtil.generateToken("editor1", "EDITOR");
        viewerToken = "Bearer " + jwtUtil.generateToken("viewer1", "VIEWER");
    }

    @Test
    void getAllScrumAreasShouldSucceedWithoutAuth() throws Exception {
        when(scrumAreaService.getAllScrumAreas()).thenReturn(List.of(mockArea));

        mockMvc.perform(get("/api/scrum-areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getScrumAreaByIdNotFound() throws Exception {
        when(scrumAreaService.getScrumAreaById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/scrum-areas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ScrumArea with ID 999 not found"));
    }

    @Test
    void createScrumAreaShouldReturn403WithoutToken() throws Exception {
        mockMvc.perform(post("/api/scrum-areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockArea)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createScrumAreaWithEditorTokenShouldSucceed() throws Exception {
        when(scrumAreaService.createScrumArea(any(ScrumArea.class))).thenReturn(mockArea);

        mockMvc.perform(post("/api/scrum-areas")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockArea)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.areaName").value("AreaX"));
    }

    @Test
    void deleteScrumAreaWithEditorTokenShouldSucceed() throws Exception {
        doNothing().when(scrumAreaService).deleteScrumArea(1L);

        mockMvc.perform(delete("/api/scrum-areas/1")
                        .header("Authorization", editorToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteScrumAreaWithoutTokenShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/scrum-areas/1"))
                .andExpect(status().isForbidden());
    }
}
