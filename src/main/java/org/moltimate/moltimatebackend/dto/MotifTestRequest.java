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
import java.util.Optional;

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

    // Motif to be created and then tested
    private String pdbId;
    private String ecNumber;
    private List<String> activeSiteResidues = new ArrayList<>();
    private MultipartFile customStructure;
    private Type type;

    // Test information
    private int precisionFactor;

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
}
