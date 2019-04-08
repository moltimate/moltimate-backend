package org.moltimate.moltimatebackend.dto.MotifTesting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.StructureUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of Alignments and useful data around them.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotifTestResponse {

    private String motifPdbId;
    private String motifEcNumber;
    private List<Residue> activeSiteResidues;

    List<SuccessfulAlignment> alignments; // Alignments that found a match
    List<FailedAlignment> failedAlignments; // Alignments that did not find a match
    List<String> failedPdbIds; // PDB ids that failed to be processed

    public MotifTestResponse(Motif motif) {
        this.motifPdbId = motif.getPdbId();
        this.motifEcNumber = motif.getEcNumber();
        this.activeSiteResidues = motif.getActiveSiteResidues();

        this.alignments = new ArrayList<>();
        this.failedAlignments = new ArrayList<>();
        this.failedPdbIds = new ArrayList<>();
    }

    @Data
    private class SuccessfulAlignment {
        private String queryPdbId;
        private String queryEcNumber;
        private double rmsd;
        private int levenshtein;
        private List<Residue> alignedResidues;

        private SuccessfulAlignment(Structure queryStructure, Alignment alignment) {
            this.queryPdbId = queryStructure.getPDBCode();
            this.queryEcNumber = StructureUtils.ecNumber(queryStructure);
            this.rmsd = alignment.getRmsd();
            this.levenshtein = alignment.getMaxDistance(); // Todo fix when alignment is modified to 1 levenshtein distance
            this.alignedResidues = alignment.getAlignedResidues();
        }
    }

    public void addSuccessfulEntry(Structure queryStructure, Alignment alignment) {
        this.alignments.add(new SuccessfulAlignment(queryStructure, alignment));
    }

    @Data
    @AllArgsConstructor
    private class FailedAlignment {
        private String queryPdbId;
        private String queryEcNumber;
    }

    public void addFailedEntry(String queryPdbId, String queryEcNumber) {
        this.failedAlignments.add(new FailedAlignment(queryPdbId, queryEcNumber));
    }

    public void addFailedPdbIds(List<String> pdbIds) {
        this.failedPdbIds.addAll(pdbIds);
    }

    public void addFailedPdbId(String pdbId) {
        this.failedPdbIds.add(pdbId);
    }
}
