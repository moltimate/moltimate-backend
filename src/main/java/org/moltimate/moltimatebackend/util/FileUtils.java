package org.moltimate.moltimatebackend.util;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.FileConvert;
import org.biojava.nbio.structure.io.MMCIFFileReader;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.moltimate.moltimatebackend.dto.MakeMotifRequest;
import org.moltimate.moltimatebackend.dto.MotifFile;
import org.moltimate.moltimatebackend.exception.InvalidFileException;
import org.moltimate.moltimatebackend.exception.MotifFileParseException;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    private static final PDBFileReader PDB_FILE_READER = new PDBFileReader();
    private static final MMCIFFileReader MMCIF_FILE_READER = new MMCIFFileReader();
    private static final String MOTIF_DATA_SEPARATOR = "MOLTIMATE-DATA\n";

    private static enum ProteinFileType {
        PDB(".pdb"),
        MMCIF(".cif"),
        MOTIF(".motif");

        private String fileExtension;

        ProteinFileType(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        @Override
        public String toString() {
            return fileExtension;
        }

        public static List<String> getValidFileTypes() {
            return Arrays.stream(ProteinFileType.values())
                    .map(proteinFileType -> proteinFileType.toString())
                    .collect(Collectors.toList());
        }
    }

    public static ResponseEntity<Resource> createPdbFile(String pdbId) {
        Resource pdbFile = new ByteArrayResource(ProteinUtils.queryPdb(pdbId)
                                                         .toPDB()
                                                         .getBytes());
        return createResponseFile(pdbFile, pdbId, ProteinFileType.PDB);
    }

    public static ResponseEntity<Resource> createMmcifFile(String pdbId) {
        Resource mmcifFile = new ByteArrayResource(ProteinUtils.queryPdb(pdbId)
                                                           .toMMCIF()
                                                           .getBytes());
        return createResponseFile(mmcifFile, pdbId, ProteinFileType.MMCIF);
    }

    public static ResponseEntity<Resource> createMotifFile(MakeMotifRequest request) {
        return createMotifFile(request.getPdbId(), request.getEcNumber(), request.getActiveSiteResidues());
    }

    public static ResponseEntity<Resource> createMotifFile(String pdbId, String ecNumber, List<Residue> activeSiteResidues) {
        String pdbFile = ProteinUtils.queryPdb(pdbId).toPDB();
        List<String> residueStrings = activeSiteResidues.stream()
                .map(residue -> String.format(
                        "%s %s %s",
                        residue.getResidueName(),
                        residue.getResidueId(),
                        residue.getResidueChainName()
                     )
                )
                .collect(Collectors.toList());

        String motifFileFooter = MOTIF_DATA_SEPARATOR
                + String.format("%s,%s,", pdbId, ecNumber)
                + String.join(",", residueStrings);

        Resource motifFile = new ByteArrayResource((pdbFile + motifFileFooter).getBytes());
        return createResponseFile(motifFile, pdbId, ProteinFileType.MOTIF);
    }

    public static ResponseEntity<Resource> createMotifFile(String pdbId, String ecNumber, List<Residue> activeSiteResidues, Structure structure) {
        String pdbFile = structure.toPDB();
        List<String> residueStrings = activeSiteResidues.stream()
                .map(residue -> String.format(
                        "%s %s %s",
                        residue.getResidueName(),
                        residue.getResidueId(),
                        residue.getResidueChainName()
                     )
                )
                .collect(Collectors.toList());

        String motifFileFooter = MOTIF_DATA_SEPARATOR
                + String.format("%s,%s,", pdbId, ecNumber)
                + String.join(",", residueStrings);

        Resource motifFile = new ByteArrayResource((pdbFile + motifFileFooter).getBytes());
        return createResponseFile(motifFile, pdbId, ProteinFileType.MOTIF);
    }

    public static Structure getStructureFromFile(MultipartFile file) {
        return readMotifFile(file).getStructure();
    }

    public static Motif getMotifFromFile(MultipartFile file) {
        return readMotifFile(file).getMotif();
    }

    public static MotifFile readMotifFile(MultipartFile file) {
        try {
            String[] partitions = new String(file.getBytes()).split(MOTIF_DATA_SEPARATOR);
            return MotifFile.builder()
                    .structure(getStructureFromString(partitions[0]))
                    .motif(getMotifFromString(partitions[1]))
                    .build();
        } catch (InvalidFileException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MotifFileParseException(file);
        }
    }

    private static Structure getStructureFromString(String string) {
        try {
            return PDB_FILE_READER.getStructure(new ByteArrayInputStream(string.getBytes()));
        } catch (IOException pdbReaderError) {
            try {
                return MMCIF_FILE_READER.getStructure(new ByteArrayInputStream(string.getBytes()));
            } catch (IOException ignored) {
                throw new InvalidFileException(
                        "Could not parse given file\nPlease check the file to make sure it is a valid format: " + ProteinFileType.getValidFileTypes());
            }
        }
    }

    private static Motif getMotifFromString(String string) {
        String[] motifData = string.split(",");

        List<Residue> activeSiteResidues = new ArrayList<>();
        for (int i = 2; i < motifData.length; i++) {
            String[] residueData = motifData[i].split(" ");
            activeSiteResidues.add(Residue.builder()
                                           .residueName(residueData[0])
                                           .residueId(residueData[1])
                                           .residueChainName(residueData[2])
                                           .build());
        }

        String pdbId = motifData[0];
        String ecNumber = motifData[1];
        return MotifUtils.generateMotif(pdbId, ecNumber, ProteinUtils.queryPdb(pdbId), activeSiteResidues);
    }

    private static ResponseEntity<Resource> createResponseFile(Resource file, String fileName, ProteinFileType proteinFileType) {
        return createResponseFile(file, fileName, proteinFileType.toString());
    }

    private static ResponseEntity<Resource> createResponseFile(Resource file, String fileName, String fileExtension) {
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + fileExtension + "\""
                )
                .body(file);
    }
}
