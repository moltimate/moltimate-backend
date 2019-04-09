package org.moltimate.moltimatebackend.parser;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.core.io.FileUrlResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CsaActiveSiteParser implements ActiveSiteParser {

    // TODO: don't make web requests for this, use the local file
    private static final String CSA_CSV_URL = "https://raw.githubusercontent.com/moltimate/moltimate-backend/master/src/main/resources/motifdata/csa_curated_data.csv";

    /**
     * Retrieve all active sites from the Catalytic Site Atlas.
     *
     * @return A list of ActiveSites
     */
    public List<ActiveSite> parseMotifs() {
        try {
            Reader reader = new InputStreamReader(new FileUrlResource(new URL(CSA_CSV_URL)).getInputStream());
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1)
                    .build();

            List<ActiveSite> activeSites = new ArrayList<>();
            ActiveSite nextSite;
            while ((nextSite = readNextCsaActiveSite(csvReader)) != null) {
                activeSites.add(nextSite);
            }

            return activeSites;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Reads the next protein's active site residues from the Catalytic Site Atlas curated data file.
     */
    private static ActiveSite readNextCsaActiveSite(CSVReader csvReader) throws IOException {
        List<Residue> activeSiteResidues = new ArrayList<>();

        String[] residueEntry;
        while ((residueEntry = csvReader.readNext()) != null) {
            String pdbId = residueEntry[2];

            boolean isResidue = "residue".equals(residueEntry[4]);
            if (isResidue) {
                Residue residue = Residue.builder()
                        .residueName(residueEntry[5])
                        .residueChainName(residueEntry[6])
                        .residueId(residueEntry[7])
                        .build();
                if (!activeSiteResidues.contains(residue)) {
                    activeSiteResidues.add(residue);
                }
            }

            // If the next row is the end of file OR start of a different protein, stop and return current Residue list
            String[] nextEntry = csvReader.peek();
            if (nextEntry == null || !nextEntry[2].equals(pdbId)) {
                return ActiveSite.builder()
                        .pdbId(pdbId)
                        .residues(activeSiteResidues)
                        .build();
            }
        }

        return null;
    }
}
