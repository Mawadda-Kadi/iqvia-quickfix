package com.iqvia.quickfix.exception;

public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException(Long id) {
        super("Attachment mit ID " + id + " nicht gefunden");
    }
}
