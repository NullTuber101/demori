package com.fdp.datareport.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private Validator validator;
    private Role testRole;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        testRole = Role.builder()
                .id(1L)
                .roleName("EDITOR")
                .canApproveUsers(true)
                .canWrite(true)
                .canDelete(false)
                .build();
    }

    @Test
    void testValidUser() {
        User user = User.builder()
                .id(1L)
                .brid("test123")
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .role(testRole)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void testMissingRequiredFields() {
        User user = new User(); // all fields null

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSizeGreaterThan(0);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("brid"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void testLombokMethods() {
        User u1 = User.builder()
                .id(1L)
                .brid("test123")
                .email("test@example.com")
                .password("secret")
                .name("Test User")
                .role(testRole)
                .build();

        User u2 = User.builder()
                .id(1L)
                .brid("test123")
                .email("test@example.com")
                .password("secret")
                .name("Test User")
                .role(testRole)
                .build();

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
        assertThat(u1.toString()).contains("test123");
    }
}
