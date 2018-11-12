package org.moltimate.moltimatebackend.validation.exceptions;

public class InvalidEcNumberException extends RuntimeException {
    public InvalidEcNumberException(String ecNumber) {
        super("EC number can only contain integers separated by periods, but received: " + ecNumber);
    }
}
