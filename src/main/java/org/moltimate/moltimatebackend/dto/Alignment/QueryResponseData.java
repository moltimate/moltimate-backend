package org.moltimate.moltimatebackend.dto.Alignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.StructureUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class QueryResponseData {
    private String queryPdbId;
    private String queryEcNumber;

    List<SuccessfulAlignment> alignments;   // Alignments that found a match
    List<FailedAlignment> failedAlignments; // Alignments that did not find a match

    public QueryResponseData(Structure structure) {
        this.queryPdbId = structure.getPDBCode();
        this.queryEcNumber = StructureUtils.ecNumber(structure);

        this.alignments = new ArrayList<>();
        this.failedAlignments = new ArrayList<>();
    }

    @Data
    private class SuccessfulAlignment {
        private String motifPdbId;
        private String motifEcNumber;
        private double rmsd;
        private int levenshtein;
        private List<Residue> activeSiteResidues;
        private List<Residue> alignedResidues;

        private SuccessfulAlignment(Motif motif, Alignment alignment) {
            this.motifPdbId = motif.getPdbId();
            this.motifEcNumber = motif.getEcNumber();
            this.rmsd = alignment.getRmsd();
            this.levenshtein = alignment.getMaxDistance(); // Todo fix when alignment is modified to 1 levenshtein distance
            this.activeSiteResidues = alignment.getActiveSiteResidues();
            this.alignedResidues = alignment.getAlignedResidues();
        }
    }

    @Data
    @AllArgsConstructor
    private class FailedAlignment {
        private String motifPdbId;
        private String motifEcNumber;
    }

    public void addSuccessfulEntry(Motif motif, Alignment alignment) {
        this.alignments.add(new SuccessfulAlignment(motif, alignment));
    }

    public void addFailedEntry(String motifPdbId, String motifEcNumber) {
        this.failedAlignments.add(new FailedAlignment(motifPdbId, motifEcNumber));
    }

    public boolean similar(QueryResponseData other) {
        if (this.equals(other)) {
            return true;
        } else {
            if (this.getQueryPdbId().equals(other.getQueryPdbId())) {
                return this.getQueryEcNumber().equals(other.getQueryEcNumber());
            }
        }
        return false;
    }

    public void merge(QueryResponseData other) {
        if (this.similar(other)) {
            this.alignments.addAll(other.getAlignments());
            this.failedAlignments.addAll(other.getFailedAlignments());
        }
    }
}