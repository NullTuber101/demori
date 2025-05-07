package com.fdp.datareport.entities;

import com.fdp.datareport.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull(message = "BRID is required")
    private String brid;

    @Column(unique = true, nullable = false)
    @NotNull(message = "Email is required")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "Password is required")
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private String rejectionReason;

    private LocalDateTime createdAt = LocalDateTime.now();
}
