package com.fdp.datareport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdp.datareport.entity.Project;
import com.fdp.datareport.entity.Sprint;
import com.fdp.datareport.entity.Status;
import com.fdp.datareport.service.SprintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SprintController.class)
class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    @Autowired
    private ObjectMapper objectMapper;

    private Sprint sprint;

    @BeforeEach
    void setUp() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse("2024-01-01");
        Date endDate = sdf.parse("2024-01-15");

        sprint = new Sprint();
        sprint.setId(1L);
        sprint.setSprintName("Sprint Alpha");
        sprint.setSprintStartDate(startDate);
        sprint.setSprintEndDate(endDate);
        sprint.setSprintJira("https://jira.example.com/SprintAlpha");
        sprint.setSprintDescription("Initial sprint for testing");
        sprint.setAssignedTo("John Doe");
        sprint.setSprintFor(new Status());  // Assume not null but content irrelevant for test
        sprint.setProject(new Project());  // Assume not null but content irrelevant for test
    }

    @Test
    void testCreateSprintSuccess() throws Exception {
        Mockito.when(sprintService.createSprint(eq(1L), any(Sprint.class))).thenReturn(sprint);

        mockMvc.perform(post("/api/projects/1/sprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sprintName", is("Sprint Alpha")));
    }

    @Test
    void testCreateSprintFailure() throws Exception {
        Mockito.when(sprintService.createSprint(eq(1L), any(Sprint.class)))
                .thenThrow(new RuntimeException("Creation error"));

        mockMvc.perform(post("/api/projects/1/sprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Creation error")));
    }

    @Test
    void testGetSprintsByProject() throws Exception {
        Mockito.when(sprintService.getSprintsByProject(1L)).thenReturn(List.of(sprint));

        mockMvc.perform(get("/api/projects/1/sprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sprintName", is("Sprint Alpha")));
    }

    @Test
    void testGetSprintByIdSuccess() throws Exception {
        Mockito.when(sprintService.getSprintById(1L)).thenReturn(sprint);

        mockMvc.perform(get("/api/projects/sprints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintName", is("Sprint Alpha")));
    }

    @Test
    void testGetSprintByIdNotFound() throws Exception {
        Mockito.when(sprintService.getSprintById(1L)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/projects/sprints/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Sprint with ID 1 not found")));
    }

    @Test
    void testUpdateSprintSuccess() throws Exception {
        Mockito.when(sprintService.updateSprint(eq(1L), any(Sprint.class))).thenReturn(sprint);

        mockMvc.perform(put("/api/projects/sprints/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintName", is("Sprint Alpha")));
    }

    @Test
    void testUpdateSprintNotFound() throws Exception {
        Mockito.when(sprintService.updateSprint(eq(1L), any(Sprint.class)))
                .thenThrow(new RuntimeException());

        mockMvc.perform(put("/api/projects/sprints/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Sprint with ID 1 not found")));
    }

    @Test
    void testDeleteSprintSuccess() throws Exception {
        mockMvc.perform(delete("/api/projects/sprints/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteSprintNotFound() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(sprintService).deleteSprint(1L);

        mockMvc.perform(delete("/api/projects/sprints/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Sprint with ID 1 not found")));
    }
}
