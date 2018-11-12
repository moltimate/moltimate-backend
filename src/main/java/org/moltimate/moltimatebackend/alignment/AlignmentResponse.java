package org.moltimate.moltimatebackend.alignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * A list of Alignments and useful data around them.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlignmentResponse {
    Map<String, List<Alignment>> alignments;
}
