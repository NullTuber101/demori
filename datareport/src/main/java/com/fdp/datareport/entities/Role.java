package com.fdp.datareport.entities;

import jakarta.persistence.*;
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
    private String roleName; // SUPER_USER, VIEWER, EDITOR

    private boolean canApproveUsers;
    private boolean canWrite;
    private boolean canDelete;
}
