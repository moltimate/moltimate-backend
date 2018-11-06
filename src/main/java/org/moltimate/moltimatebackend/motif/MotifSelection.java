package org.moltimate.moltimatebackend.motif;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MotifSelection {
    String atom1Name;
    String atom2Name;
    String residue1Name;
    String residue2Name;
    double distance;
}
