package com.fdp.datareport.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Velocity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message="Sprint Name is required")
    @Size(max = 30, message = "Sprint Name must not exceed 30 characters")
    private String sprintName;
    @NotNull(message="velocity is required")
    @PositiveOrZero(message = "Velocity must be zero or positive")
    private Float velocity;
    @NotNull(message="Sprint End Date is required")
    private LocalDate sprintEndDate;

    @NotNull(message = "Scrum Area is required") // MANDATORY
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ScrumArea scrumArea;

}
