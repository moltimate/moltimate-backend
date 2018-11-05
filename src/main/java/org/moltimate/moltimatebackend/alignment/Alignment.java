package org.moltimate.moltimatebackend.alignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the results of performing a single alignment between two active sites.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alignment {

    double rmsd;
}
