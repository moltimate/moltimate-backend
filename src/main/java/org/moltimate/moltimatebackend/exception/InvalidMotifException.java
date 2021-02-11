package org.moltimate.moltimatebackend.exception;

import java.util.List;

public class InvalidMotifException extends RuntimeException {
    public InvalidMotifException(String pdbId) {
        super(String.format("Cannot find motif with PDB id: %s", pdbId));
    }

    public InvalidMotifException(List<String> pdbIds) {
        super(String.format("Could not find motifs for the following PDB ids: %s", pdbIds));
    }
}
