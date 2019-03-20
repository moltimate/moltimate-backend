package org.moltimate.moltimatebackend.util;

import org.biojava.nbio.structure.io.MMCIFFileReader;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileUtils {

    private static final PDBFileReader PDB_FILE_READER = new PDBFileReader();
    private static final MMCIFFileReader MMCIF_FILE_READER = new MMCIFFileReader();

    public static Motif getMotifFromFile(MultipartFile file) {
        return null;

        // TODO: Read motif metadata from file and process it

//        InputStream fileInputStream;
//        try {
//            fileInputStream = new ByteArrayInputStream(file.getBytes());
//            try {
//                return PDB_FILE_READER.getStructure(fileInputStream);
//            } catch (IOException pdbReaderError) {
//                return MMCIF_FILE_READER.getStructure(fileInputStream);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public static Resource getPdbFile(String pdbId) {
        return new ByteArrayResource(ProteinUtils.queryPdb(pdbId)
                                             .toPDB()
                                             .getBytes());
    }

    public static Resource getMmcifFile(String pdbId) {
        return new ByteArrayResource(ProteinUtils.queryPdb(pdbId)
                                             .toMMCIF()
                                             .getBytes());
    }

    public static Resource getMotifFile(String pdbId, List<Residue> activeSiteResidues) {
        return null;
    }
}
