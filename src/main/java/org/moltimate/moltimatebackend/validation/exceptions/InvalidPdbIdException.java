package org.moltimate.moltimatebackend.validation.exceptions;

import java.util.List;

public class InvalidPdbIdException extends RuntimeException {
    public InvalidPdbIdException(String pdbId) {
        super("Cannot find structure with PDB id: " + pdbId);
    }

    public InvalidPdbIdException(List<String> pdbIds) {
        super("Could not find structures for the following PDB ids:" + pdbIds);
    }
}
