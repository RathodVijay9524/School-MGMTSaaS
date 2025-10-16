package com.vijay.User_Master.exceptions;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.exceptions.exception.InvalidTokenException;
import com.vijay.User_Master.exceptions.exception.TokenExpiredException;
import com.vijay.User_Master.exceptions.exception.TokenNotFoundException;
import com.vijay.User_Master.exceptions.exception.TokenRefreshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mapping.PropertyReferenceException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ============= TOKEN EXCEPTIONS ============= //
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<?> handleTokenNotFoundException(TokenNotFoundException ex) {
        logger.warn("Token not found: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Authentication token not found",
                HttpStatus.NOT_FOUND
        );
    }

    // ===== Token Refresh Specific Exceptions =====
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<?> handleTokenRefreshException(TokenRefreshException ex) {
        logger.error("Token refresh failed: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Token refresh failed. Please login again",
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> handleTokenExpiredException(TokenExpiredException ex) {
        logger.warn("Token expired: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Session expired. Please login again",
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex) {
        logger.warn("Invalid token: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Invalid authentication token",
                HttpStatus.FORBIDDEN
        );
    }

    // ============= EXISTING EXCEPTIONS ============= //
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error("Bad credentials: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        logger.error("User exists: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource missing: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ExceptionUtil.createErrorResponse(
                errors,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        logger.error("System error: {}", ex.getMessage(), ex);
        return ExceptionUtil.createErrorResponseMessage(
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        logger.error("Null pointer: {}", ex.getMessage(), ex);
        return ExceptionUtil.createErrorResponseMessage(
                "A system error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<?> handleBadApiRequest(BadApiRequestException ex) {
        logger.error("Bad API request: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    // ============= CUSTOM BUSINESS EXCEPTIONS ============= //
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<?> handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        logger.warn("Business rule violation: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResource(DuplicateResourceException ex) {
        logger.warn("Duplicate resource: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ConcurrentUpdateException.class)
    public ResponseEntity<?> handleConcurrentUpdate(ConcurrentUpdateException ex) {
        logger.warn("Concurrent update conflict: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException ex) {
        logger.warn("Entity not found: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    // ============= DATABASE EXCEPTIONS ============= //
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<?> handleOptimisticLock(OptimisticLockException ex) {
        logger.warn("Optimistic lock exception: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "The record was modified by another user. Please refresh and try again.",
                HttpStatus.CONFLICT
        );
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Data integrity violation. Please check your input data.",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResultDataAccess(EmptyResultDataAccessException ex) {
        logger.warn("No data found: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "No data found for the requested resource",
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElement(NoSuchElementException ex) {
        logger.warn("Element not found: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Requested resource not found",
                HttpStatus.NOT_FOUND
        );
    }

    // ============= VALIDATION EXCEPTIONS ============= //
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.error("Invalid JSON format: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Invalid JSON format. Please check your request body.",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException ex) {
        logger.error("Binding error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ExceptionUtil.createErrorResponse(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParameter(MissingServletRequestParameterException ex) {
        logger.error("Missing parameter: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Missing required parameter: " + ex.getParameterName(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Type mismatch: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Invalid parameter type for: " + ex.getName(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<?> handlePropertyReference(PropertyReferenceException ex) {
        logger.error("Property reference error: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Invalid property reference: " + ex.getPropertyName(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.error("Method not supported: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "HTTP method not supported: " + ex.getMethod(),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }
}


