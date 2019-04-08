package org.moltimate.moltimatebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Group;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Represents a single residue in an active site
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Residue {

    @NotNull
    private String residueName; // "Asp", "Glu", ...

    @NotNull
    private String residueChainName; // "A", "B", ...

    @NotNull
    private String residueId; // "7", "70", ...

    @NotNull
    private String residueAltLoc;

    public String getIdentifier() {
        return residueName + " " + residueChainName + " " + residueId;
    }

    public static Residue fromGroup(Group residue) {
        return Residue.builder()
                .residueName(residue.getChemComp().getThree_letter_code())
                .residueId(residue.getResidueNumber().toString())
                .residueChainName(residue.getResidueNumber().getChainName())
                .residueAltLoc(getAltLocFromGroup(residue))
                .build();
    }

    public static String getAltLocFromGroup(Group residue){
        if(residue.hasAltLoc()){
            return residue.getAtoms().get(1).getAltLoc().toString();
        }
        return "";
    }
}
