package org.moltimate.moltimatebackend.dto.MotifTesting;

import lombok.Data;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.StructureUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class SuccessfulAlignment {

    private String motifPdbId;
    private String motifEcNumber;
    private List<Residue> activeSiteResidues;
    private List<SuccessfulAlignmentData> entries;

    @Data
    private class SuccessfulAlignmentData {
        private String queryPdbId;
        private String queryEcNumber;
        private double rmsd;
        private int levenshtein;
        private List<Residue> alignedResidues;

        SuccessfulAlignmentData(Structure queryStructure, Alignment alignment) {
            this.queryPdbId = queryStructure.getPDBCode();
            this.queryEcNumber = StructureUtils.ecNumber(queryStructure);
            this.rmsd = alignment.getRmsd();
            this.levenshtein = alignment.getMaxDistance(); // Todo fix when alignment is modified to 1 levenshtein distance
            this.alignedResidues = alignment.getAlignedResidues();
        }
    }

    public SuccessfulAlignment(String motifPdbId, String motifEcNumber, List<Residue> activeSiteResidues) {
        this.motifPdbId = motifPdbId;
        this.motifEcNumber = motifEcNumber;
        this.activeSiteResidues = activeSiteResidues;
        this.entries = new ArrayList<>();
    }

    public void addEntry(Structure queryStructure, Alignment alignment) {
        this.entries.add(new SuccessfulAlignmentData(queryStructure, alignment));
    }
}
