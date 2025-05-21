package com.fdp.datareport.entity;

import com.fdp.datareport.validation.annotations.ValidDateRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

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

    @NotNull(message = "Sprint start Date is required")
    @Temporal(TemporalType.DATE)
    private Date sprintStartDate;

    @NotNull(message = "Sprint End Date is required")
    @Temporal(TemporalType.DATE)
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
