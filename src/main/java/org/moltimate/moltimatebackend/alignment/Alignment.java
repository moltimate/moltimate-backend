package org.moltimate.moltimatebackend.alignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.moltimate.moltimatebackend.model.Residue;

import java.util.List;

/**
 * Represents the results of performing a single alignment between two active sites.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alignment {
    String motifPdbId;
    List<Residue> activeSiteResidues;
    List<Residue> alignedResidues;
    int minDistance;
    int maxDistance;
    double rmsd;
}
