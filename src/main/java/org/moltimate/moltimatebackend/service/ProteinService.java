package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProteinService provides a way to query proteins from the PDB and convert them to BioJava Structure objects.
 */
@Service
@Slf4j
public class ProteinService {

    private static final PDBFileReader PDB_FILE_READER = new PDBFileReader();

    @Autowired
    private AlignmentService alignmentService;

    /**
     * @param pdbIds Varargs array of PDB ID strings
     * @return List of BioJava Structure objects representing each PDB ID
     */
    public List<Structure> queryPdb(String... pdbIds) {
        return queryPdb(Arrays.asList(pdbIds));
    }

    /**
     * @param pdbIds List of PDB ID strings
     * @return List of BioJava Structure objects representing each PDB ID
     */
    public List<Structure> queryPdb(List<String> pdbIds) {
        return pdbIds.stream()
                .map(this::queryPdb)
                .collect(Collectors.toList());
    }

    /**
     * @param pdbId PDB ID string
     * @return BioJava Structure object representing the PDB ID
     */
    public Structure queryPdb(String pdbId) {
        try {
            return PDB_FILE_READER.getStructureById(pdbId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot find structure with PDB id " + pdbId);
        }
    }
}
