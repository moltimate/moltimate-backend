package org.moltimate.moltimatebackend.dto.MotifTesting;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FailedAlignment {

    private String motifPdbId;
    private String motifEcNumber;
    private List<FailedAlignmentData> entries;

    @Data
    @AllArgsConstructor
    private class FailedAlignmentData {
        private String queryPdbId;
        private String queryEcNumber;
    }

    public FailedAlignment(String motifPdbId, String motifEcNumber) {
        this.motifPdbId = motifPdbId;
        this.motifEcNumber = motifEcNumber;
        this.entries = new ArrayList<>();
    }

    public void addEntry(String queryPdbId, String queryEcNumber) {
        this.entries.add(new FailedAlignmentData(queryPdbId, queryEcNumber));
    }
}
