package org.moltimate.moltimatebackend.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.HttpUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @return A Map where the String key is a protein's PDB ID and the List<Residue> is each residue in the active site
     */
    public Map<String, List<Residue>> getActiveSites() {
        try (Reader catalyticSiteAtlasCsvData = new StringReader(HttpUtils.readStringFromURL(CSA_CSV_URL));
             CSVReader csvReader = new CSVReaderBuilder(catalyticSiteAtlasCsvData).withSkipLines(1)
                     .build()
        ) {
            Map<String, List<Residue>> activeSites = new HashMap<>();
            List<Residue> nextSite;
            while ((nextSite = readNextActiveSite(csvReader)) != null) {
                String pdbId = csvReader.peek()[2];
                activeSites.put(pdbId, nextSite);
            }

            return activeSites;
        } catch (IOException e) {
            log.error("Error reading catalytic site atlas curated data file");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Reads out the next list of active site Residues from a CSVReader attached to the Catalytic Site Atlas curated data file.
     */
    private List<Residue> readNextActiveSite(CSVReader csvReader) throws IOException {
        String[] residueEntry = csvReader.peek();
        String pdbId = residueEntry[2];

        MultiKeyMap<String, Residue> proteinResidues = new MultiKeyMap<>();
        while ((residueEntry = csvReader.readNext()) != null) {
            // If this row is a residue (instead of reactant, product, ...), store it in a MultiKeyMap.
            // A MultikeyMap is used to dedupe residue rows by 1) residueName and 2) residueId.
            boolean isResidue = "residue".equals(residueEntry[4]);
            if (isResidue) {
                String residueName = residueEntry[5];
                String residueId = residueEntry[7];
                Residue residue = Residue.builder()
                        .residueName(residueName)
                        .residueId(residueId)
                        .build();
                proteinResidues.put(residueName, residueId, residue);
            }

            // If the next row is for a different protein, return the current active site Residues
            String[] nextEntry = csvReader.peek();
            if (nextEntry != null && !nextEntry[2].equals(pdbId)) {
                return new ArrayList<>(proteinResidues.values());
            }
        }

        return null;
    }
}
