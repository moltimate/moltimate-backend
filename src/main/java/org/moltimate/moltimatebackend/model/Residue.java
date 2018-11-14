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
    private String residueId; // "7", "70", ...

    public static Residue fromGroup(Group residue) {
        return Residue.builder()
                .residueName(residue.getChemComp()
                                     .getThree_letter_code())
                .residueId(residue.getResidueNumber()
                                   .toString())
                .build();
    }
}
