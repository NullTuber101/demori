package com.fdp.datareport.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrumArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message="Area Name is required")
    private String areaName;
    @NotNull(message="Scrum Master Name is required")
    private String scrumMaster;
    @NotNull(message="Scrum Team Name is required")
    private String scrumTeam;
    @NotNull(message="Board Id is required")
    private String boardId;

}