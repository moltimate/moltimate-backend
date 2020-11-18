package org.moltimate.moltimatebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResidueQuerySet {

    @Id
    @GeneratedValue
    private long id;

    @ElementCollection
    private List<MotifSelection> selections;

    public ResidueQuerySet(List<MotifSelection> selections) {
        this.selections = selections;
    }
}
