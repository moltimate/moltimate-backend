package org.moltimate.moltimatebackend.dto.Alignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuccessfulAlignment {
    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    @NotNull
    private String pdbId;

    @NotNull
    private String ecNumber;

    @NotNull
    private double rmsd;

    @NotNull
    private int levenstein;

    @NotNull
    @ElementCollection
    private List<Residue> activeSiteResidues;

    @NotNull
    @ElementCollection
    private List<Residue> alignedResidues;

    public SuccessfulAlignment(Motif motif, Alignment alignment) {
        this.pdbId = motif.getPdbId();
        this.ecNumber = motif.getEcNumber();
        this.rmsd = alignment.getRmsd();
        this.levenstein = alignment.getLevenstein();
        this.activeSiteResidues = alignment.getActiveSiteResidues();
        this.alignedResidues = alignment.getAlignedResidues();
    }

    public SuccessfulAlignment clone(){
        List<Residue> cloneActiveSiteResidues = new ArrayList<>();
        for(Residue residue: activeSiteResidues){
            cloneActiveSiteResidues.add(residue.clone());
        }
        List<Residue> cloneAlignedResidues = new ArrayList<>();
        for(Residue residue: alignedResidues){
            cloneAlignedResidues.add(residue.clone());
        }
        return new SuccessfulAlignment(this.id, this.pdbId, this.ecNumber, this.rmsd, this.levenstein,
                                       cloneActiveSiteResidues, cloneAlignedResidues);
    }
}
