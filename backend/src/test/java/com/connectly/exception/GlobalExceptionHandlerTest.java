package com.connectly.exception;

import com.connectly.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();

        request = new MockHttpServletRequest();
        request.setRequestURI("/api/users/register");
    }

    @Test
    void handleDuplicateEmail_shouldReturnConflict() {

        DuplicateEmailException exception =
                new DuplicateEmailException("Email already exists");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleDuplicateEmail(exception, request);

        assertErrorResponse(
                response,
                HttpStatus.CONFLICT,
                "Email already exists"
        );

        assertNull(response.getBody().getValidationErrors());
    }

    @Test
    void handleDuplicateUsername_shouldReturnConflict() {

        DuplicateUsernameException exception =
                new DuplicateUsernameException("Username already exists");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleDuplicateUsername(exception, request);

        assertErrorResponse(
                response,
                HttpStatus.CONFLICT,
                "Username already exists"
        );

        assertNull(response.getBody().getValidationErrors());
    }

    @Test
    void handleUserNotFound_shouldReturnNotFound() {

        UserNotFoundException exception =
                new UserNotFoundException("User not found");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleUserNotFound(exception, request);

        assertErrorResponse(
                response,
                HttpStatus.NOT_FOUND,
                "User not found"
        );
    }

    @Test
    void handleValidationErrors_shouldReturnFieldErrors()
            throws NoSuchMethodException {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError(
                        "request",
                        "username",
                        "Username is required"
                )
        );

        bindingResult.addError(
                new FieldError(
                        "request",
                        "email",
                        "Email must be valid"
                )
        );

        Method method = getClass().getDeclaredMethod(
                "sampleMethod",
                String.class
        );

        MethodParameter methodParameter =
                new MethodParameter(method, 0);

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        methodParameter,
                        bindingResult
                );

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleValidationErrors(
                        exception,
                        request
                );

        assertErrorResponse(
                response,
                HttpStatus.BAD_REQUEST,
                "Request validation failed"
        );

        assertEquals(
                Map.of(
                        "username", "Username is required",
                        "email", "Email must be valid"
                ),
                response.getBody().getValidationErrors()
        );
    }

    @Test
    void handleValidationErrors_shouldUseDefaultMessageWhenNull()
            throws NoSuchMethodException {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError(
                        "request",
                        "username",
                        null
                )
        );

        Method method = getClass().getDeclaredMethod(
                "sampleMethod",
                String.class
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        new MethodParameter(method, 0),
                        bindingResult
                );

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleValidationErrors(
                        exception,
                        request
                );

        assertEquals(
                "Invalid value",
                response.getBody()
                        .getValidationErrors()
                        .get("username")
        );
    }

    @Test
    void handleValidationErrors_shouldKeepFirstErrorForDuplicateField()
            throws NoSuchMethodException {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError(
                        "request",
                        "username",
                        "First error"
                )
        );

        bindingResult.addError(
                new FieldError(
                        "request",
                        "username",
                        "Second error"
                )
        );

        Method method = getClass().getDeclaredMethod(
                "sampleMethod",
                String.class
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        new MethodParameter(method, 0),
                        bindingResult
                );

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleValidationErrors(
                        exception,
                        request
                );

        assertEquals(
                "First error",
                response.getBody()
                        .getValidationErrors()
                        .get("username")
        );

        assertEquals(
                1,
                response.getBody().getValidationErrors().size()
        );
    }

    @Test
    void handleConstraintViolation_shouldReturnBadRequest() {

        ConstraintViolationException exception =
                new ConstraintViolationException(
                        "Invalid request parameter",
                        Set.of()
                );

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleConstraintViolation(
                        exception,
                        request
                );

        assertErrorResponse(
                response,
                HttpStatus.BAD_REQUEST,
                "Request validation failed"
        );

        assertNull(response.getBody().getValidationErrors());
    }

    @Test
    void handleMalformedRequest_shouldReturnBadRequest() {

        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "Malformed JSON",
                        (org.springframework.http.HttpInputMessage) null
                );

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleMalformedRequest(
                        exception,
                        request
                );

        assertErrorResponse(
                response,
                HttpStatus.BAD_REQUEST,
                "Request body is missing or contains invalid JSON"
        );
    }

    @Test
    void handleUnexpectedException_shouldReturnInternalServerError() {

        Exception exception =
                new IllegalStateException("Unexpected internal error");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleUnexpectedException(
                        exception,
                        request
                );

        assertErrorResponse(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    private void assertErrorResponse(
            ResponseEntity<ErrorResponse> response,
            HttpStatus expectedStatus,
            String expectedMessage
    ) {

        assertEquals(expectedStatus, response.getStatusCode());

        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertNotNull(body.getTimestamp());
        assertEquals(expectedStatus.value(), body.getStatus());
        assertEquals(expectedStatus.getReasonPhrase(), body.getError());
        assertEquals(expectedMessage, body.getMessage());
        assertEquals("/api/users/register", body.getPath());
    }

    private void sampleMethod(String value) {
        // Used to construct MethodParameter for validation tests.
    }
}
