package com.fdp.datareport.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrumArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Area Name is required")
    @Size(max = 30, message = "Scrum Area Name must not exceed 30 characters")
    private String areaName;

    @NotBlank(message = "Scrum Master Name is required")
    private String scrumMaster;

    @NotBlank(message = "Scrum Team Name is required")
    private String scrumTeam;

    @NotBlank(message = "Board Id is required")
    private String boardId;

}