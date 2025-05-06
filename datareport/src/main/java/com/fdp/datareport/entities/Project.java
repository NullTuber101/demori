package com.fdp.datareport.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fdp.datareport.validation.annotations.ValidDateRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message="Project Name is required")
    private String projectName;
    @Lob
    private String description;

    @NotNull(message="Developer Name is required")
    @Lob
    private String developer;

    @NotNull(message="Jira is required")
    @Lob
    private String jira;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // Automatically delete projects when the Area is deleted
    private Area area;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    @OnDelete(action = OnDeleteAction.SET_NULL) // Set Status to null when the Status is deleted
    private Status status;
    @NotNull(message="Start Date is required")
    private Date startDate;
    @NotNull(message="End Date is required")
    private Date endDate;


        // This method is invoked before a Project is deleted
    @PreRemove
    void onDelete() {
        // The cascading behavior for Area and Status is already handled by annotations
        // No need to do manual nullification here for cascading delete or nullification
    }
}
