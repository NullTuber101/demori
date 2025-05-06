package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.Status;
import com.fdp.datareport.services.StatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatusController.class)
public class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    @MockBean
    private StatusService statusService;  // Mocking the service layer

    private Status validStatus;
    private Status invalidStatus;

    @BeforeEach
    public void setUp() {
        validStatus = new Status(null, "In Progress", 80);  // Valid status
        invalidStatus = new Status(null, "Invalid", 150);   // Invalid status (percentage > 100)
    }

    @Test
    public void testGetAllStatuses() throws Exception {
        // Mock the service to return a list of statuses
        when(statusService.getAllStatuses()).thenReturn(List.of(validStatus));

        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$[0].statusName").value("In Progress"))
                .andExpect(jsonPath("$[0].percentage").value(80));
    }

    @Test
    public void testGetStatusById_Existing() throws Exception {
        // Mock the service to return a status by id
        when(statusService.getStatusById(1L)).thenReturn(java.util.Optional.of(validStatus));

        mockMvc.perform(get("/api/statuses/1"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.statusName").value("In Progress"))
                .andExpect(jsonPath("$.percentage").value(80));
    }

    @Test
    public void testGetStatusById_NotFound() throws Exception {
        // Mock the service to return an empty Optional when ID does not exist
        when(statusService.getStatusById(1L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/statuses/1"))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(content().string(""));  // Body should be empty
    }

    @Test
    public void testAddStatus_Valid() throws Exception {
        // Mock the service to return the valid status when created
        when(statusService.createStatus(validStatus)).thenReturn(validStatus);

        // Convert validStatus to JSON and send a POST request
        String validStatusJson = objectMapper.writeValueAsString(validStatus);

        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validStatusJson))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.statusName").value("In Progress"))
                .andExpect(jsonPath("$.percentage").value(80));
    }

    @Test
    public void testAddStatus_InvalidPercentage() throws Exception {
        // Convert invalidStatus to JSON and send a POST request
        String invalidStatusJson = objectMapper.writeValueAsString(invalidStatus);

        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidStatusJson))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.message[0]").value("percentage: Percentage must be between 0 and 100"));
    }

    @Test
    public void testUpdateStatus_Valid() throws Exception {
        // Mock the service to return updated status
        when(statusService.updateStatus(1L, validStatus)).thenReturn(validStatus);

        // Convert validStatus to JSON and send a PUT request
        String validStatusJson = objectMapper.writeValueAsString(validStatus);

        mockMvc.perform(put("/api/statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validStatusJson))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.statusName").value("In Progress"))
                .andExpect(jsonPath("$.percentage").value(80));
    }

    @Test
    public void testUpdateStatus_NotFound() throws Exception {
        // Mock the service to return null (indicating that status was not found)
        when(statusService.updateStatus(1L, validStatus)).thenReturn(null);

        String validStatusJson = objectMapper.writeValueAsString(validStatus);

        mockMvc.perform(put("/api/statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validStatusJson))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }

    @Test
    public void testDeleteStatus_Success() throws Exception {
        // Mock the service to return true (indicating successful deletion)
        when(statusService.deleteStatus(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/statuses/1"))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    @Test
    public void testDeleteStatus_NotFound() throws Exception {
        // Mock the service to return false (indicating status was not found)
        when(statusService.deleteStatus(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/statuses/1"))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }
}
