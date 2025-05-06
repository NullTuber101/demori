package com.fdp.datareport.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fdp.datareport.validation.annotations.ValidPercentage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    private String statusName;
    @ValidPercentage
    private Integer percentage;


}
