package org.moltimate.moltimatebackend.dto.Alignment;

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
public class MotifAlignmentResponse {

    private String pdbId;
    private String ecNumber;
    private List<Residue> activeSiteResidues;

    private List<SuccessfulAlignment> alignments; // Alignments that found a match
    private List<FailedAlignment> failedAlignments; // Alignments that did not find a match
    private List<String> failedPdbIds; // PDB ids that failed to be processed

    public MotifAlignmentResponse(Motif motif) {
        this.pdbId = motif.getPdbId();
        this.ecNumber = motif.getEcNumber();
        this.activeSiteResidues = motif.getActiveSiteResidues();

        this.alignments = new ArrayList<>();
        this.failedAlignments = new ArrayList<>();
        this.failedPdbIds = new ArrayList<>();
    }

    @Data
    private class SuccessfulAlignment {
        private String pdbId;
        private String ecNumber;
        private double rmsd;
        private int levenstein;
        private List<Residue> alignedResidues;

        private SuccessfulAlignment(Structure queryStructure, Alignment alignment) {
            this.pdbId = queryStructure.getPDBCode();
            this.ecNumber = StructureUtils.ecNumber(queryStructure);
            this.rmsd = alignment.getRmsd();
            this.levenstein = alignment.getLevenstein();
            this.alignedResidues = alignment.getAlignedResidues();
        }
    }

    public void addSuccessfulEntry(Structure queryStructure, Alignment alignment) {
        this.alignments.add(new SuccessfulAlignment(queryStructure, alignment));
    }

    @Data
    @AllArgsConstructor
    private class FailedAlignment {
        private String pdbId;
        private String ecNumber;
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
