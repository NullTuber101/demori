package com.fdp.datareport.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SprintTest {

    private Validator validator;

    private Project testProject;
    private Status testStatus;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        testProject = new Project();
        testProject.setId(1L);
        testProject.setProjectName("Demo Project");
        testProject.setStartDate(new Date());
        testProject.setEndDate(new Date());

        testStatus = new Status(1L, "In Progress", 50);
    }

    @Test
    void testValidSprint() {
        Sprint sprint = new Sprint(
                1L,
                "Sprint 1",
                new Date(System.currentTimeMillis() - 86400000L), // yesterday
                new Date(),
                "JIRA-SPRINT-1",
                "Initial sprint",
                "Test User",
                testStatus,
                testProject
        );

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);
        assertThat(violations).isEmpty();
    }

    @Test
    void testMissingRequiredFields() {
        Sprint sprint = new Sprint();

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);

        assertThat(violations).hasSizeGreaterThan(0);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("sprintName"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("sprintStartDate"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("sprintEndDate"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("sprintJira"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("assignedTo"));
    }

    @Test
    void testInvalidDateRange() {
        Sprint sprint = new Sprint(
                2L,
                "Invalid Sprint",
                new Date(System.currentTimeMillis() + 100000), // future start
                new Date(),                                    // past end
                "JIRA-BAD",
                "Bad date range",
                "Test User",
                testStatus,
                testProject
        );

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().toLowerCase().contains("start date must")
                        || v.getMessage().toLowerCase().contains("invalid date range"));
    }
}
