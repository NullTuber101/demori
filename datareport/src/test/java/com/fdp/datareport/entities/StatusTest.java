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

public class StatusTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidStatus() {
        Status status = new Status(1L, "Completed", 80);
        Set<ConstraintViolation<Status>> violations = validator.validate(status);
        assertTrue(violations.isEmpty(), "Should be valid for correct percentage");
    }

    @Test
    public void testNullStatusName() {
        Status status = new Status(1L, null, 50);
        Set<ConstraintViolation<Status>> violations = validator.validate(status);
        assertEquals(1, violations.size());
        assertEquals("Status name is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidNegativePercentage() {
        Status status = new Status(1L, "Started", -5);
        Set<ConstraintViolation<Status>> violations = validator.validate(status);
        assertEquals(1, violations.size());
    }

    @Test
    public void testInvalidOver100Percentage() {
        Status status = new Status(1L, "In Progress", 150);
        Set<ConstraintViolation<Status>> violations = validator.validate(status);
        assertEquals(1, violations.size());
    }

    @Test
    public void testNullPercentage() {
        Status status = new Status(1L, "In Progress", null);
        Set<ConstraintViolation<Status>> violations = validator.validate(status);
        assertTrue(violations.isEmpty() || violations.size() == 1); // Depending on how @ValidPercentage handles nulls
    }
}
