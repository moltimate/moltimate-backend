package org.moltimate.moltimatebackend.util;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.MMCIFFileReader;
import org.biojava.nbio.structure.io.PDBFileReader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ProteinUtils {

    private static final PDBFileReader PDB_FILE_READER = new PDBFileReader();
    private static final MMCIFFileReader MMCIF_FILE_READER = new MMCIFFileReader();

    /**
     * @param pdbIds List of PDB ID strings
     * @return List of BioJava Structure objects representing each PDB ID
     */
    public static List<Structure> queryPdb(List<String> pdbIds) {
        return pdbIds.stream()
                .map(ProteinUtils::queryPdb)
                .collect(Collectors.toList());
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
                mmcifReaderError.printStackTrace();
                throw new RuntimeException("Cannot find structure with PDB id " + pdbId);
            }
        }
    }
}
