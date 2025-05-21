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
                new Date(System.currentTimeMillis() - 86400000L),
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
        Sprint sprint = new Sprint(); // all null

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);

        assertThat(violations).hasSize(5);
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
                "Sprint Fail",
                new Date(System.currentTimeMillis() + 86400000L), // future start
                new Date(System.currentTimeMillis()),              // now
                "JIRA-FAIL",
                "Bad range",
                "Tester",
                testStatus,
                testProject
        );

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals("Start date must not be after end date"));
    }


    @Test
    void testSprintNameExceedsMaxLength() {
        Sprint sprint = new Sprint();
        sprint.setSprintName("A".repeat(51)); // Exceeds @Size max = 50
        sprint.setSprintStartDate(new Date());
        sprint.setSprintEndDate(new Date(System.currentTimeMillis() + 86400000));
        sprint.setSprintJira("JIRA-OVER");
        sprint.setAssignedTo("Test User");
        sprint.setProject(testProject);
        sprint.setSprintFor(testStatus);

        Set<ConstraintViolation<Sprint>> violations = validator.validate(sprint);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("sprintName") &&
                        v.getMessage().contains("must not exceed 50 characters"));
    }
}
