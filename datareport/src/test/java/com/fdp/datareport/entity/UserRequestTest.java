package com.fdp.datareport.entity;

import com.fdp.datareport.enums.RequestStatus;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUserRequest() {
        UserRequest request = UserRequest.builder()
                .brid("test01")
                .email("test01@example.com")
                .password("securepass")
                .name("Test User")
                .status(RequestStatus.PENDING)
                .rejectionReason(null)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testMissingRequiredFields() {
        UserRequest request = new UserRequest();
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSizeGreaterThan(0);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("brid"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"))
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void testLombokMethods() {
        UserRequest r1 = UserRequest.builder()
                .id(1L)
                .brid("b123")
                .email("a@a.com")
                .password("x")
                .name("X")
                .status(RequestStatus.PENDING)
                .build();

        UserRequest r2 = UserRequest.builder()
                .id(1L)
                .brid("b123")
                .email("a@a.com")
                .password("x")
                .name("X")
                .status(RequestStatus.PENDING)
                .build();

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        assertThat(r1.toString()).contains("b123");
    }
}
