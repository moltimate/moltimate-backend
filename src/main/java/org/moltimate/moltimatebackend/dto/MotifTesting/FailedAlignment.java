package org.moltimate.moltimatebackend.dto.MotifTesting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedAlignment {

    private String motifPdbId;
    private String ecNumber;
}
