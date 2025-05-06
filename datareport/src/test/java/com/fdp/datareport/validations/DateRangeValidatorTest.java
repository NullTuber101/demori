package com.fdp.datareport.validation.validators;

import com.fdp.datareport.validation.annotations.ValidDateRange;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeValidatorTest {

    private DateRangeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DateRangeValidator();

        validator.initialize(new ValidDateRange() {
            @Override
            public String startField() {
                return "start";
            }

            @Override
            public String endField() {
                return "end";
            }

            @Override
            public String message() {
                return "Start date must be before end date";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return ValidDateRange.class;
            }
        });
    }

    // Dummy test class with 'start' and 'end' fields
    static class DummyBean {
        Date start;
        Date end;

        DummyBean(Date start, Date end) {
            this.start = start;
            this.end = end;
        }
    }

    @Test
    void testValidDateRange_shouldReturnTrue() {
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis() + 100000);
        DummyBean bean = new DummyBean(start, end);

        assertTrue(validator.isValid(bean, null));
    }

    @Test
    void testInvalidDateRange_shouldReturnFalse() {
        Date start = new Date(System.currentTimeMillis() + 100000);
        Date end = new Date(System.currentTimeMillis());
        DummyBean bean = new DummyBean(start, end);

        assertFalse(validator.isValid(bean, null));
    }

    @Test
    void testNullDates_shouldReturnTrue() {
        DummyBean bean = new DummyBean(null, null);
        assertTrue(validator.isValid(bean, null));
    }

    @Test
    void testStartNull_shouldReturnTrue() {
        DummyBean bean = new DummyBean(null, new Date());
        assertTrue(validator.isValid(bean, null));
    }

    @Test
    void testEndNull_shouldReturnTrue() {
        DummyBean bean = new DummyBean(new Date(), null);
        assertTrue(validator.isValid(bean, null));
    }
}
