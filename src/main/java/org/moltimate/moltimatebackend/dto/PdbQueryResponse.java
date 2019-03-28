package org.moltimate.moltimatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Structure;

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
    List<Structure> structures;
    List<String> foundPdbIds;
    List<String> failedPdbIds = new ArrayList<>();

    public PdbQueryResponse generatePdbQueryResponse(List<String> pdbIds, List<Structure> structures) {
        this.structures = structures;
        foundPdbIds = structures.stream()
                .map(Structure::getPDBCode)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        if (pdbIds.size() == foundPdbIds.size()) {
            return this;
        }

        failedPdbIds = pdbIds.stream()
                .filter(pdbId -> !foundPdbIds.contains(pdbId))
                .collect(Collectors.toList());
        return this;
    }
}
