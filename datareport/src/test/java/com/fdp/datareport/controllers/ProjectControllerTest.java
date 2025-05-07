package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Project;
import com.fdp.datareport.services.ProjectService;
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

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    private String editorToken;
    private String viewerToken;

    private Project mockProject;

    @BeforeEach
    void setup() {
        editorToken = "Bearer " + jwtUtil.generateToken("editor123", "EDITOR");
        viewerToken = "Bearer " + jwtUtil.generateToken("viewer123", "VIEWER");

        mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setProjectName("Test Project");
        mockProject.setDeveloper("devX");
        mockProject.setJira("JIRA-999");
        mockProject.setStartDate(new Date());
        mockProject.setEndDate(new Date(System.currentTimeMillis() + 86400000));
    }

    @Test
    void testCreateProjectWithEditorTokenShouldSucceed() throws Exception {
        when(projectService.createProject(any(Project.class))).thenReturn(mockProject);

        mockMvc.perform(post("/api/projects")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectName").value("Test Project"));
    }

    @Test
    void testCreateProjectWithViewerTokenShouldFail() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .header("Authorization", viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateProjectWithoutTokenShouldFail() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetProjectByIdPublicAccess() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(mockProject));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jira").value("JIRA-999"));
    }

    @Test
    void testDeleteProjectWithEditorToken() throws Exception {
        when(projectService.deleteProject(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/projects/1")
                        .header("Authorization", editorToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProjectWithViewerTokenShouldFail() throws Exception {
        mockMvc.perform(delete("/api/projects/1")
                        .header("Authorization", viewerToken))
                .andExpect(status().isForbidden());
    }
}
