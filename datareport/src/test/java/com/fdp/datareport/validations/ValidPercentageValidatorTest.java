package com.fdp.datareport.validations;

import com.fdp.datareport.validation.validators.ValidPercentageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidPercentageValidatorTest {

    private ValidPercentageValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidPercentageValidator();
        validator.initialize(null); // No config needed
    }

    @Test
    void testValidPercentageValues_shouldReturnTrue() {
        assertTrue(validator.isValid(0, null));
        assertTrue(validator.isValid(50, null));
        assertTrue(validator.isValid(100, null));
    }

    @Test
    void testInvalidPercentageValues_shouldReturnFalse() {
        assertFalse(validator.isValid(-1, null));
        assertFalse(validator.isValid(101, null));
        assertFalse(validator.isValid(1000, null));
    }

    @Test
    void testNullValue_shouldReturnFalse() {
        assertFalse(validator.isValid(null, null));
    }
}
