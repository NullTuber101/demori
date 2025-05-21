package com.fdp.datareport.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Role name is required")
    @Size(max = 30, message = "Role name must not exceed 30 characters")
    private String roleName; // SUPER_USER, VIEWER, EDITOR

    private boolean canApproveUsers;
    private boolean canWrite;
    private boolean canDelete;
}
