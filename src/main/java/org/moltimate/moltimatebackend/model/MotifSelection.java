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
    private String atomType1;

    @NotNull
    private String atomType2;

    @NotNull
    private String residueName1;

    @NotNull
    private String residueName2;

    @NotNull
    private double distance;
}
