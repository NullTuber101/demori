package com.fdp.datareport.entities;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class VelocityTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidVelocity() {
        ScrumArea scrumArea = new ScrumArea(1L, "Area A", "SM", "Team A", "BOARD-123");
        Velocity velocity = new Velocity(null, "Sprint 1", 30.0f, LocalDate.now(), scrumArea);

        Set<ConstraintViolation<Velocity>> violations = validator.validate(velocity);
        assertThat(violations).isEmpty();
    }

    @Test
    void testMissingRequiredFields() {
        Velocity velocity = new Velocity(); // All fields null

        Set<ConstraintViolation<Velocity>> violations = validator.validate(velocity);
        assertThat(violations).hasSizeGreaterThan(0);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Sprint Name is required"));
        assertThat(violations).anyMatch(v -> v.getMessage().contains("velocity is required"));
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Sprint End Date is required"));
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Scrum Area is required"));
    }

    @Test
    void testNegativeVelocity() {
        ScrumArea scrumArea = new ScrumArea(1L, "Area A", "SM", "Team A", "BOARD-123");
        Velocity velocity = new Velocity(null, "Sprint 1", -5.0f, LocalDate.now(), scrumArea);

        Set<ConstraintViolation<Velocity>> violations = validator.validate(velocity);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Velocity must be zero or positive"));
    }
}
