package com.fdp.datareport.exceptions;

import com.fdp.datareport.controllers.ProjectController;
import com.fdp.datareport.entities.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fdp.datareport.services.ProjectService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHandleValidationErrors() throws Exception {
        // Create a project object with missing required fields
        Project invalidProject = new Project();
        invalidProject.setDescription("Test Description");
        invalidProject.setJira("JIRA-123"); // Missing projectName, developer, dates

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProject)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message[?(@ =~ /.*projectName.*/)]").exists())
                .andExpect(jsonPath("$.message[?(@ =~ /.*developer.*/)]").exists())
                .andExpect(jsonPath("$.message[?(@ =~ /.*startDate.*/)]").exists())
                .andExpect(jsonPath("$.message[?(@ =~ /.*endDate.*/)]").exists());
    }
}
