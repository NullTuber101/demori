package com.fdp.datareport.entities;

import com.fdp.datareport.validation.annotations.ValidDateRange;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProjectEntityTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidProject() {
        Project project = new Project();
        project.setProjectName("Test Project");
        project.setDescription("This is a test.");
        project.setDeveloper("Jane Developer");
        project.setJira("JIRA-123");
        project.setStartDate(new Date(System.currentTimeMillis() - 86400000)); // yesterday
        project.setEndDate(new Date()); // today
        project.setArea(new Area()); // stub
        project.setStatus(new Status()); // stub

        Set<ConstraintViolation<Project>> violations = validator.validate(project);
        assertTrue(violations.isEmpty(), "Project should be valid");
    }

    @Test
    void shouldFailWhenRequiredFieldsMissing() {
        Project project = new Project(); // all fields null

        Set<ConstraintViolation<Project>> violations = validator.validate(project);
        assertFalse(violations.isEmpty(), "Should have violations");
        assertEquals(5, violations.size(), "Expected 5 @NotNull violations (projectName, developer, jira, startDate, endDate)");
    }

    @Test
    void shouldFailValidationForInvalidDateRange() {
        Project project = new Project();
        project.setProjectName("Date Test");
        project.setDeveloper("Dev");
        project.setJira("JIRA-456");
        project.setStartDate(new Date(System.currentTimeMillis() + 86400000)); // tomorrow
        project.setEndDate(new Date()); // today

        Set<ConstraintViolation<Project>> violations = validator.validate(project);
        assertFalse(violations.isEmpty(), "Expected validation failure for invalid date range");

        boolean hasDateRangeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().toLowerCase().contains("end date") || v.getMessage().toLowerCase().contains("valid date"));

        assertTrue(hasDateRangeViolation, "Expected a date range violation message");
    }

    @Test
    void onDeleteShouldNotThrow() {
        Project project = new Project();
        assertDoesNotThrow(project::onDelete, "onDelete should not throw any exception");
    }
}
