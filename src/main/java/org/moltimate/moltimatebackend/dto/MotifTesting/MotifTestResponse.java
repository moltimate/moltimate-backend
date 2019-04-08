package org.moltimate.moltimatebackend.dto.MotifTesting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.moltimate.moltimatebackend.model.Alignment;

import java.util.List;
import java.util.Map;

/**
 * A list of Alignments and useful data around them.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotifTestResponse {
    Map<String, List<Alignment>> alignments;

    Map<String, List<FailedAlignment>> failedAlignments;

    List<String> failedPdbIds;
}
