package com.fdp.datareport.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SprintTest {

    private Validator validator;
    private Status dummyStatus;
    private Project dummyProject;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        dummyStatus = new Status(1L, "Done", 100);
        dummyProject = new Project(
                1L,
                "Project X",
                "Test project description",
                "Dev Name",
                "PRJ-100",
                new Area(1L, "Area 1", "Lead A", "lead@example.com"),
                dummyStatus,
                getDate(2024, 1, 1),
                getDate(2024, 12, 31)
        );
    }

    private Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.getTime();
    }

    @Test
    public void testValidSprint() {
        Sprint sprint = new Sprint(
                1L,
                "Sprint 1",
                getDate(2024, 5, 1),
                getDate(2024, 5, 15),
                "SPRINT-101",
                "Initial sprint",
                "Alice",
                dummyStatus,
                dummyProject
        );

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);
        assertTrue(violations.isEmpty(), "Valid sprint should have no violations");
    }

    @Test
    public void testMissingFields() {
        Sprint sprint = new Sprint();

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertEquals(5, violations.size(), "Should catch all required @NotNull fields");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sprintName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sprintStartDate")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sprintEndDate")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sprintJira")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("assignedTo")));
    }

    @Test
    public void testInvalidDateRange() {
        Sprint sprint = new Sprint(
                1L,
                "Sprint 2",
                getDate(2024, 5, 15), // start
                getDate(2024, 5, 1),  // end (invalid)
                "SPRINT-102",
                "Sprint with invalid dates",
                "Bob",
                dummyStatus,
                dummyProject
        );

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertEquals(1, violations.size(), "Should catch 1 invalid date range");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Start date must not be after end date")));
    }

    @Test
    public void testNullDatesHandledByNotNullOnly() {
        Sprint sprint = new Sprint(
                1L,
                "Sprint 3",
                null,
                null,
                "SPRINT-103",
                "Sprint with null dates",
                "Charlie",
                dummyStatus,
                dummyProject
        );

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertEquals(2, violations.size(), "Should catch null sprintStartDate and sprintEndDate");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sprintStartDate")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sprintEndDate")));
    }
}
