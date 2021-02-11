package org.moltimate.moltimatebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This is used to store active site residues for a PDB entry when automatically generating motifs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSite {

    private String pdbId;
    private List<Residue> residues;
}
