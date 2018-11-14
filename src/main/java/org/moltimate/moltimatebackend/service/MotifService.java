package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.repository.ResidueQuerySetRepository;
import org.moltimate.moltimatebackend.validation.EcNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MotifService provides a way to query for and create motifs which represent the active sites of proteins.
 */
@Service
@Slf4j
public class MotifService {

    @Autowired
    private MotifRepository motifRepository;

    @Autowired
    private ResidueQuerySetRepository residueQuerySetRepository;

    /**
     * Saves a new Motif to the database.
     *
     * @param motif New Motif to save
     * @return A newly generated Motif
     */
    public Motif saveMotif(Motif motif) {
        motif.getSelectionQueries()
                .values()
                .forEach(residueQuerySetRepository::save);
        return motifRepository.save(motif);
    }

    /**
     * @return List of all Motifs in the database
     */
    public List<Motif> findAll() {
        log.info("Getting all motifs");
        return motifRepository.findAll();
    }

    /**
     * @param pdbId PDB ID for this Motif
     * @return The matching motif
     */
    public Motif queryByPdbId(String pdbId) {
        log.info("Querying for motif with pdbId: " + pdbId);
        return motifRepository.findByPdbId(pdbId);
    }

    /**
     * @param ecNumber EC number to filter the set of comparable motifs
     * @return List of Motifs in this enzyme commission class
     */
    public List<Motif> queryByEcNumber(String ecNumber) {
        log.info("Querying for motifs in EC class: " + ecNumber);
        EcNumberValidator.validate(ecNumber);
        return motifRepository.findByEcNumberStartingWith(ecNumber);
    }
}
