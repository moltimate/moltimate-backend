package org.moltimate.moltimatebackend.motif;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MotifSelection {

    @NotNull
    String atom1Name;

    @NotNull
    String atom2Name;

    @NotNull
    String residue1Name;

    @NotNull
    String residue2Name;

    @NotNull
    double distance;


}
