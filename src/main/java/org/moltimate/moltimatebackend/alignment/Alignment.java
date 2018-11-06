package org.moltimate.moltimatebackend.alignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the results of performing a single alignment between two active sites.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alignment {
    String proteinName;
    String motifName;
    List<String> residues;
    List<String> activeSite;
    int minDistance;
    int maxDistance;
    double rmsd;
}
