package org.moltimate.moltimatebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String chainId; // "A", "G", "AA", ...

    @NotNull
    private String residueId; // "7", "70", ...
}
