package org.moltimate.moltimatebackend.request;

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
import java.util.List;
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
    private String ecNumber; // TODO: Make this into a filter

    public List<Structure> getPdbIdsAsStructures() {
        List<Structure> structures = ProteinUtils.queryPdb(pdbIds);
        if (structures.size() == 0) {
            throw new InvalidPdbIdException(pdbIds);
        }
        return structures;
    }

    public List<Motif> getCustomMotifs() {
        return customMotifs.stream()
                .map(FileUtils::getMotifFromFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
