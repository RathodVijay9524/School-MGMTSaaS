package com.vijay.User_Master.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * Examples:
 * - ISBN already exists
 * - Exam code already exists
 * - Subject code already exists
 * - Bus number already exists
 */
@Getter
@ResponseStatus(value = HttpStatus.CONFLICT) // 409
public class DuplicateResourceException extends RuntimeException {
    
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    
    public DuplicateResourceException(String resourceName, Object fieldValue) {
        super(String.format("%s already exists: '%s'", resourceName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = null;
        this.fieldValue = fieldValue;
    }
    
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public DuplicateResourceException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }
}

