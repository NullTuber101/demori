package com.fdp.datareport.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdp.datareport.entities.Area;
import com.fdp.datareport.services.AreaService;
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

@WebMvcTest(AreaController.class)
class AreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AreaService areaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Area sampleArea;

    @BeforeEach
    void setUp() {
        sampleArea = new Area(1L, "Engineering", "Alice Smith", "alice@example.com");
    }

    @Test
    void testGetAllAreas() throws Exception {
        Mockito.when(areaService.getAllAreas()).thenReturn(List.of(sampleArea));

        mockMvc.perform(get("/api/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Engineering"))
                .andExpect(jsonPath("$[0].leadName").value("Alice Smith"))
                .andExpect(jsonPath("$[0].leadEmail").value("alice@example.com"));
    }

    @Test
    void testGetAreaById_Found() throws Exception {
        Mockito.when(areaService.getAreaById(1L)).thenReturn(Optional.of(sampleArea));

        mockMvc.perform(get("/api/areas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Engineering"));
    }

    @Test
    void testGetAreaById_NotFound() throws Exception {
        Mockito.when(areaService.getAreaById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/areas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddArea() throws Exception {
        Mockito.when(areaService.addArea(any(Area.class))).thenReturn(sampleArea);

        mockMvc.perform(post("/api/areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleArea)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Engineering"));
    }

    @Test
    void testUpdateArea_Found() throws Exception {
        Mockito.when(areaService.updateArea(eq(1L), any(Area.class))).thenReturn(sampleArea);

        mockMvc.perform(put("/api/areas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleArea)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Engineering"));
    }

    @Test
    void testUpdateArea_NotFound() throws Exception {
        Mockito.when(areaService.updateArea(eq(999L), any(Area.class))).thenReturn(null);

        mockMvc.perform(put("/api/areas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleArea)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteArea_Found() throws Exception {
        Mockito.when(areaService.deleteArea(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/areas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteArea_NotFound() throws Exception {
        Mockito.when(areaService.deleteArea(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/areas/999"))
                .andExpect(status().isNotFound());
    }
}
