package org.moltimate.moltimatebackend.alignment;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.alignment.requests.ActiveSiteAlignmentRequest;
import org.moltimate.moltimatebackend.alignment.requests.BackboneAlignmentRequest;
import org.moltimate.moltimatebackend.motif.MotifService;
import org.moltimate.moltimatebackend.protein.ProteinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AlignmentService provides a way to align the active sites and backbones of
 */
@Service
@Slf4j
public class AlignmentService {

    @Autowired
    private ProteinService proteinService;

    @Autowired
    private MotifService motifService;

    /**
     * Executes the ActiveSiteAlignmentRequest on protein active sites.
     *
     * @param alignmentRequest The alignment request JSON mapped to an object
     * @return AlignmentResponse which contains all alignments and their relevant data
     */
    public AlignmentResponse alignActiveSites(ActiveSiteAlignmentRequest alignmentRequest) {
        return alignActiveSites(
                proteinService.queryPdb(alignmentRequest.getPdbIds()),
                motifService.queryMotifs(alignmentRequest.getEcNumber())
        );
    }

    private AlignmentResponse alignActiveSites(List<Structure> sourceStructures, List<Structure> motifStructures) {
        // ... for each sourceStructure, call alignActiveSites() against all motifStructures

        return new AlignmentResponse(/* populate me with a list of Alignments */);
    }

    private Alignment alignActiveSites(Structure structure1, Structure structure2) {
        // ... logic to perform one active site alignment between two structures

        return new Alignment();
    }

    /**
     * Executes the BackboneAlignmentRequest on protein backbones.
     *
     * @param alignmentRequest The alignment request JSON mapped to an object
     * @return AlignmentResponse which contains all alignments and their relevant data
     */
    public AlignmentResponse alignBackbones(BackboneAlignmentRequest alignmentRequest) {
        return alignBackbones(
                proteinService.queryPdb(alignmentRequest.getSourcePdbIds()),
                proteinService.queryPdb(alignmentRequest.getCompareToPdbIds())
        );
    }

    private AlignmentResponse alignBackbones(List<Structure> sourceStructures, List<Structure> compareToStructures) {
        // ... for each sourceStructures, call alignBackbones() against all compareToStructures

        return new AlignmentResponse(/* populate me with a list of Alignments */);
    }

    private Alignment alignBackbones(Structure structure1, Structure structure2) {
        // ... logic to perform one backbone alignment between two structures

        return new Alignment();
    }
}
