package com.iqvia.quickfix.exception;

public class InvalidSupportUserException extends RuntimeException {
    public InvalidSupportUserException(Long id) {

        super("Der Benutzer with ID " + id + " hat nicht die Rolle SUPPORT");
    }
}
