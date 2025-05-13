package com.fdp.datareport.controllers;

import com.fdp.datareport.config.SecurityConfig;
import com.fdp.datareport.entities.Area;
import com.fdp.datareport.filters.JwtAuthFilter;
import com.fdp.datareport.services.AreaService;
import com.fdp.datareport.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AreaController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtUtil.class})
public class AreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AreaService areaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Area area;

    @BeforeEach
    void setup() {
        area = new Area(1L, "Engineering", "Alice", "alice@test.com");
    }

    @Test
    void testGetAllAreas() throws Exception {
        Mockito.when(areaService.getAllAreas()).thenReturn(List.of(area));

        mockMvc.perform(get("/api/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Engineering"));
    }

    @Test
    void testGetAreaByIdFound() throws Exception {
        Mockito.when(areaService.getAreaById(1L)).thenReturn(Optional.of(area));

        mockMvc.perform(get("/api/areas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leadName").value("Alice"));
    }

    @Test
    void testGetAreaByIdNotFound() throws Exception {
        Mockito.when(areaService.getAreaById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/areas/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Area not found"));
    }

    @Test
    @WithMockUser(roles = {"EDITOR"})
    void testAddAreaAuthorized() throws Exception {
        Mockito.when(areaService.addArea(any(Area.class))).thenReturn(area);

        mockMvc.perform(post("/api/areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.leadEmail").value("alice@test.com"));
    }

    @Test
    void testAddAreaUnauthorized() throws Exception {
        mockMvc.perform(post("/api/areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER"})
    void testUpdateAreaSuccess() throws Exception {
        Mockito.when(areaService.updateArea(eq(1L), any(Area.class))).thenReturn(area);

        mockMvc.perform(put("/api/areas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Engineering"));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER"})
    void testUpdateAreaNotFound() throws Exception {
        Mockito.when(areaService.updateArea(eq(1L), any(Area.class))).thenReturn(null);

        mockMvc.perform(put("/api/areas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Area not found"));
    }

    @Test
    @WithMockUser(roles = {"EDITOR"})
    void testDeleteAreaSuccess() throws Exception {
        Mockito.when(areaService.deleteArea(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/areas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"EDITOR"})
    void testDeleteAreaNotFound() throws Exception {
        Mockito.when(areaService.deleteArea(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/areas/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Area not found"));
    }

    @Test
    void testGetAreaByIdException() throws Exception {
        Mockito.when(areaService.getAreaById(1L)).thenThrow(new RuntimeException("DB issue"));

        mockMvc.perform(get("/api/areas/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Unexpected error: DB issue"));
    }

    @Test
    @WithMockUser(roles = {"EDITOR"})
    void testAddAreaThrowsException() throws Exception {
        Mockito.when(areaService.addArea(any())).thenThrow(new RuntimeException("Insert failed"));

        mockMvc.perform(post("/api/areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to create area: Insert failed"));
    }

    @Test
    @WithMockUser(roles = {"EDITOR"})
    void testUpdateAreaThrowsException() throws Exception {
        Mockito.when(areaService.updateArea(eq(1L), any())).thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/areas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to update area: Update failed"));
    }

    @Test
    @WithMockUser(roles = {"EDITOR"})
    void testDeleteAreaThrowsException() throws Exception {
        Mockito.doThrow(new RuntimeException("Delete failed")).when(areaService).deleteArea(1L);

        mockMvc.perform(delete("/api/areas/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to delete area: Delete failed"));
    }

}
