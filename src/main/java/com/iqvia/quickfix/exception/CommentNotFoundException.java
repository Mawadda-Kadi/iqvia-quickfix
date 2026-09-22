package com.iqvia.quickfix.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long id) {
        super("Comment mit ID " + id + " nicht gefunden");
    }
}
