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
import java.util.stream.Collectors;

@Data
public class QueryResponseData {
    private String pdbId;
    private String ecNumber;

    private List<SuccessfulAlignment> alignments;   // Alignments that found a match
    private List<FailedAlignment> failedAlignments; // Alignments that did not find a match

    public QueryResponseData(Structure structure) {
        this.pdbId = structure.getPDBCode();
        this.ecNumber = StructureUtils.ecNumber(structure);

        this.alignments = new ArrayList<>();
        this.failedAlignments = new ArrayList<>();
    }

    @Data
    private class SuccessfulAlignment {
        private String pdbId;
        private String ecNumber;
        private double rmsd;
        private int levenstein;
        private List<Residue> activeSiteResidues;
        private List<Residue> alignedResidues;

        private SuccessfulAlignment(Motif motif, Alignment alignment) {
            this.pdbId = motif.getPdbId();
            this.ecNumber = motif.getEcNumber();
            this.rmsd = alignment.getRmsd();
            this.levenstein = alignment.getLevenstein();
            this.activeSiteResidues = alignment.getActiveSiteResidues();
            this.alignedResidues = alignment.getAlignedResidues();
        }
    }

    @Data
    @AllArgsConstructor
    private class FailedAlignment {
        private String pdbId;
        private String ecNumber;
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
            if (this.getPdbId().equals(other.getPdbId())) {
                return this.getEcNumber().equals(other.getEcNumber());
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

    public void filterEcNumber(String ecNumberPrefix) {
        if (ecNumberPrefix != null) {
            this.alignments = this.alignments.stream()
                    .filter(successfulAlignment -> successfulAlignment
                            .getEcNumber()
                            .startsWith(ecNumberPrefix))
                    .collect(Collectors.toList());
            this.failedAlignments = this.failedAlignments.stream()
                    .filter(failedAlignment -> failedAlignment
                            .getEcNumber()
                            .startsWith(ecNumberPrefix))
                    .collect(Collectors.toList());
        }
    }
}