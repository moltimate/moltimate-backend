package org.moltimate.moltimatebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.exception.InvalidPdbIdException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PdbQueryResponse represents the collection of structures and failed ids from a series of queries
 * to the PDB
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdbQueryResponse {
    private List<Structure> structures = new ArrayList<>();
    private List<String> foundPdbIds = new ArrayList<>();
    private List<String> failedPdbIds = new ArrayList<>();

    public PdbQueryResponse generatePdbQueryResponse(List<String> pdbIds, List<Structure> structures) {
        if (structures.size() == 0 && pdbIds.size() != 0) {
            throw new InvalidPdbIdException(pdbIds);
        }

        this.structures = structures;
        foundPdbIds = structures.stream()
                .map(Structure::getPDBCode)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        if (pdbIds.size() == foundPdbIds.size()) {
            return this;
        }

        failedPdbIds = pdbIds.stream()
                .map(String::toUpperCase)
                .filter(pdbId -> !foundPdbIds.contains(pdbId))
                .collect(Collectors.toList());
        return this;
    }
}
