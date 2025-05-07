package com.fdp.datareport.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ScrumAreaTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidScrumArea() {
        ScrumArea area = new ScrumArea(
                1L,
                "Test Area",
                "Test Master",
                "Test Team",
                "Board-123"
        );

        Set<ConstraintViolation<ScrumArea>> violations = validator.validate(area);
        assertThat(violations).isEmpty();
    }

    @Test
    void testMissingFields() {
        ScrumArea area = new ScrumArea(); // all fields null

        Set<ConstraintViolation<ScrumArea>> violations = validator.validate(area);

        assertThat(violations).hasSizeGreaterThan(0);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("areaName"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("scrumMaster"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("scrumTeam"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("boardId"));
    }
}
