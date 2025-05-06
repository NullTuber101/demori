package com.fdp.datareport.entities;


import com.fdp.datareport.validation.annotations.ValidDateRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@ValidDateRange(startField = "sprintStartDate", endField = "sprintEndDate")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message="Sprint Name is required")
    private String sprintName;

    @Temporal(TemporalType.DATE)
    @NotNull(message="Sprint start Date is required")

    private Date sprintStartDate;

    @Temporal(TemporalType.DATE)
    @NotNull(message="Sprint End Date is required")
    private Date sprintEndDate;

    @NotNull(message="Sprint Jira is required")
    @Lob
    private String sprintJira;

    @Lob
    private String sprintDescription;

    @NotNull(message="Assignee is required")
    @Lob
    private String assignedTo;

    // Many sprints can be for one status
    @ManyToOne
    @JoinColumn(name = "status_id")
    @OnDelete(action = OnDeleteAction.SET_NULL) // Set Status to null when the Status is deleted
    private Status sprintFor;

    // Many sprints can be linked to one project
    @ManyToOne
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // Automatically delete projects when the Area is deleted
    private Project project;
}
