package org.moltimate.moltimatebackend.exception;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String errorMessage) {
        super(errorMessage);
    }
}
