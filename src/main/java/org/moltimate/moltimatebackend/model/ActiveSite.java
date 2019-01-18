package org.moltimate.moltimatebackend.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * This is used to store active site residues for a PDB entry when automatically generating motifs.
 */
@Data
@Builder
public class ActiveSite {
    String pdbId;
    List<Residue> residues;
}
