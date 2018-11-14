package org.moltimate.moltimatebackend.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BackboneAlignmentRequest represents the PDB ids of the proteins whose active sites will be compared
 * against a second list of proteins.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackboneAlignmentRequest extends AlignmentRequest {

    private List<String> sourcePdbIds;
    private List<String> compareToPdbIds;
}
