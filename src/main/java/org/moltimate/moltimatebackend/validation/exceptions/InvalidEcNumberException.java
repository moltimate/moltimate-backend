package org.moltimate.moltimatebackend.validation.exceptions;

public class InvalidEcNumberException extends RuntimeException {
    public InvalidEcNumberException(String ecNumber) {
        super(String.format("EC number can only contain integers separated by periods, but received: %s", ecNumber));
    }
}
