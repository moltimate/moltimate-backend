package org.moltimate.moltimatebackend.dto.Alignment;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of Alignments and useful data around them.
 */
@Data
public class QueryAlignmentResponse {

    private List<QueryResponseData> entries;

    private List<String> failedPdbIds; // PDB ids that failed to be processed

    public QueryAlignmentResponse() {
        this.entries = new ArrayList<>();
        this.failedPdbIds = new ArrayList<>();
    }

    public void addQueryResponseData(QueryResponseData newData) {
        QueryResponseData found = null;
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

    public void addFailedPdbIds(List<String> pdbIds) {
        this.failedPdbIds.addAll(pdbIds);
    }

    public void addFailedPdbId(String pdbId) {
        this.failedPdbIds.add(pdbId);
    }
}
