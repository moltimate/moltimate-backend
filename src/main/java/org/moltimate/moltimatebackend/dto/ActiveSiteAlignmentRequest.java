package org.moltimate.moltimatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.util.FileUtils;
import org.moltimate.moltimatebackend.util.ProteinUtils;
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
    private double precisionFactor;

    public PdbQueryResponse callPdbForResponse() {
        return ProteinUtils.queryPdbResponse(pdbIds);
    }

    public List<Motif> extractCustomMotifsFromFiles() {
        return customMotifs.stream()
                .map(FileUtils::getMotifFromFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<MotifStructure> extractCustomMotifMapFromFiles() {
        List<MotifStructure> motifStructureList = new ArrayList<>();
        for (MultipartFile file : customMotifs) {
            Motif _customMotif = FileUtils.getMotifFromFile(file);
            Structure _customStructure = FileUtils.structureFromFile(file);
            if (_customMotif != null && _customStructure != null) {
                motifStructureList.add(MotifStructure.builder()
                        .motif(_customMotif)
                        .motifStructure(_customStructure)
                        .build());
            }
        }
        return motifStructureList;
    }

    public double getPrecisionFactor() {
        if (this.precisionFactor <= 0) {
            return 1d;
        }
        return this.precisionFactor;
    }
}
