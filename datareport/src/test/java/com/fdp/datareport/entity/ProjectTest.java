package com.fdp.datareport.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

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
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenRequiredFieldsMissing() {
        Project project = new Project(); // all fields null

        Set<ConstraintViolation<Project>> violations = validator.validate(project);

        assertThat(violations).hasSize(5);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("projectName") &&
                        v.getMessage().equals("Project Name is required"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("developer") &&
                        v.getMessage().equals("Developer Name is required"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("jira") &&
                        v.getMessage().equals("Jira is required"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("startDate") &&
                        v.getMessage().equals("Start Date is required"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("endDate") &&
                        v.getMessage().equals("End Date is required"));
    }

    @Test
    void shouldFailValidationForInvalidDateRange() {
        Project project = new Project();
        project.setProjectName("Invalid Dates");
        project.setDeveloper("Dev");
        project.setJira("JIRA-456");
        project.setStartDate(new Date(System.currentTimeMillis() + 86400000)); // future
        project.setEndDate(new Date(System.currentTimeMillis())); // now

        Set<ConstraintViolation<Project>> violations = validator.validate(project);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().toLowerCase().contains("end date") ||
                        v.getMessage().toLowerCase().contains("valid date"));
    }

    @Test
    void shouldTriggerOnDeleteMethodSafely() {
        Project project = new Project();
        assertDoesNotThrow(project::onDelete, "onDelete should not throw any exception");
    }

    @Test
    void shouldFailWhenProjectNameExceedsMaxLength() {
        Project project = new Project();
        project.setProjectName("A".repeat(31)); // exceeds @Size max=30
        project.setDeveloper("Dev");
        project.setJira("JIRA-789");
        project.setStartDate(new Date());
        project.setEndDate(new Date());

        Set<ConstraintViolation<Project>> violations = validator.validate(project);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("projectName") &&
                        v.getMessage().contains("must not exceed 30 characters"));
    }
}
