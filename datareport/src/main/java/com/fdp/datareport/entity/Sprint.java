package com.fdp.datareport.entity;


import com.fdp.datareport.validation.annotations.ValidDateRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Sprint name is required")
    @Size(max = 50, message = "Sprint name must not exceed 50 characters")
    private String sprintName;

    @Temporal(TemporalType.DATE)
    @NotNull(message="Sprint start Date is required")
    private Date sprintStartDate;

    @Temporal(TemporalType.DATE)
    @NotNull(message="Sprint End Date is required")
    private Date sprintEndDate;

    @NotBlank(message = "Sprint Jira is required")
    @Lob
    private String sprintJira;

    @Lob
    private String sprintDescription;

    @NotBlank(message = "Assignee is required")
    @Lob
    private String assignedTo;

    @ManyToOne
    @JoinColumn(name = "status_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Status sprintFor;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;
}
