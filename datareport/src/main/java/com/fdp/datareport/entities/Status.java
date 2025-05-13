package com.fdp.datareport.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fdp.datareport.validation.annotations.ValidPercentage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="Status name is required")
    @Size(max = 30, message = "Status Name must not exceed 30 characters")
    private String statusName;
    @ValidPercentage
    private Integer percentage;


}
