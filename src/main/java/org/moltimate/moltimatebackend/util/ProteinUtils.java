package org.moltimate.moltimatebackend.util;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.MMCIFFileReader;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.moltimate.moltimatebackend.dto.PdbQueryResponse;
import org.moltimate.moltimatebackend.validation.exceptions.InvalidFileException;
import org.moltimate.moltimatebackend.validation.exceptions.InvalidPdbIdException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ProteinUtils {

    private static final PDBFileReader PDB_FILE_READER = new PDBFileReader();
    private static final MMCIFFileReader MMCIF_FILE_READER = new MMCIFFileReader();

    /**
     * @param pdbIds List of PDB ID strings
     * @return List of BioJava Structure objects representing each PDB ID
     */
    public static List<Structure> queryPdb(List<String> pdbIds) {
        return pdbIds.stream()
                .map(ProteinUtils::queryPdbOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static PdbQueryResponse queryPdbResponse(List<String> pdbIds) {
        if (pdbIds.size() > 0){
            return new PdbQueryResponse().generatePdbQueryResponse(pdbIds, queryPdb(pdbIds));
        } return new PdbQueryResponse();
    }

    /**
     * @param pdbId PDB ID string
     * @return BioJava Structure object representing the PDB ID
     */
    public static Structure queryPdb(String pdbId) {
        try {
            return PDB_FILE_READER.getStructureById(pdbId);
        } catch (IOException pdbReaderError) {
            try {
                return MMCIF_FILE_READER.getStructureById(pdbId);
            } catch (IOException mmcifReaderError) {
//                mmcifReaderError.printStackTrace();
                throw new InvalidPdbIdException(pdbId);
            }
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

    public static Structure structureFromFile(MultipartFile file) {
        try {
            return PDB_FILE_READER.getStructure(file.getInputStream());
        } catch (IOException pdbReaderError) {
            try {
                return MMCIF_FILE_READER.getStructure(file.getInputStream());
            } catch (IOException ignored) {
                throw new InvalidFileException("Could not parse given file\nPlease check the file to make sure it is a valid PDB or MMCIF file");
            }
        }
    }
}
