package com.fdp.datareport.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class VelocityTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testMissingRequiredFields() {
        Velocity velocity = new Velocity();
        velocity.setSprintName(""); // NotBlank
        velocity.setVelocity(null); // NotNull
        velocity.setSprintEndDate(null); // NotNull
        velocity.setScrumArea(null); // NotNull

        Set<ConstraintViolation<Velocity>> violations = validator.validate(velocity);

        assertThat(violations).hasSize(4);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("sprintName") &&
                        v.getMessage().equals("Sprint Name is required"));

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("velocity") &&
                        v.getMessage().equals("Velocity is required"));

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("sprintEndDate") &&
                        v.getMessage().equals("Sprint End Date is required"));

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("scrumArea") &&
                        v.getMessage().equals("Scrum Area is required"));
    }

    @Test
    void testValidVelocityObjectWithSettersAndGetters() {
        ScrumArea scrumArea = new ScrumArea();
        scrumArea.setId(1L);
        scrumArea.setAreaName("Backend Team");
        scrumArea.setScrumMaster("Alice");
        scrumArea.setScrumTeam("Team Alpha");
        scrumArea.setBoardId("BOARD-001");

        Velocity velocity = new Velocity();
        velocity.setSprintName("Sprint 10");
        velocity.setVelocity(15.5f);
        velocity.setSprintEndDate(LocalDate.now());
        velocity.setScrumArea(scrumArea);

        Set<ConstraintViolation<Velocity>> violations = validator.validate(velocity);
        assertThat(violations).isEmpty();

        // Access all fields for getter/setter coverage
        assertThat(velocity.getSprintName()).isEqualTo("Sprint 10");
        assertThat(velocity.getVelocity()).isEqualTo(15.5f);
        assertThat(velocity.getSprintEndDate()).isNotNull();
        assertThat(velocity.getScrumArea()).isEqualTo(scrumArea);
    }

    @Test
    void testAllArgsConstructor() {
        ScrumArea area = new ScrumArea(2L, "DevOps", "John", "Infra", "BOARD-XYZ");
        Velocity velocity = new Velocity(10L, "Sprint Final", 10f, LocalDate.now(), area);

        assertThat(velocity.getId()).isEqualTo(10L);
        assertThat(velocity.getSprintName()).isEqualTo("Sprint Final");
        assertThat(velocity.getVelocity()).isEqualTo(10f);
        assertThat(velocity.getSprintEndDate()).isNotNull();
        assertThat(velocity.getScrumArea()).isEqualTo(area);
    }
}
