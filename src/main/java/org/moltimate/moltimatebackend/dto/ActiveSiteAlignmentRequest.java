package org.moltimate.moltimatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.util.FileUtils;
import org.moltimate.moltimatebackend.util.ProteinUtils;
import org.moltimate.moltimatebackend.validation.exceptions.InvalidPdbIdException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ActiveSiteAlignmentRequest represents the PDB ids of the proteins whose active sites will be compared
 * against a set of motifs in the provided ecNumber.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSiteAlignmentRequest {

    private List<String> pdbIds = new ArrayList<>();
    private List<String> options = new ArrayList<>();
    private List<String> filters = new ArrayList<>();
    private List<MultipartFile> customMotifs = new ArrayList<>();
    private String ecNumber;
    private int precisionFactor;

    public PdbQueryResponse callPdbForResponse() {
        PdbQueryResponse response = ProteinUtils.queryPdbResponse(pdbIds);
        if (response.structures.size() == 0) {
            throw new InvalidPdbIdException(pdbIds);
        }
        return response;
    }

    public List<Motif> extractCustomMotifsFromFiles() {
        return customMotifs.stream()
                .map(FileUtils::getMotifFromFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Map<Motif, Structure> extractCustomMotifMapFromFiles() {
        Map<Motif, Structure> results = new HashMap<>();
        for (MultipartFile file : customMotifs) {
            Motif cMotif = FileUtils.getMotifFromFile(file);
            Structure cStructure = ProteinUtils.structureFromFile(file);
            if (cMotif != null && cStructure != null) {
                results.put(cMotif, cStructure);
            }
        }
        return results;
    }

    public int getPrecisionFactor() {
        if (this.precisionFactor <= 0) {
            return 1;
        }
        return this.precisionFactor;
    }
}
