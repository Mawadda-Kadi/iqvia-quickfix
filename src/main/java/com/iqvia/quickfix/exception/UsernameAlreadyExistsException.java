package com.iqvia.quickfix.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super(username + " existiert bereits");
    }
}

