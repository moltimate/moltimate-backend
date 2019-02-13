package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.ResidueQuerySet;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.repository.ResidueQuerySetRepository;
import org.moltimate.moltimatebackend.validation.EcNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MotifService provides a way to query for and create motifs which represent the active sites of proteins.
 */
@Service
@Slf4j
public class MotifService {

    private static final int MOTIF_BATCH_SIZE = 32;

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
     * Batch saves a list of new Motif to the database.
     *
     * @param motifs New Motif to save
     * @return A newly generated Motif
     */
    public void saveMotifs(List<Motif> motifs) {
        log.info("Saving " + motifs.size() + " motifs with IDs " + motifs.stream()
                .map(Motif::getPdbId)
                .collect(Collectors.toList()));

        List<ResidueQuerySet> residueQuerySets = motifs.stream()
                .map(Motif::getSelectionQueries)
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        residueQuerySetRepository.saveAll(residueQuerySets);
        motifRepository.saveAll(motifs);
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
        residueQuerySetRepository.deleteAll();
        residueQuerySetRepository.flush();
    }
}
