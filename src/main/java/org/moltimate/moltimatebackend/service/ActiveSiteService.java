package org.moltimate.moltimatebackend.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.HttpUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Service used to query for or create active site Residues. Also provides the functionality to update our database's
 * active site table.
 */
@Service
@Validated
@Slf4j
public class ActiveSiteService {

    private static final String CSA_CSV_URL = "https://www.ebi.ac.uk/thornton-srv/m-csa/media/flat_files/curated_data.csv";

    /**
     * Retrieve all active sites from the Catalytic Site Atlas.
     *
     * @return A list of ActiveSites
     */
    public List<ActiveSite> getActiveSites() {
        try {
            CSVReader reader = new CSVReader(new FileReader("tmp/active_sites.csv"), ',');
            String[] residueEntry;
            List<ActiveSite> activeSites = new ArrayList<>();
            while ((residueEntry = reader.readNext()) != null) {
                String pdbId = residueEntry[0];
                List<Residue> activeSiteResidues = new ArrayList<>();
                for (int i = 1; i < residueEntry.length; i++) {
                    String[] res = residueEntry[i].split(" ");
                    Residue residue = Residue.builder()
                                             .residueName(res[0])
                                             .residueId(res[1])
                                             .residueChainName(res[2])
                                             .build();
                    activeSiteResidues.add(residue);
                }

                activeSites.add(ActiveSite.builder()
                                          .pdbId(pdbId)
                                          .residues(activeSiteResidues)
                                          .build());
            }
            Reader catalyticSiteAtlasCsvData = new StringReader(HttpUtils.readStringFromURL(CSA_CSV_URL));
            CSVReader csvReader = new CSVReaderBuilder(catalyticSiteAtlasCsvData).withSkipLines(1)
                                                                                 .build();

            ActiveSite nextSite;
            while ((nextSite = readNextActiveSite(csvReader)) != null) {
                activeSites.add(nextSite);
            }

            return activeSites;
        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * Reads the next protein's active site residues from the Catalytic Site Atlas curated data file.
     */
    private ActiveSite readNextActiveSite(CSVReader csvReader) throws IOException {
        List<Residue> activeSiteResidues = new ArrayList<>();

        String[] residueEntry;
        while ((residueEntry = csvReader.readNext()) != null) {
            String pdbId = residueEntry[2];

            boolean isResidue = "residue".equals(residueEntry[4]);
            if (pdbId.equals("1a0j") || pdbId.equals("1rtf")) {
                System.out.println("yeet");
            }
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
