package org.moltimate.moltimatebackend.dto.Alignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of Alignments and useful data around them.
 */
@Data
@Entity
@Builder
@AllArgsConstructor
public class QueryAlignmentResponse {

    @Id
    private String cacheKey;

    @ElementCollection
    @NotNull
    private List<QueryResponseData> entries;

    @ElementCollection
    private List<String> failedPdbIds; // PDB ids that failed to be processed

    public QueryAlignmentResponse() {
        this.entries = new ArrayList<>();
        this.failedPdbIds = new ArrayList<>();
    }

    public void addQueryResponseData(QueryResponseData newData) {
        QueryResponseData found = null;
        if (newData.getAlignments()
                .isEmpty() && newData.getFailedAlignments()
                .isEmpty()) {
            return;
        }
        for (QueryResponseData entry : this.entries) {
            if (entry.similar(newData)) {
                found = entry;
                break;
            }
        }
        if (found == null) {
            this.entries.add(newData);
        } else {
            found.merge(newData);
        }
    }

    public void merge(QueryAlignmentResponse other) {
        for (QueryResponseData responseData : other.getEntries()) {
            this.addQueryResponseData(responseData);
        }
    }

    public void addFailedPdbIds(List<String> pdbIds) {
        this.failedPdbIds.addAll(pdbIds);
    }

    public void addFailedPdbId(String pdbId) {
        this.failedPdbIds.add(pdbId);
    }

    public void filterEcNumber(String ecNumberPrefix) {
        if (ecNumberPrefix != null) {
            for (QueryResponseData entry : entries) {
                entry.filterEcNumber(ecNumberPrefix);
            }
        }
    }
}
