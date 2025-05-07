package com.fdp.datareport.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users") // "user" is a reserved keyword in many DBs
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull(message = "BRID is required")
    private String brid;

    @Column(nullable = false)
    @NotNull(message = "Name is required")
    private String name;


    @Column(unique = true, nullable = false)
    @NotNull(message = "Email is required")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "Password is required")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
}
