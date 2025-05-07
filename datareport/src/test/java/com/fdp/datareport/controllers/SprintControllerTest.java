package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Project;
import com.fdp.datareport.entities.Sprint;
import com.fdp.datareport.services.SprintService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SprintService sprintService;

    private String editorToken;
    private String viewerToken;
    private Sprint sprint;

    @BeforeEach
    void setUp() {
        editorToken = "Bearer " + jwtUtil.generateToken("editor1", "EDITOR");
        viewerToken = "Bearer " + jwtUtil.generateToken("viewer1", "VIEWER");

        sprint = new Sprint();
        sprint.setId(1L);
        sprint.setSprintName("Sprint Alpha");
        sprint.setSprintJira("JIRA-001");
        sprint.setSprintDescription("Test Description");
        sprint.setAssignedTo("DevX");
        sprint.setSprintStartDate(new Date());
        sprint.setSprintEndDate(new Date(System.currentTimeMillis() + 86400000));
        sprint.setProject(new Project());
    }

    @Test
    void createSprintWithoutTokenShouldFail() throws Exception {
        mockMvc.perform(post("/api/projects/1/sprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSprintWithEditorTokenShouldSucceed() throws Exception {
        when(sprintService.createSprint(any(Long.class), any(Sprint.class))).thenReturn(sprint);

        mockMvc.perform(post("/api/projects/1/sprints")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sprintName").value("Sprint Alpha"));
    }

    @Test
    void getSprintsByProjectShouldSucceedWithoutAuth() throws Exception {
        when(sprintService.getSprintsByProject(1L)).thenReturn(List.of(sprint));

        mockMvc.perform(get("/api/projects/1/sprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteSprintShouldReturn403WithoutToken() throws Exception {
        mockMvc.perform(delete("/api/projects/sprints/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteSprintWithEditorTokenShouldSucceed() throws Exception {
        doNothing().when(sprintService).deleteSprint(1L);

        mockMvc.perform(delete("/api/projects/sprints/1")
                        .header("Authorization", editorToken))
                .andExpect(status().isNoContent());
    }
}
