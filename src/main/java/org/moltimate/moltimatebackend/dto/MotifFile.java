package org.moltimate.moltimatebackend.dto;

import lombok.Builder;
import lombok.Data;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Motif;

@Data
@Builder
public class MotifFile {

    Structure structure;
    Motif motif;
}
