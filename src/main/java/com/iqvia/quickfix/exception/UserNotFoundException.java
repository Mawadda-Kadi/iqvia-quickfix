package com.iqvia.quickfix.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User mit ID " + id + " nicht gefunden");
    }
}
