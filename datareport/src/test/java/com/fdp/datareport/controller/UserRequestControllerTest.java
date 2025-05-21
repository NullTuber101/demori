package com.fdp.datareport.controller;

import com.fdp.datareport.entity.User;
import com.fdp.datareport.entity.UserRequest;
import com.fdp.datareport.enums.RequestStatus;
import com.fdp.datareport.service.UserRequestService;
import com.fdp.datareport.service.UserService;
import com.fdp.datareport.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
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
    private UserRequest validRequest;

    @BeforeEach
    void setup() {
        superUserToken = "Bearer " + jwtUtil.generateToken("super123", "SUPER_USER");

        validRequest = UserRequest.builder()
                .id(1L)
                .brid("new123")
                .email("new@example.com")
                .password("pass123")
                .name("New User")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Signup tests
    @Test
    void shouldSubmitSignupRequestSuccessfully() throws Exception {
        when(userService.isDuplicate("new123", "new@example.com")).thenReturn(false);
        when(userRequestService.isDuplicate("new123", "new@example.com")).thenReturn(false);
        when(userRequestService.createRequest(any(UserRequest.class)))
                .thenReturn(UserRequest.builder().id(1L).build());

        mockMvc.perform(post("/api/requests/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Signup request submitted successfully."))
                .andExpect(jsonPath("$.requestId").value(1));
    }

    @Test
    void shouldReturn409IfDuplicateSignup() throws Exception {
        when(userService.isDuplicate("dupe", "dupe@example.com")).thenReturn(true);

        UserRequest request = UserRequest.builder()
                .brid("dupe")
                .email("dupe@example.com")
                .password("pass")
                .name("Dummy")  // Now passes validation
                .build();

        mockMvc.perform(post("/api/requests/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("BRID or Email already exists or is under review."));
    }


    @Test
    void shouldReturn409IfDataIntegrityViolation() throws Exception {
        when(userService.isDuplicate(any(), any())).thenReturn(false);
        when(userRequestService.isDuplicate(any(), any())).thenReturn(false);
        when(userRequestService.createRequest(any())).thenThrow(new DataIntegrityViolationException("conflict"));

        mockMvc.perform(post("/api/requests/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate BRID or Email detected."));
    }

    @Test
    void shouldReturn500IfSignupFails() throws Exception {
        when(userService.isDuplicate(any(), any())).thenReturn(false);
        when(userRequestService.isDuplicate(any(), any())).thenReturn(false);
        when(userRequestService.createRequest(any())).thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(post("/api/requests/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Signup failed: DB down"));
    }

    @Test
    void shouldReturn400IfSignupRequestIsInvalid() throws Exception {
        // Missing brid, email, password
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setName("Invalid User");

        mockMvc.perform(post("/api/requests/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages").isArray());
    }

    //  Pending requests
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

    //  Approve request
    @Test
    void shouldReturn404IfApproveRequestNotFound() throws Exception {
        when(userRequestService.getRequestById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/requests/99/approve?roleName=EDITOR")
                        .header("Authorization", superUserToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Request not found"));
    }

    @Test
    void shouldReturn400IfApproveAlreadyReviewed() throws Exception {
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

    @Test
    void shouldApproveRequestSuccessfully() throws Exception {
        when(userRequestService.getRequestById(1L)).thenReturn(Optional.of(validRequest));
        when(userService.approveRequest(eq(validRequest), eq("EDITOR")))
                .thenReturn(User.builder().id(101L).build());

        mockMvc.perform(post("/api/requests/1/approve?roleName=EDITOR")
                        .header("Authorization", superUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User approved successfully."))
                .andExpect(jsonPath("$.userId").value(101));
    }

    @Test
    void shouldReturn409IfApproveConflict() throws Exception {
        when(userRequestService.getRequestById(1L)).thenReturn(Optional.of(validRequest));
        when(userService.approveRequest(eq(validRequest), eq("EDITOR")))
                .thenThrow(new DataIntegrityViolationException("conflict"));

        mockMvc.perform(post("/api/requests/1/approve?roleName=EDITOR")
                        .header("Authorization", superUserToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("BRID or Email already exists."));
    }

    @Test
    void shouldReturn500IfApprovalFails() throws Exception {
        when(userRequestService.getRequestById(1L)).thenReturn(Optional.of(validRequest));
        when(userService.approveRequest(eq(validRequest), eq("EDITOR")))
                .thenThrow(new RuntimeException("Approval exception"));

        mockMvc.perform(post("/api/requests/1/approve?roleName=EDITOR")
                        .header("Authorization", superUserToken))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Approval failed: Approval exception"));
    }

    //  Reject request
    @Test
    void shouldReturn404IfRejectRequestNotFound() throws Exception {
        when(userRequestService.getRequestById(55L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/requests/55/reject?reason=invalid")
                        .header("Authorization", superUserToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Request not found"));
    }

    @Test
    void shouldReturn400IfRejectAlreadyReviewed() throws Exception {
        UserRequest reviewed = UserRequest.builder()
                .id(2L)
                .status(RequestStatus.REJECTED)
                .build();

        when(userRequestService.getRequestById(2L)).thenReturn(Optional.of(reviewed));

        mockMvc.perform(post("/api/requests/2/reject?reason=invalid")
                        .header("Authorization", superUserToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Request already reviewed."));
    }

    @Test
    void shouldRejectRequestSuccessfully() throws Exception {
        when(userRequestService.getRequestById(1L)).thenReturn(Optional.of(validRequest));
        doNothing().when(userRequestService).rejectRequest(1L, "test reason");

        mockMvc.perform(post("/api/requests/1/reject?reason=test reason")
                        .header("Authorization", superUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Request rejected successfully."));
    }
}
