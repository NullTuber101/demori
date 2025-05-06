package com.fdp.datareport.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScrumAreaTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidScrumArea() {
        ScrumArea area = new ScrumArea(1L, "Platform", "Alice", "Alpha Team", "BOARD-123");

        Set<ConstraintViolation<ScrumArea>> violations = validator.validate(area);
        assertTrue(violations.isEmpty(), "Valid ScrumArea should have no violations");
    }

    @Test
    public void testMissingFields() {
        ScrumArea area = new ScrumArea(); // All null

        Set<ConstraintViolation<ScrumArea>> violations = validator.validate(area);

        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertEquals(4, violations.size(), "Should trigger all @NotNull validations");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("areaName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("scrumMaster")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("scrumTeam")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("boardId")));
    }

    @Test
    public void testPartialInvalidScrumArea() {
        ScrumArea area = new ScrumArea(1L, "Mobile", null, "Beta Team", null);

        Set<ConstraintViolation<ScrumArea>> violations = validator.validate(area);

        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertEquals(2, violations.size(), "Should catch 2 missing required fields");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("scrumMaster")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("boardId")));
    }
}
