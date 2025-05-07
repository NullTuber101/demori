package com.fdp.datareport.services;

import com.fdp.datareport.entities.UserRequest;
import com.fdp.datareport.enums.RequestStatus;
import com.fdp.datareport.repositories.UserRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserRequestServiceTest {

    @Mock
    private UserRequestRepository userRequestRepository;

    @InjectMocks
    private UserRequestService userRequestService;

    private UserRequest sampleRequest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        sampleRequest = UserRequest.builder()
                .id(1L)
                .brid("BR123")
                .email("test@example.com")
                .password("secure")
                .name("Test User")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateRequest() {
        when(userRequestRepository.save(any())).thenReturn(sampleRequest);

        UserRequest created = userRequestService.createRequest(new UserRequest());
        assertThat(created.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @Test
    void testGetPendingRequests() {
        when(userRequestRepository.findAllByStatus(RequestStatus.PENDING)).thenReturn(List.of(sampleRequest));

        List<UserRequest> result = userRequestService.getPendingRequests();
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetRequestById() {
        when(userRequestRepository.findById(1L)).thenReturn(Optional.of(sampleRequest));

        Optional<UserRequest> result = userRequestService.getRequestById(1L);
        assertThat(result).isPresent().contains(sampleRequest);
    }

    @Test
    void testRejectRequest() {
        when(userRequestRepository.findById(1L)).thenReturn(Optional.of(sampleRequest));
        when(userRequestRepository.save(any())).thenReturn(sampleRequest);

        userRequestService.rejectRequest(1L, "Invalid");

        verify(userRequestRepository).save(argThat(req ->
                req.getStatus() == RequestStatus.REJECTED && "Invalid".equals(req.getRejectionReason())));
    }

    @Test
    void testIsDuplicateReturnsTrueIfBridOrEmailExists() {
        when(userRequestRepository.existsByBrid("BR123")).thenReturn(true);
        when(userRequestRepository.existsByEmail("other@example.com")).thenReturn(false);

        assertThat(userRequestService.isDuplicate("BR123", "other@example.com")).isTrue();
    }

    @Test
    void testIsDuplicateReturnsFalseIfNeitherExists() {
        when(userRequestRepository.existsByBrid("BR123")).thenReturn(false);
        when(userRequestRepository.existsByEmail("test@example.com")).thenReturn(false);

        assertThat(userRequestService.isDuplicate("BR123", "test@example.com")).isFalse();
    }

    @Test
    void testSave() {
        when(userRequestRepository.save(any())).thenReturn(sampleRequest);

        UserRequest saved = userRequestService.save(sampleRequest);
        assertThat(saved).isEqualTo(sampleRequest);
    }

    @Test
    void testCleanOldRejectedRequests() {
        userRequestService.cleanOldRejectedRequests();
        verify(userRequestRepository).deleteAllByStatusAndCreatedAtBefore(eq(RequestStatus.REJECTED), any());
    }
}
