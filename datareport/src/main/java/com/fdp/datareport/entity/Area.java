package com.fdp.datareport.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Area name is required")
    @Size(max = 30, message = "Area name must not exceed 30 characters")
    private String name;

    @NotBlank(message = "Lead name is required")
    @Size(max = 50, message = "Lead name must not exceed 50 characters")
    private String leadName;

    @NotBlank(message = "Lead email is required")
    @Email(message = "Lead email must be a valid email address")
    @Size(max = 100, message = "Lead email must not exceed 100 characters")
    private String leadEmail;
}
