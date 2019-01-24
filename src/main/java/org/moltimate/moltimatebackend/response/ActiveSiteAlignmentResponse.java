package org.moltimate.moltimatebackend.response;

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
public class ActiveSiteAlignmentResponse {
    Map<String, List<Alignment>> alignments;
}
