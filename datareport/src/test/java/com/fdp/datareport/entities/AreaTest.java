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

public class AreaTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidArea() {
        Area area = new Area(1L, "Development", "John Doe", "john.doe@example.com");

        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        assertTrue(violations.isEmpty(), "No violations should occur for a valid Area");
    }

    @Test
    public void testNullName() {
        Area area = new Area(1L, null, "John Doe", "john.doe@example.com");

        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        assertEquals(1, violations.size());
        assertEquals("Name is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testNullLeadName() {
        Area area = new Area(1L, "Development", null, "john.doe@example.com");

        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        assertEquals(1, violations.size());
        assertEquals("Lead Name is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testNullLeadEmail() {
        Area area = new Area(1L, "Development", "John Doe", null);

        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        assertEquals(1, violations.size());
        assertEquals("Lead Email is required", violations.iterator().next().getMessage());
    }
}
