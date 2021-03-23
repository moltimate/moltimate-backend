package org.moltimate.moltimatebackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.dto.response.PdbQueryResponse;
import org.moltimate.moltimatebackend.exception.InvalidPdbIdException;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.FileUtils;
import org.moltimate.moltimatebackend.util.ProteinUtils;
import org.springframework.web.multipart.MultipartFile;
import org.moltimate.moltimatebackend.exception.MotifTestFailedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotifTestRequest {

    public enum Type {

        SELF("self"),
        LIST("list"),
        HOMOLOG("homolog"),
        RANDOM("random");

        private final String name;

        Type(final String name) {
            this.name = name;
        }

        public static Type fromName(String name) {
            for (Type t : values()) {
                if (t.name.equalsIgnoreCase(name)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown enum type " + name + ", Allowed values are " + Arrays.toString(values()));
        }
    }

    // Motif Attributes
    private String pdbId;
    private String ecNumber;
    private List<String> activeSiteResidues;
    private MultipartFile customMotifStructure;

    // Testing Attributes
    private Type type;
    private int precisionFactor = 1;
    private int randomCount = 1;
    private List<String> testPdbIds = new ArrayList<>();
    private List<MultipartFile> customStructures = new ArrayList<>();

    public Structure motifStructure() {
        if (customMotifStructure == null) {
            Optional<Structure> response = ProteinUtils.queryPdbOptional(pdbId);
            if (response.isPresent()) {
                return response.get();
            }
            throw new InvalidPdbIdException(pdbId);
        }
        return FileUtils.getStructureFromFile(customMotifStructure);
    }

    public Type getType() {
        if (this.type == null) {
            return Type.SELF;
        }
        return this.type;
    }

    public int getPrecisionFactor() {
        if (this.precisionFactor <= 0) {
            return 1;
        }
        return this.precisionFactor;
    }

    public int getRandomCount() {
        if (this.randomCount <= 0) {
            return 1;
        }
        return this.randomCount;
    }

    public PdbQueryResponse callPdbForResponse() {
        return ProteinUtils.queryPdbResponse(testPdbIds);
    }

    public List<Structure> extractCustomStructuresFromFiles() {
        return customStructures.stream()
                .map(FileUtils::getStructureFromFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Residue> parseResidueEntries() {
        List<Residue> residueList = new ArrayList<>();
        if(this.activeSiteResidues == null){
            throw new MotifTestFailedException("Missing Active Site Residues");
        }else {
            for (String residueEntry : this.activeSiteResidues) {
                for (String residueAttr : residueEntry.split(",")) {
                    String[] res = residueAttr.split(" ");
                    Residue residue = Residue.builder()
                            .residueName(res[0])
                            .residueChainName(res[1])
                            .residueId(res[2])
                            .build();
                    residueList.add(residue);
                }
            }
            residueList.sort(Comparator.comparingInt(r -> Integer.parseInt(r.getResidueId())));
            return residueList;
        }
    }
}
