package com.iqvia.quickfix.exception;

public class PasswordsDoNotMatchException extends RuntimeException {
    public PasswordsDoNotMatchException() {
        super("Die Passwörter stimmen nicht überein.");
    }
}
