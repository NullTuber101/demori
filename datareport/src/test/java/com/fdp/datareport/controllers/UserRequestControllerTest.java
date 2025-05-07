package com.fdp.datareport.controllers;

import com.fdp.datareport.entities.UserRequest;
import com.fdp.datareport.enums.RequestStatus;
import com.fdp.datareport.services.UserRequestService;
import com.fdp.datareport.services.UserService;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserRequestControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserRequestService userRequestService;
    @MockBean private UserService userService;

    private String superUserToken;

    @BeforeEach
    void setup() {
        superUserToken = "Bearer " + jwtUtil.generateToken("super123", "SUPER_USER");
    }

    @Test
    void shouldSubmitSignupRequestSuccessfully() throws Exception {
        UserRequest request = UserRequest.builder()
                .brid("new123")
                .email("new@example.com")
                .password("pass123")
                .name("New User")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.isDuplicate("new123", "new@example.com")).thenReturn(false);
        when(userRequestService.isDuplicate("new123", "new@example.com")).thenReturn(false);
        when(userRequestService.createRequest(any(UserRequest.class)))
                .thenReturn(UserRequest.builder().id(1L).build());

        mockMvc.perform(post("/api/requests/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Signup request submitted successfully."))
                .andExpect(jsonPath("$.requestId").value(1));
    }

    @Test
    void shouldReturn409IfDuplicateSignup() throws Exception {
        UserRequest request = UserRequest.builder()
                .brid("dupe")
                .email("dupe@example.com")
                .password("pass")
                .build();

        when(userService.isDuplicate("dupe", "dupe@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/requests/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("BRID or Email already exists or is under review."));
    }

    @Test
    void shouldGetPendingRequestsWithSuperUser() throws Exception {
        mockMvc.perform(get("/api/requests/pending")
                        .header("Authorization", superUserToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403WhenFetchingPendingRequestsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/requests/pending"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404IfApproveRequestNotFound() throws Exception {
        when(userRequestService.getRequestById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/requests/99/approve?roleName=EDITOR")
                        .header("Authorization", superUserToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Request not found"));
    }

    @Test
    void shouldReturn400IfAlreadyReviewed() throws Exception {
        UserRequest reviewed = UserRequest.builder()
                .id(1L)
                .status(RequestStatus.APPROVED)
                .build();

        when(userRequestService.getRequestById(1L)).thenReturn(Optional.of(reviewed));

        mockMvc.perform(post("/api/requests/1/approve?roleName=EDITOR")
                        .header("Authorization", superUserToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Request already reviewed."));
    }
}
