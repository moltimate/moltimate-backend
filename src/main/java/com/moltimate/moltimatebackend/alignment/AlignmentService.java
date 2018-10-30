package com.moltimate.moltimatebackend.alignment;

import org.biojava.nbio.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlignmentService {

    private static final Logger log = LoggerFactory.getLogger(AlignmentService.class);

    public AlignmentResponse performActiveSiteAlignments(List<Structure> queryStructures, List<Structure> motifStructures) {
        // ... for each queryStructure, call performActiveSiteAlignment() against all motifStructures

        return new AlignmentResponse(/* populate me with a list of Alignments */);
    }

    public Alignment performActiveSiteAlignment(Structure structure1, Structure structure2) {
        // ... logic to perform one active site alignment between two structures

        return new Alignment();
    }
}
