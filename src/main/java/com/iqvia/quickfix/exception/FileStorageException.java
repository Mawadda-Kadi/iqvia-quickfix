package com.iqvia.quickfix.exception;

public class FileStorageException extends RuntimeException {

    public FileStorageException(Throwable cause) {
        super("Die Datei konnte nicht gespeichert werden.", cause);
    }
}
