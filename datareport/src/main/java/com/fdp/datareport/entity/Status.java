package com.fdp.datareport.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fdp.datareport.validation.annotations.ValidPercentage;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Status name is required")
    @Size(max = 30, message = "Status name must not exceed 30 characters")
    private String statusName;

    @ValidPercentage
    private Integer percentage;

}
