package org.moltimate.moltimatebackend.validation.exceptions;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String errorMessage) {
        super(errorMessage);
    }
}
