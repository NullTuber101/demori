package com.fdp.datareport.service;

import com.fdp.datareport.entity.UserRequest;
import com.fdp.datareport.enums.RequestStatus;
import com.fdp.datareport.repository.UserRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRequestService {

    private final UserRequestRepository userRequestRepository;

    public UserRequest createRequest(UserRequest request) {
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        return userRequestRepository.save(request);
    }

    public List<UserRequest> getPendingRequests() {
        return userRequestRepository.findAllByStatus(RequestStatus.PENDING);
    }

    public Optional<UserRequest> getRequestById(Long id) {
        return userRequestRepository.findById(id);
    }

    public void rejectRequest(Long id, String reason) {
        userRequestRepository.findById(id).ifPresent(req -> {
            req.setStatus(RequestStatus.REJECTED);
            req.setRejectionReason(reason);
            userRequestRepository.save(req);
        });
    }

    public boolean isDuplicate(String brid, String email) {
        return userRequestRepository.existsByBrid(brid) || userRequestRepository.existsByEmail(email);
    }
    public UserRequest save(UserRequest request) {
        return userRequestRepository.save(request);
    }
    public void cleanOldRejectedRequests() {
        userRequestRepository.deleteAllByStatusAndCreatedAtBefore(RequestStatus.REJECTED, LocalDateTime.now().minusDays(14));
    }
}
