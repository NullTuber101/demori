package com.fdp.datareport.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdp.datareport.entities.Project;
import com.fdp.datareport.entities.Sprint;
import com.fdp.datareport.services.SprintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SprintController.class)
public class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    @Autowired
    private ObjectMapper objectMapper;

    private Sprint sprint;

    @BeforeEach
    void setup() {
        sprint = new Sprint();
        sprint.setId(1L);
        sprint.setSprintName("Sprint 1");
        sprint.setSprintStartDate(new Date());
        sprint.setSprintEndDate(new Date());
        sprint.setSprintJira("SPR-101");
        sprint.setAssignedTo("Alice");
        sprint.setSprintDescription("Initial Sprint");
        sprint.setProject(new Project());
    }

    @Test
    void testCreateSprint() throws Exception {
        Mockito.when(sprintService.createSprint(eq(1L), any(Sprint.class))).thenReturn(sprint);

        mockMvc.perform(post("/api/projects/1/sprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sprintName").value("Sprint 1"));
    }

    @Test
    void testGetAllSprintsByProject() throws Exception {
        Mockito.when(sprintService.getSprintsByProject(1L)).thenReturn(List.of(sprint));

        mockMvc.perform(get("/api/projects/1/sprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sprintJira").value("SPR-101"));
    }

    @Test
    void testGetSprintById_Found() throws Exception {
        Mockito.when(sprintService.getSprintById(1L)).thenReturn(sprint);

        mockMvc.perform(get("/api/projects/sprints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedTo").value("Alice"));
    }

    @Test
    void testGetSprintById_NotFound() throws Exception {
        Mockito.when(sprintService.getSprintById(99L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/projects/sprints/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateSprint() throws Exception {
        Mockito.when(sprintService.updateSprint(eq(1L), any(Sprint.class))).thenReturn(sprint);

        mockMvc.perform(put("/api/projects/sprints/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintName").value("Sprint 1"));
    }

    @Test
    void testDeleteSprint() throws Exception {
        mockMvc.perform(delete("/api/projects/sprints/1"))
                .andExpect(status().isNoContent());
    }
}
