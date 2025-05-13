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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
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
        editorToken = "Bearer " + jwtUtil.generateToken("editorUser", "EDITOR");
        viewerToken = "Bearer " + jwtUtil.generateToken("viewerUser", "VIEWER");

        mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setProjectName("Test Project");
        mockProject.setDeveloper("devX");
        mockProject.setJira("JIRA-999");
        mockProject.setStartDate(new Date());
        mockProject.setEndDate(new Date(System.currentTimeMillis() + 86400000));
    }

    // ✅ GET all projects
    @Test
    void testGetAllProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of(mockProject));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Test Project"));
    }

    // ✅ GET project by id - success
    @Test
    void testGetProjectByIdFound() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(mockProject));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jira").value("JIRA-999"));
    }

    // ❌ GET project by id - not found
    @Test
    void testGetProjectByIdNotFound() throws Exception {
        when(projectService.getProjectById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Project not found"));
    }

    // ✅ POST create project - with valid editor token
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

    // ❌ POST create project - with viewer token
    @Test
    void testCreateProjectWithViewerTokenShouldFail() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .header("Authorization", viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject)))
                .andExpect(status().isForbidden());
    }

    // ❌ POST create project - without token
    @Test
    void testCreateProjectWithoutTokenShouldFail() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject)))
                .andExpect(status().isForbidden());
    }

    // ✅ PUT update project - success
    @Test
    void testUpdateProjectWithEditorTokenShouldSucceed() throws Exception {
        when(projectService.updateProject(eq(1L), any(Project.class))).thenReturn(Optional.of(mockProject));

        mockMvc.perform(put("/api/projects/1")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jira").value("JIRA-999"));
    }

    // ❌ PUT update project - not found
    @Test
    void testUpdateProjectNotFound() throws Exception {
        when(projectService.updateProject(eq(99L), any(Project.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/projects/99")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Project not found"));
    }

    // ❌ PUT update project - viewer not allowed
    @Test
    void testUpdateProjectWithViewerTokenShouldFail() throws Exception {
        mockMvc.perform(put("/api/projects/1")
                        .header("Authorization", viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject)))
                .andExpect(status().isForbidden());
    }

    // ✅ DELETE project - success
    @Test
    void testDeleteProjectWithEditorTokenShouldSucceed() throws Exception {
        when(projectService.deleteProject(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/projects/1")
                        .header("Authorization", editorToken))
                .andExpect(status().isNoContent());
    }

    // ❌ DELETE project - not found
    @Test
    void testDeleteProjectNotFound() throws Exception {
        when(projectService.deleteProject(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/projects/99")
                        .header("Authorization", editorToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Project not found"));
    }

    // ❌ DELETE project - viewer not allowed
    @Test
    void testDeleteProjectWithViewerTokenShouldFail() throws Exception {
        mockMvc.perform(delete("/api/projects/1")
                        .header("Authorization", viewerToken))
                .andExpect(status().isForbidden());
    }
}
