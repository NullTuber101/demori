package com.fdp.datareport.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdp.datareport.entities.Area;
import com.fdp.datareport.entities.Project;
import com.fdp.datareport.entities.Status;
import com.fdp.datareport.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private Project sampleProject;

    @BeforeEach
    void setup() {
        Area area = new Area(1L, "Platform", "Jane Doe", "jane@example.com");
        Status status = new Status(1L, "In Progress", 50);

        sampleProject = new Project(
                1L,
                "Data Platform",
                "Analytics system for data ingestion.",
                "Alice",
                "DP-1001",
                area,
                status,
                new Date(System.currentTimeMillis() - 86400000L),  // yesterday
                new Date(System.currentTimeMillis())               // today
        );
    }

    @Test
    void testCreateProject() throws Exception {
        Mockito.when(projectService.createProject(any(Project.class))).thenReturn(sampleProject);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Data Platform"));
    }

    @Test
    void testGetAllProjects() throws Exception {
        Mockito.when(projectService.getAllProjects()).thenReturn(List.of(sampleProject));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].developer").value("Alice"));
    }

    @Test
    void testGetProjectById_Found() throws Exception {
        Mockito.when(projectService.getProjectById(1L)).thenReturn(Optional.of(sampleProject));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jira").value("DP-1001"));
    }

    @Test
    void testGetProjectById_NotFound() throws Exception {
        Mockito.when(projectService.getProjectById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProject_Found() throws Exception {
        Mockito.when(projectService.updateProject(eq(1L), any(Project.class)))
                .thenReturn(Optional.of(sampleProject));

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Analytics system for data ingestion."));
    }

    @Test
    void testUpdateProject_NotFound() throws Exception {
        Mockito.when(projectService.updateProject(eq(2L), any(Project.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/projects/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProject)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProject_Found() throws Exception {
        Mockito.when(projectService.deleteProject(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProject_NotFound() throws Exception {
        Mockito.when(projectService.deleteProject(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/projects/99"))
                .andExpect(status().isNotFound());
    }
}
