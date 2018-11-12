package org.moltimate.moltimatebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Represents a single active site, including its list of residues.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSite {

    @Id
    @NotNull
    @Size(min = 1)
    private String pdbId; // PDB ID

    private String mcsaId; // M-CSA ID

    private String uniprotId; // Uniprot ID

    private String customId; // An optional custom identifier useful for custom active sites

    @Size(min = 7)
    @NotNull
    private String ecNumber;

    @ElementCollection
    @Valid
    private List<Residue> residues;
}
