package com.fdp.datareport.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AreaTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidArea() {
        Area area = new Area(1L, "Test Area", "Test Name", "test@example.com");

        Set<ConstraintViolation<Area>> violations = validator.validate(area);

        assertThat(violations).isEmpty();
    }

    @Test
    void testNullNameShouldFailValidation() {
        Area area = new Area(1L, null, "Test Name", "test@example.com");

        Set<ConstraintViolation<Area>> violations = validator.validate(area);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void testNullLeadNameShouldFailValidation() {
        Area area = new Area(1L, "Test Area", null, "test@example.com");

        Set<ConstraintViolation<Area>> violations = validator.validate(area);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("leadName"));
    }

    @Test
    void testNullLeadEmailShouldFailValidation() {
        Area area = new Area(1L, "Test Area", "Test Name", null);

        Set<ConstraintViolation<Area>> violations = validator.validate(area);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("leadEmail"));
    }

    @Test
    void testLombokGeneratedMethods() {
        Area area1 = new Area(1L, "Test Area", "Test Name", "test@example.com");
        Area area2 = new Area(1L, "Test Area", "Test Name", "test@example.com");

        assertThat(area1).isEqualTo(area2);
        assertThat(area1.hashCode()).isEqualTo(area2.hashCode());
        assertThat(area1.toString()).contains("Test Area");
    }
}
