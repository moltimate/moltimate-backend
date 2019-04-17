package org.moltimate.moltimatebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the results of performing a single alignment between two active sites.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alignment {

    private String motifPdbId;
    private List<Residue> activeSiteResidues;
    private List<Residue> alignedResidues;
    private int levenshtein;
    private double rmsd;
    private String ecNumber;
}
