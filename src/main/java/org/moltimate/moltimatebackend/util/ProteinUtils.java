package org.moltimate.moltimatebackend.util;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.moltimate.moltimatebackend.dto.response.PdbQueryResponse;
import org.moltimate.moltimatebackend.exception.InvalidPdbIdException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ProteinUtils {

    /**
     * @param pdbIds List of PDB ID strings
     * @return List of BioJava Structure objects representing each PDB ID
     */
    public static List<Structure> queryPdb(List<String> pdbIds) {
        return pdbIds.stream().parallel()
                .map(ProteinUtils::queryPdbOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static PdbQueryResponse queryPdbResponse(List<String> pdbIds, List<MultipartFile> proteinFiles) {
        if (pdbIds.size() > 0 || proteinFiles.size() > 0) {
            List<Structure> structList = queryPdb(pdbIds);

            structList.addAll(FileUtils.getProteinsFromFiles(proteinFiles));
            return new PdbQueryResponse().generatePdbQueryResponse(pdbIds, structList);
        }
        return new PdbQueryResponse();
    }

    /**
     * @param pdbId PDB ID string
     * @return BioJava Structure object representing the PDB ID
     */
    public static Structure queryPdb(String pdbId) {
        try {
            return StructureIO.getStructure(pdbId);
        } catch (IOException | StructureException e) {
            throw new InvalidPdbIdException("Could not find structure with id: " + pdbId);
        }
    }

    /**
     * Use this when processing a list so that an error doesn't interrupt execution
     *
     * @param pdbId PDB ID string
     * @return Optional of a BioJava Structure object representing the PDB ID
     */
    public static Optional<Structure> queryPdbOptional(String pdbId) {
        try {
            return Optional.of(queryPdb(pdbId));
        } catch (InvalidPdbIdException e) {
            return Optional.empty();
        }
    }
}
