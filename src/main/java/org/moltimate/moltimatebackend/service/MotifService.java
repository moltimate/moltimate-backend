package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.repository.ResidueQuerySetRepository;
import org.moltimate.moltimatebackend.validation.EcNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * MotifService provides a way to query for and create motifs which represent the active sites of proteins.
 */
@Service
@Slf4j
public class MotifService {

    private static final int MOTIF_BATCH_SIZE = 512;

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
        log.info("Saving motif with ID " + motif.getPdbId());
        motif.getSelectionQueries()
                .values()
                .forEach(residueQuerySetRepository::save);
        return motifRepository.save(motif);
    }

    /**
     * @param pdbId PDB ID for this Motif
     * @return The matching motif
     */
    public Motif queryByPdbId(String pdbId) {
        return motifRepository.findByPdbId(pdbId);
    }

    /**
     * @return List of all Motifs in the database
     */
    public Page<Motif> findAll(int pageNumber) {
        return motifRepository.findAll(PageRequest.of(pageNumber, MOTIF_BATCH_SIZE));
    }

    /**
     * @param ecNumber EC number to filter the set of comparable motifs
     * @return List of Motifs in this enzyme commission class
     */
    public Page<Motif> queryByEcNumber(String ecNumber, int pageNumber) {
        if (ecNumber == null) {
            return findAll(pageNumber);
        }
        EcNumberValidator.validate(ecNumber);
        return motifRepository.findByEcNumberStartingWith(ecNumber, PageRequest.of(pageNumber, MOTIF_BATCH_SIZE));
    }

    /**
     * Delete all motifs and their residue query sets
     */
    public void deleteAllAndFlush() {
        motifRepository.deleteAll();
        motifRepository.flush();
//        residueQuerySetRepository.deleteAll();
//        residueQuerySetRepository.flush();
    }
}
