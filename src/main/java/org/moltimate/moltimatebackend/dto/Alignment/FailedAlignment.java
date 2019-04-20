package org.moltimate.moltimatebackend.dto.Alignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FailedAlignment {
    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    @NotNull
    private String pdbId;

    @NotNull
    private String ecNumber;

    public FailedAlignment(String pdbId, String ecNumber) {
        this.pdbId = pdbId;
        this.ecNumber = ecNumber;
    }

    public FailedAlignment clone(){
        FailedAlignment clone = new FailedAlignment();
        clone.setId(this.id);
        clone.setPdbId(this.pdbId);
        clone.setEcNumber(this.ecNumber);
        return clone;
    }
}
