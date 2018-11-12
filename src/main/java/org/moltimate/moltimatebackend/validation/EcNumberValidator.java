package org.moltimate.moltimatebackend.validation;

import org.moltimate.moltimatebackend.validation.exceptions.InvalidEcNumberException;

public class EcNumberValidator {

    public static final String VALIDATION_REGEX = "\\d+(\\.\\d+)+";

    /**
     * Validates an EC number.
     *
     * @param ecNumber EC number to validate
     * @throws InvalidEcNumberException if the EC number is not integers separated by periods
     */
    public static void validate(String ecNumber) throws InvalidEcNumberException {
        if (!ecNumber.matches(VALIDATION_REGEX)) {
            throw new InvalidEcNumberException(ecNumber);
        }
    }
}
