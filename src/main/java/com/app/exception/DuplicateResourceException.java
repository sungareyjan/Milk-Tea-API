package com.app.exception;

// A runtime exception for duplicate resources like username, email, etc.
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
