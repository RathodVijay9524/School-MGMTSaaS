package com.vijay.User_Master.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Simplified exception for entity not found scenarios.
 * Extends ResourceNotFoundException but provides simpler constructors.
 */
@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends ResourceNotFoundException {
    
    public EntityNotFoundException(String entityName, Object id) {
        super(entityName, "id", id);
    }
    
    public EntityNotFoundException(String entityName, String fieldName, Object fieldValue) {
        super(entityName, fieldName, fieldValue);
    }
    
    public EntityNotFoundException(String message) {
        super("Entity", "identifier", message);
    }
}

