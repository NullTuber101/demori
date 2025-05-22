package com.fdp.datareport.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdp.datareport.controller.VelocityController;
import com.fdp.datareport.entity.ScrumArea;
import com.fdp.datareport.entity.Velocity;
import com.fdp.datareport.service.VelocityService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VelocityController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VelocityService velocityService;

    @Autowired
    private ObjectMapper objectMapper;

    private Velocity validVelocity;
    private ScrumArea validScrumArea;

    @BeforeEach
    void setUp() {
        validScrumArea = new ScrumArea(1L, "Backend", "Alice", "Team A", "BRD-001");

        validVelocity = new Velocity();
        validVelocity.setId(1L);
        validVelocity.setSprintName("Sprint A");
        validVelocity.setVelocity(10.0f);
        validVelocity.setSprintEndDate(LocalDate.now());
        validVelocity.setScrumArea(validScrumArea);
    }

    @Test
    void testHandleValidationException() throws Exception {
        Velocity invalidVelocity = new Velocity(); // Missing required fields

        mockMvc.perform(post("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidVelocity)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.messages", not(empty())));
    }

    @Test
    void testHandleDataIntegrityViolationException() throws Exception {
        Mockito.when(velocityService.createVelocity(eq(1L), any(Velocity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        mockMvc.perform(post("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVelocity)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.messages[0]", containsString("Duplicate key")));
    }

    @Test
    void testHandleIllegalArgumentException() throws Exception {
        Mockito.when(velocityService.createVelocity(eq(1L), any(Velocity.class)))
                .thenThrow(new IllegalArgumentException("Invalid velocity data"));

        mockMvc.perform(post("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVelocity)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.messages[0]", is("Invalid velocity data")));
    }

    @Test
    void testHandleEntityNotFoundException() throws Exception {
        Mockito.when(velocityService.createVelocity(eq(1L), any(Velocity.class)))
                .thenThrow(new EntityNotFoundException("Scrum area not found"));

        mockMvc.perform(post("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVelocity)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.messages[0]", is("Scrum area not found")));
    }

    @Test
    void testHandleGenericException() throws Exception {
        Mockito.when(velocityService.createVelocity(eq(1L), any(Velocity.class)))
                .thenThrow(new RuntimeException("Unexpected crash"));

        mockMvc.perform(post("/api/velocities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVelocity)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.messages[0]", is("Something went wrong")));
    }
}
