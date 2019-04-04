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

public class PromolActiveSiteParser implements ActiveSiteParser {

    // TODO: don't make web requests for this, use the local file
    private static final String PROMOL_CSV_URL = "https://raw.githubusercontent.com/moltimate/moltimate-backend/master/src/main/resources/motifdata/promol_active_sites.csv";

    /**
     * Retrieve all active sites from the scraped Promol motifs.
     *
     * @return A list of ActiveSites
     */
    public List<ActiveSite> parseMotifs() {
        try {
            Reader reader = new InputStreamReader(new FileUrlResource(new URL(PROMOL_CSV_URL)).getInputStream());
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1)
                    .build();
            String[] residueEntry;
            List<ActiveSite> activeSites = new ArrayList<>();
            while ((residueEntry = csvReader.readNext()) != null) {
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

            return activeSites;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
