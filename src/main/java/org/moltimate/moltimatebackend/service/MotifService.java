package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.MotifSelection;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.model.ResidueQuerySet;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.repository.ResidueQuerySetRepository;
import org.moltimate.moltimatebackend.util.ProteinUtils;
import org.moltimate.moltimatebackend.util.StructureUtils;
import org.moltimate.moltimatebackend.validation.EcNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

    @Autowired
    private ActiveSiteService activeSiteService;

    @Autowired
    private MotifService motifService;

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
        return motifRepository.findByEcNumberEqualsOrEcNumberStartingWith("unknown", ecNumber, PageRequest.of(pageNumber, MOTIF_BATCH_SIZE));
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

    // TODO: Pessimistic lock the motifs table until update is finished
    public void updateMotifs() {
        log.info("Updating Motif database");
        List<ActiveSite> activeSites = activeSiteService.getActiveSites();

        log.info("Deleting and flushing Motif database");
        motifService.deleteAllAndFlush();

        log.info("Saving " + activeSites.size() + " new motifs");
        AtomicInteger motifsSaved = new AtomicInteger(0);
        List<String> failedPdbIds = new ArrayList<>();
        activeSites.parallelStream()
                .forEach(activeSite -> {
                    String pdbId = activeSite.getPdbId();
                    try {
                        List<Residue> residues = activeSite.getResidues();
                        Structure structure = ProteinUtils.queryPdb(pdbId);
                        for (Residue res : residues) {
                            Group group = StructureUtils.getResidue(
                                    structure, res.getResidueName(), res.getResidueId());
                            res.setResidueChainName(group.getResidueNumber()
                                                            .getChainName());
                            res.setResidueAltLoc(Residue.getAltLocFromGroup(group));
                        }
                        Motif motif = Motif.builder()
                                .pdbId(pdbId)
                                .activeSiteResidues(residues)
                                .ecNumber(StructureUtils.ecNumber(structure))
                                .selectionQueries(generateSelectionQueries(structure, residues))
                                .build();
                        motifService.saveMotif(motif);
                        motifsSaved.incrementAndGet();
                    } catch (Exception e) {
                        e.printStackTrace();
                        failedPdbIds.add(pdbId);
                    }
                });

        log.info("Failed to save " + failedPdbIds.size() + " motifs to the database: " + failedPdbIds.toString());
        log.info("Finished saving " + motifsSaved.get() + " motifs to the database");
    }

    /**
     * Generate a map where keys are PDB IDs and values are ResidueQuerySets
     *
     * @param structure          Structure (protein) to generate selection queries for
     * @param activeSiteResidues List of active site Residue objects for this protein
     * @return
     */
    private Map<String, ResidueQuerySet> generateSelectionQueries(Structure structure, List<Residue> activeSiteResidues) {
        // Remove unwanted atoms from each residue's list of atoms
        Map<Residue, List<Atom>> filteredResidueAtoms = new HashMap<>();
        activeSiteResidues.forEach(residue -> {
            Group group = StructureUtils.getResidue(structure, residue.getResidueName(), residue.getResidueId());
            List<Atom> groupAtoms = group.getAtoms();
            Atom firstCbAtom = groupAtoms.stream()
                    .filter(atom -> atom.getName()
                            .equals("CB"))
                    .findFirst()
                    .orElse(groupAtoms.get(1));
            if (residue.getResidueName()
                    .equalsIgnoreCase("ALA")) {
                firstCbAtom = groupAtoms.get(1);
            }
            List<Atom> filteredAtoms = groupAtoms.subList(groupAtoms.indexOf(firstCbAtom), groupAtoms.size());
            filteredAtoms = filteredAtoms.stream()
                    .filter(atom -> !atom.getName()
                            .contains("H"))
                    .collect(Collectors.toList());
            filteredResidueAtoms.put(residue, filteredAtoms);
        });

        // Compare every filtered atom in each residue to every other filtered atom in every other residue of this protein
        Map<String, ResidueQuerySet> selectionQueries = new HashMap<>();
        filteredResidueAtoms.forEach((residue, filteredAtoms) -> {
            List<MotifSelection> motifSelections = new ArrayList<>();
            filteredResidueAtoms.forEach((compareResidue, compareFilteredAtoms) -> {
                if (residue == compareResidue) {
                    return; // Do not compare a residue to itself
                }

                filteredAtoms.forEach(atom -> {
                    compareFilteredAtoms.forEach(compareAtom -> {
                        MotifSelection motifSelection = MotifSelection.builder()
                                .atomType1(atom.getName())
                                .atomType2(compareAtom.getName())
                                .residueName1(atom.getGroup()
                                                      .getChemComp()
                                                      .getThree_letter_code())
                                .residueName2(compareAtom.getGroup()
                                                      .getChemComp()
                                                      .getThree_letter_code())
                                .distance(StructureUtils.rmsd(atom, compareAtom) + 2)
                                .build();
                        motifSelections.add(motifSelection);
                    });
                });
            });
            selectionQueries.put(residue.getIdentifier(), new ResidueQuerySet(motifSelections));
        });

        return selectionQueries;
    }
}
