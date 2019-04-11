package org.moltimate.moltimatebackend.dto.Alignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.util.StructureUtils;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponseData {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String pdbId;

    @NotNull
    private String ecNumber;

    @ElementCollection
    private List<SuccessfulAlignment> alignments;   // Alignments that found a match

    @ElementCollection
    private List<FailedAlignment> failedAlignments; // Alignments that did not find a match

    public QueryResponseData(Structure structure) {
        this.pdbId = structure.getPDBCode();
        this.ecNumber = StructureUtils.ecNumber(structure);

        this.alignments = new ArrayList<>();
        this.failedAlignments = new ArrayList<>();
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
            if (this.getPdbId()
                    .equals(other.getPdbId())) {
                return this.getEcNumber()
                        .equals(other.getEcNumber());
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