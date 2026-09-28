package com.iqvia.quickfix.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User mit ID " + id + " wurde nicht gefunden");
    }

    public UserNotFoundException(String username) {
        super("Benutzer mit dem Benutzernamen \"" + username + "\" wurde nicht gefunden");
    }
}
