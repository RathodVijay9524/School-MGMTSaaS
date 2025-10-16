package com.vijay.User_Master.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a concurrent update conflict is detected (optimistic locking failure).
 * This happens when two users try to update the same record simultaneously.
 * The user should refresh and try again.
 */
@Getter
@ResponseStatus(value = HttpStatus.CONFLICT) // 409
public class ConcurrentUpdateException extends RuntimeException {
    
    private final String resourceName;
    private final Object resourceId;
    
    public ConcurrentUpdateException(String resourceName, Object resourceId) {
        super(String.format("%s with id '%s' was modified by another user. Please refresh and try again.", 
                resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
    
    public ConcurrentUpdateException(String message) {
        super(message);
        this.resourceName = null;
        this.resourceId = null;
    }
    
    public ConcurrentUpdateException(String message, Throwable cause) {
        super(message, cause);
        this.resourceName = null;
        this.resourceId = null;
    }
}

