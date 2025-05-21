package com.fdp.datareport.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fdp.datareport.validation.annotations.ValidDateRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@ValidDateRange(startField = "startDate", endField = "endDate")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project Name is required")
    @Size(max = 30, message = "Project Name must not exceed 30 characters. Please enter the details in description")
    private String projectName;

    @Lob
    private String description;

    @NotBlank(message = "Developer Name is required")
    @Lob
    private String developer;

    @NotBlank(message = "Jira is required")
    @Lob
    private String jira;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Area area;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Status status;

    @NotNull(message = "Start Date is required")
    private Date startDate;

    @NotNull(message = "End Date is required")
    private Date endDate;

    @PreRemove
    void onDelete() {
        // No-op; cascade handles it
    }
}
