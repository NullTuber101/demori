package com.fdp.datareport.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationExceptions_shouldReturnBadRequestWithMessages() throws NoSuchMethodException {
        // Create a dummy binding result with errors
        Object target = new Object();
        String objectName = "object";
        BindingResult bindingResult = new BeanPropertyBindingResult(target, objectName);
        bindingResult.addError(new FieldError(objectName, "email", "Email is required"));
        bindingResult.addError(new FieldError(objectName, "brid", "BRID is required"));

        // Wrap in MethodArgumentNotValidException
        Method dummyMethod = DummyController.class.getMethod("dummyMethod");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // Invoke the handler
        ResponseEntity<Object> response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(GlobalExceptionHandler.ErrorResponse.class);

        GlobalExceptionHandler.ErrorResponse errorResponse = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertThat(errorResponse.getError()).isEqualTo("Bad Request");
        assertThat(errorResponse.getMessage()).contains("email: Email is required", "brid: BRID is required");
    }

    @Test
    void handleDataIntegrityViolation_shouldReturnConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Unique index or primary key violation");

        ResponseEntity<Object> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isInstanceOf(GlobalExceptionHandler.ErrorResponse.class);

        GlobalExceptionHandler.ErrorResponse errorResponse = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertThat(errorResponse.getError()).isEqualTo("Conflict");
        assertThat(errorResponse.getMessage()).contains("Duplicate key constraint violated: BRID or Email may already exist.");
    }

    // Dummy controller to simulate MethodArgumentNotValidException constructor usage
    static class DummyController {
        public void dummyMethod() {}
    }
}
