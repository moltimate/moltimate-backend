package org.moltimate.moltimatebackend.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.moltimate.moltimatebackend.dto.MotifFile;
import org.moltimate.moltimatebackend.dto.PdbQueryResponse;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.util.FileUtils;
import org.moltimate.moltimatebackend.util.ProteinUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AlignmentRequest represents the PDB ids of the proteins whose active sites will be compared
 * against a set of motifs in the provided ecNumber.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlignmentRequest {

    private List<String> pdbIds = new ArrayList<>();
    private List<String> options = new ArrayList<>();
    private List<String> filters = new ArrayList<>();
    private List<MultipartFile> customMotifs = new ArrayList<>();
    private String ecNumber;
    private double precisionFactor;

    public PdbQueryResponse callPdbForResponse() {
        return ProteinUtils.queryPdbResponse(pdbIds);
    }

    public List<MotifFile> extractCustomMotifFileList() {
        return customMotifs.stream()
                .map(FileUtils::readMotifFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public double getPrecisionFactor() {
        if (this.precisionFactor <= 0) {
            return 1d;
        }
        return this.precisionFactor;
    }
}
