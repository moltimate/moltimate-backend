package org.moltimate.moltimatebackend.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service used to query for or create active site Residues. Also provides the functionality to update our database's
 * active site table.
 */
@Service
@Slf4j
public class ActiveSiteService {

    //TODO: don't make web requests for these, use the local files.
    private static final String PROMOL_CSV_URL = "https://raw.githubusercontent.com/moltimate/moltimate-backend/master/src/main/resources/motifdata/promol_active_sites.csv";
    private static final String CSA_CSV_URL = "https://raw.githubusercontent.com/moltimate/moltimate-backend/master/src/main/resources/motifdata/csa_curated_data.csv";
//    private static final String CSA_CSV_URL = "https://www.ebi.ac.uk/thornton-srv/m-csa/media/flat_files/csa_curated_data.csv";

    /**
     * Returns a list of protein active sites from known sources of truth
     */
    public List<ActiveSite> getActiveSites() {
        return dedupeActiveSites(Arrays.asList(
                getCsaActiveSites(),
                getPromolActiveSites()
        ));
    }

    /**
     * Takes an ordered list of active site lists, and dedupes them by PDB ID. The lists are processed in the order
     * they are passed, so the first list gets priority if there are duplicate PDB IDs.
     *
     * @param activeSiteLists Ordered list of active site lists
     * @return List of distinct active sites from all of the provided active site lists
     */
    private List<ActiveSite> dedupeActiveSites(List<List<ActiveSite>> activeSiteLists) {
        Map<String, Boolean> pdbIdSeen = new HashMap<>();
        List<ActiveSite> distinctActiveSites = new ArrayList<>();

        activeSiteLists.forEach(activeSites -> {
            activeSites.forEach(activeSite -> {
                if (activeSite.getResidues()
                        .size() < 3) {
                    return; // ignore active sites with fewer than 3 residues
                }

                if (!pdbIdSeen.containsKey(activeSite.getPdbId()) && !"".equals(activeSite.getPdbId())) {
                    distinctActiveSites.add(activeSite);
                    pdbIdSeen.put(activeSite.getPdbId(), true);
                }
            });
        });

        return distinctActiveSites;
    }

    /**
     * Retrieve all active sites from the Catalytic Site Atlas.
     *
     * @return A list of ActiveSites
     */
    private List<ActiveSite> getCsaActiveSites() {
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
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    /**
     * Retrieve all active sites from the scraped Promol motifs.
     *
     * @return A list of ActiveSites
     */
    private List<ActiveSite> getPromolActiveSites() {
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
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    /**
     * Reads the next protein's active site residues from the Catalytic Site Atlas curated data file.
     */
    private ActiveSite readNextCsaActiveSite(CSVReader csvReader) throws IOException {
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
