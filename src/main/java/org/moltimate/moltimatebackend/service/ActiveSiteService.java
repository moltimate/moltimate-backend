package org.moltimate.moltimatebackend.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.moltimate.moltimatebackend.helper.HttpHelper;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.repository.ActiveSiteRepository;
import org.moltimate.moltimatebackend.validation.EcNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Service used to query for or create ActiveSites. Also provides the functionality to update our database's
 * active site table.
 */
@Service
@Validated
@Slf4j
public class ActiveSiteService {

    private static final String CSA_CSV_URL = "https://www.ebi.ac.uk/thornton-srv/m-csa/media/flat_files/curated_data.csv";

    @Autowired
    private ActiveSiteRepository activeSiteRepository;

    /**
     * Find a single ActiveSite by its PDB ID.
     *
     * @param pdbId PDB ID that is unique to this active site
     * @return ActiveSite
     */
    public ActiveSite queryByPdbId(String pdbId) {
        return activeSiteRepository.findByPdbId(pdbId);
    }

    /**
     * Find a single ActiveSite by its M-CSA ID.
     *
     * @param mcsaId M-CSA ID that is unique to this active site
     * @return ActiveSite
     */
    public ActiveSite queryByMcsaId(String mcsaId) {
        return activeSiteRepository.findByMcsaId(mcsaId);
    }

    /**
     * Find a single ActiveSite by its Uniprot ID.
     *
     * @param uniprotId Uniprot ID that is unique to this active site
     * @return ActiveSite
     */
    public ActiveSite queryByUniprotId(String uniprotId) {
        return activeSiteRepository.findByUniprotId(uniprotId);
    }

    /**
     * Find a single ActiveSite by its custom ID.
     *
     * @param customId custom ID that is unique to this active site
     * @return ActiveSite
     */
    public ActiveSite queryByCustomId(String customId) {
        return activeSiteRepository.findByCustomId(customId);
    }

    /**
     * Find a list of ActiveSites by their EC number.
     * Can be partially qualified (3, or 3.4, or 3.4.24, or 3.4.24.11).
     *
     * @param ecNumber PDB ID that is unique to this active site
     * @return List of ActiveSites
     */
    public List<ActiveSite> queryByEcNumber(String ecNumber) {
        EcNumberValidator.validate(ecNumber);
        return activeSiteRepository.findByEcNumberStartingWith(ecNumber);
    }

    /**
     * Save a new ActiveSite to the database.
     *
     * @param activeSite New ActiveSite
     * @return The new ActiveSite
     */
    public ActiveSite createActiveSite(@Valid ActiveSite activeSite) {
        return activeSiteRepository.save(activeSite);
    }

    /**
     * Updates database with current active site information from the Catalytic Site Atlas.
     */
    public void updateActiveSiteTable() {
        try (Reader catalyticSiteAtlasCsvData = new StringReader(HttpHelper.readStringFromURL(CSA_CSV_URL));
             CSVReader csvReader = new CSVReaderBuilder(catalyticSiteAtlasCsvData).withSkipLines(1).build()) {

            ActiveSite nextSite;
            int activeSitesUpdated = 0;
            while ((nextSite = readNextActiveSite(csvReader)) != null) {
                activeSiteRepository.save(nextSite);
                activeSitesUpdated += 1;
            }

            log.info("Updated " + activeSitesUpdated + " protein active sites");
        } catch (IOException e) {
            log.error("Error reading catalytic site atlas curated data file");
            e.printStackTrace();
        }
    }

    /**
     * Reads out the next ActiveSite from a CSVReader attached to the Catalytic Site Atlas curated data file.
     */
    private ActiveSite readNextActiveSite(CSVReader csvReader) throws IOException {
        String[] residueEntry = csvReader.peek();
        String mcsaId = residueEntry[0];
        String uniprotId = residueEntry[1];
        String pdbId = residueEntry[2];

        MultiKeyMap<String, Residue> proteinResidues = new MultiKeyMap<>();
        while ((residueEntry = csvReader.readNext()) != null) {
            // If this row is a residue (instead of reactant, product, ...), store it in a MultiKeyMap.
            // A MultikeyMap is used to dedupe residue rows by 1) residueName, 2) chainId, and 3) residueId.
            boolean isResidue = "residue".equals(residueEntry[4]);
            if (isResidue) {
                String residueName = residueEntry[5];
                String chainId = residueEntry[6];
                String residueId = residueEntry[7];
                Residue residue = Residue.builder()
                                         .residueName(residueName)
                                         .chainId(chainId)
                                         .residueId(residueId)
                                         .build();
                proteinResidues.put(residueName, chainId, residueId, residue);
            }

            // If the next row is for a different protein, return the current ActiveSite
            String[] nextEntry = csvReader.peek();
            if (nextEntry != null && !nextEntry[2].equals(pdbId)) {
                return ActiveSite.builder()
                                 .pdbId(pdbId)
                                 .mcsaId(mcsaId)
                                 .uniprotId(uniprotId)
                                 .ecNumber(residueEntry[3])
                                 .residues(new ArrayList<>(proteinResidues.values()))
                                 .build();
            }
        }

        return null;
    }
}
