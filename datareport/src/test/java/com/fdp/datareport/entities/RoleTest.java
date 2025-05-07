package com.fdp.datareport.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRole() {
        Role role = Role.builder()
                .id(1L)
                .roleName("EDITOR")
                .canApproveUsers(false)
                .canWrite(true)
                .canDelete(false)
                .build();

        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        assertThat(violations).isEmpty();
    }

    @Test
    void testMissingRoleName() {
        Role role = Role.builder()
                .id(1L)
                .roleName(null) // This should trigger the NotNull constraint
                .canApproveUsers(true)
                .canWrite(true)
                .canDelete(false)
                .build();

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("roleName") &&
                        v.getMessage().toLowerCase().contains("role name"));
    }



    @Test
    void testLombokMethods() {
        Role r1 = Role.builder()
                .id(1L)
                .roleName("VIEWER")
                .canApproveUsers(false)
                .canWrite(false)
                .canDelete(false)
                .build();

        Role r2 = Role.builder()
                .id(1L)
                .roleName("VIEWER")
                .canApproveUsers(false)
                .canWrite(false)
                .canDelete(false)
                .build();

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        assertThat(r1.toString()).contains("VIEWER");
    }
}
