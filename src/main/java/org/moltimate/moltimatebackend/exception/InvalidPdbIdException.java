package org.moltimate.moltimatebackend.exception;

import java.util.List;

public class InvalidPdbIdException extends RuntimeException {
    public InvalidPdbIdException(String pdbId) {
        super(String.format("Cannot find structure with PDB id: %s", pdbId));
    }

    public InvalidPdbIdException(List<String> pdbIds) {
        super(String.format("Could not find structures for the following PDB ids: %s", pdbIds));
    }
}
