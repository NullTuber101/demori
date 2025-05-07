package com.fdp.datareport.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StatusTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidStatus() {
        Status status = new Status(1L, "In Progress", 50);

        Set<ConstraintViolation<Status>> violations = validator.validate(status);
        assertThat(violations).isEmpty();
    }

    @Test
    void testMissingFields() {
        Status status = new Status();

        Set<ConstraintViolation<Status>> violations = validator.validate(status);

        assertThat(violations).hasSizeGreaterThan(0);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("statusName"));
    }

    @Test
    void testInvalidPercentage() {
        Status status = new Status(2L, "Done", 120); // Invalid: > 100

        Set<ConstraintViolation<Status>> violations = validator.validate(status);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("percentage"));
    }
}
