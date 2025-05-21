package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Status;
import com.fdp.datareport.service.StatusService;
import com.fdp.datareport.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatusService statusService;

    private String editorToken;
    private String viewerToken;

    private Status sampleStatus;

    @BeforeEach
    void setUp() {
        editorToken = "Bearer " + jwtUtil.generateToken("editor", "EDITOR");
        viewerToken = "Bearer " + jwtUtil.generateToken("viewer", "VIEWER");

        sampleStatus = new Status();
        sampleStatus.setId(1L);
        sampleStatus.setStatusName("In Progress");
        sampleStatus.setPercentage(50);
    }

    @Test
    void shouldReturnAllStatusesWithoutAuth() throws Exception {
        when(statusService.getAllStatuses()).thenReturn(List.of(sampleStatus));

        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnStatusById() throws Exception {
        when(statusService.getStatusById(1L)).thenReturn(java.util.Optional.of(sampleStatus));

        mockMvc.perform(get("/api/statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName").value("In Progress"));
    }

    @Test
    void shouldReturn404ForMissingStatus() throws Exception {
        when(statusService.getStatusById(99L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/statuses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateStatusWithEditorRole() throws Exception {
        when(statusService.createStatus(any(Status.class))).thenReturn(sampleStatus);

        mockMvc.perform(post("/api/statuses")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleStatus)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusName").value("In Progress"));
    }

    @Test
    void shouldReturnForbiddenWithoutTokenOnCreate() throws Exception {
        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleStatus)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateStatusWithEditorRole() throws Exception {
        when(statusService.updateStatus(eq(1L), any(Status.class))).thenReturn(sampleStatus);

        mockMvc.perform(put("/api/statuses/1")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleStatus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percentage").value(50));
    }

    @Test
    void shouldReturn404OnUpdateIfNotFound() throws Exception {
        when(statusService.updateStatus(eq(999L), any(Status.class))).thenReturn(null);

        mockMvc.perform(put("/api/statuses/999")
                        .header("Authorization", editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleStatus)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteStatusWithEditorRole() throws Exception {
        when(statusService.deleteStatus(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/statuses/1")
                        .header("Authorization", editorToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404OnDeleteIfNotFound() throws Exception {
        when(statusService.deleteStatus(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/statuses/999")
                        .header("Authorization", editorToken))
                .andExpect(status().isNotFound());
    }
}
