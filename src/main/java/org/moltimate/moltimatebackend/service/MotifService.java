package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.model.ResidueQuerySet;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.repository.ResidueQuerySetRepository;
import org.moltimate.moltimatebackend.util.ActiveSiteUtils;
import org.moltimate.moltimatebackend.util.MotifUtils;
import org.moltimate.moltimatebackend.util.ProteinUtils;
import org.moltimate.moltimatebackend.util.StructureUtils;
import org.moltimate.moltimatebackend.validation.EcNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
    private Motif saveMotif(Motif motif) {
        motif.getSelectionQueries()
                .values()
                .forEach(residueQuerySetRepository::save);
        return motifRepository.save(motif);
    }

    /**
     * Batch saves a list of new Motif to the database.
     *
     * @param motifs New Motif to save
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
    private Page<Motif> findAll(int pageNumber) {
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
        return motifRepository.findByEcNumberEqualsOrEcNumberStartingWith(
                "unknown", ecNumber, PageRequest.of(pageNumber, MOTIF_BATCH_SIZE));
    }

    /**
     * Delete all motifs and their residue query sets
     */
    public void deleteAllAndFlush() {
        // TODO: figure out why this breaks
//        motifRepository.deleteAll();
//        motifRepository.flush();
//        residueQuerySetRepository.deleteAll();
//        residueQuerySetRepository.flush();
    }

    // TODO: Pessimistic lock the motifs table until update is finished
    public Integer updateMotifs() {
        log.info("Updating Motif database");
        List<ActiveSite> activeSites = ActiveSiteUtils.getActiveSites();

        log.info("Deleting and flushing Motif database");
//        deleteAllAndFlush();

        log.info("Saving " + activeSites.size() + " new motifs");
        AtomicInteger motifsSaved = new AtomicInteger(0);
        List<String> failedPdbIds = new ArrayList<>();
        activeSites.parallelStream()
                .forEach(activeSite -> {
                    String pdbId = activeSite.getPdbId();
                    try {
                        List<Residue> residues = activeSite.getResidues();
                        Structure structure = ProteinUtils.queryPdb(pdbId);
                        String ecNumber = StructureUtils.ecNumber(structure);

                        Motif motif = MotifUtils.generateMotif(pdbId, ecNumber, structure, residues);
                        saveMotif(motif);
                        motifsSaved.incrementAndGet();
                    } catch (Exception e) {
                        e.printStackTrace();
                        failedPdbIds.add(pdbId);
                    }
                });

        log.info("Failed to save " + failedPdbIds.size() + " motifs to the database: " + failedPdbIds.toString());
        log.info("Finished saving " + motifsSaved.get() + " motifs to the database");
        return motifsSaved.get();
    }
}
