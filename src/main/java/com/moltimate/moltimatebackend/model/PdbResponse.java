package com.moltimate.moltimatebackend.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileReader;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PdbResponse implements Serializable {
    private static final PDBFileReader pdbFileReader = new PDBFileReader();

    List<Structure> pdbQueryStructures;

    public PdbResponse(PdbQuery pdbQuery) {
        pdbQueryStructures = pdbQuery.getPdbIds()
                                     .stream()
                                     .map(pdbId -> {
                                         try {
                                             return pdbFileReader.getStructureById(pdbId);
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                             throw new RuntimeException("Cannot find structure " + pdbId);
                                         }
                                     })
                                     .collect(Collectors.toList());
    }

    @JsonValue
    public String toString() {
        return pdbQueryStructures.stream()
                                 .map(Structure::toString)
                                 .reduce("", String::concat);
    }
}
