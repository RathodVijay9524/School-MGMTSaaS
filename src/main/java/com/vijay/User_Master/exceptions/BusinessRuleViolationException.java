package com.vijay.User_Master.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a business rule is violated.
 * Examples:
 * - Cannot delete assignment with existing submissions
 * - Cannot reduce totalCopies below issuedCopies
 * - Cannot mark attendance for future date
 * - Payment amount exceeds balance
 */
@Getter
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY) // 422
public class BusinessRuleViolationException extends RuntimeException {
    
    private final String rule;
    private final String details;
    
    public BusinessRuleViolationException(String message) {
        super(message);
        this.rule = null;
        this.details = null;
    }
    
    public BusinessRuleViolationException(String rule, String details) {
        super(String.format("Business rule violation: %s - %s", rule, details));
        this.rule = rule;
        this.details = details;
    }
    
    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
        this.rule = null;
        this.details = null;
    }
}

