package com.fdp.datareport.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VelocityTest {

    private Validator validator;
    private ScrumArea dummyScrumArea;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        dummyScrumArea = new ScrumArea(
                1L,
                "Platform Engineering",
                "Jane Doe",
                "Alpha Team",
                "BOARD-123"
        );
    }

    @Test
    public void testValidVelocity() {
        Velocity velocity = new Velocity(
                1L,
                "Sprint 1",
                45.0f,
                LocalDate.of(2024, 5, 5),
                dummyScrumArea
        );

        Set<ConstraintViolation<Velocity>> violations = validator.validate(velocity);
        assertTrue(violations.isEmpty(), "Valid velocity should not trigger any validation errors.");
    }

    @Test
    public void testMissingFields() {
        Velocity velocity = new Velocity(); // All fields are null

        Set<ConstraintViolation<Velocity>> violations = validator.validate(velocity);
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        // Hibernate Validator does NOT trigger @PositiveOrZero if value is null
        // So only these are expected:
        // - sprintName: @NotNull
        // - velocity: @NotNull
        // - sprintEndDate: @NotNull
        // - scrumArea: @NotNull
        assertEquals(4, violations.size(), "All required fields should trigger violations");

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sprintName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("velocity") && v.getMessage().equals("velocity is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sprintEndDate")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("scrumArea")));
    }

    @Test
    public void testNegativeVelocity() {
        Velocity velocity = new Velocity(
                2L,
                "Sprint Negative",
                -10.0f,
                LocalDate.of(2024, 6, 15),
                dummyScrumArea
        );

        Set<ConstraintViolation<Velocity>> violations = validator.validate(velocity);
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertEquals(1, violations.size(), "Negative velocity should trigger 1 violation.");
        assertEquals("Velocity must be zero or positive", violations.iterator().next().getMessage());
    }

    @Test
    public void testZeroVelocity() {
        Velocity velocity = new Velocity(
                3L,
                "Sprint Zero",
                0.0f,
                LocalDate.of(2024, 7, 1),
                dummyScrumArea
        );

        Set<ConstraintViolation<Velocity>> violations = validator.validate(velocity);
        assertTrue(violations.isEmpty(), "Zero velocity should be valid.");
    }
}
