package org.moltimate.moltimatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.util.ProteinUtils;
import org.moltimate.moltimatebackend.validation.exceptions.InvalidPdbIdException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotifTestRequest {
    public enum Type {
        SELF,
        LIST,
        HOMOLOGUE,
        RANDOM,
        BULK_RANDOM
    }

    // Motif Attributes
    private String pdbId;
    private String ecNumber;
    private List<String> activeSiteResidues = new ArrayList<>();
    private MultipartFile customStructure;

    // Testing Attributes
    private Type type;
    private int precisionFactor = 1;
    private List<String> testPdbIds = new ArrayList<>();
    private List<MultipartFile> customStructures = new ArrayList<>();

    public Structure motifStructure() {
        if (customStructure == null) {
            Optional<Structure> response = ProteinUtils.queryPdbOptional(pdbId);
            if (response.isPresent()) {
                return response.get();
            }
            throw new InvalidPdbIdException(pdbId);
        }
        return ProteinUtils.structureFromFile(customStructure);
    }

    public int getPrecisionFactor() {
        if (this.precisionFactor <= 0) {
            return 1;
        }
        return this.precisionFactor;
    }

    public PdbQueryResponse callPdbForResponse() {
        return ProteinUtils.queryPdbResponse(testPdbIds);
    }

    public List<Structure> extractCustomStructuresFromFiles() {
        return customStructures.stream()
                .map(ProteinUtils::structureFromFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
