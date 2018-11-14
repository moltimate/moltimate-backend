package org.moltimate.moltimatebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MotifSelection {

    @NotNull
    private String atom1Name;

    @NotNull
    private String atom2Name;

    @NotNull
    private String residue1Name;

    @NotNull
    private String residue2Name;

    @NotNull
    private double distance;
}
