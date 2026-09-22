package com.iqvia.quickfix.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {

        super(email + " existiert bereits.");
    }
}
