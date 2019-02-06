package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.MotifSelection;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.model.ResidueQuerySet;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.repository.ResidueQuerySetRepository;
import org.moltimate.moltimatebackend.util.StructureUtils;
import org.moltimate.moltimatebackend.validation.EcNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MotifService provides a way to query for and create motifs which represent the active sites of proteins.
 */
@Service
@Slf4j
public class MotifService {

    private static final int MOTIF_BATCH_SIZE = 512;

    @Autowired
    private ActiveSiteService activeSiteService;

    @Autowired
    private MotifRepository motifRepository;

    @Autowired
    private ProteinService proteinService;

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
    public Page<Motif> findAll(int pageNumber) {
        log.info("Getting motifs (page " + pageNumber + ", batch size " + MOTIF_BATCH_SIZE + ")");
        return motifRepository.findAll(new PageRequest(pageNumber, MOTIF_BATCH_SIZE));
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
    public Page<Motif> queryByEcNumber(String ecNumber, int pageNumber) {
        if (ecNumber == null) {
            return findAll(pageNumber);
        }

        log.info("Querying for motifs in EC class: " + ecNumber);
        EcNumberValidator.validate(ecNumber);
        return motifRepository.findByEcNumberStartingWith(ecNumber, new PageRequest(pageNumber, MOTIF_BATCH_SIZE));
    }

    // TODO: Change to running periodically (once per week?), or create an endpoint to force-update
    // TODO: Pessimistic lock tables until update is finished
    @EventListener(ApplicationReadyEvent.class)
    private void updateMotifs() {
        log.info("Updating Motif database from the Catalytic Site Atlas and the RCSB PDB");

        // Delete all motifs and their residue query sets
        motifRepository.deleteAll();
        motifRepository.flush();
        residueQuerySetRepository.deleteAll();
        residueQuerySetRepository.flush();

        List<String> failedPdbIds = new ArrayList<>();
        activeSiteService.getActiveSites()
                         .parallelStream()
                         .forEach(activeSite -> {
                             String pdbId = activeSite.getPdbId();
                             try {
                                 List<Residue> residues = activeSite.getResidues();
                                 Structure structure = proteinService.queryPdb(pdbId);

                                 Motif motif = Motif.builder()
                                                    .pdbId(pdbId)
                                                    .activeSiteResidues(residues)
                                                    .ecNumber(StructureUtils.ecNumber(structure))
                                                    .selectionQueries(generateSelectionQueries(structure, residues))
                                                    .build();
                                 saveMotif(motif);
                             } catch (Exception e) {
                                 e.printStackTrace();
                                 failedPdbIds.add(pdbId);
                             }
                         });

        System.out.println(failedPdbIds.stream()
                                       .filter(pdbId -> !"".equals(pdbId))
                                       .collect(Collectors.toList()));
        System.out.println(failedPdbIds.stream()
                                       .filter(pdbId -> !"".equals(pdbId))
                                       .collect(Collectors.toList())
                                       .size() + " PDB entries failed (no nulls)");

        log.info("Finished updating Motif database");
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
            List<Atom> filteredAtoms = groupAtoms.subList(groupAtoms.indexOf(firstCbAtom), groupAtoms.size());
            filteredAtoms = filteredAtoms.stream().filter(atom -> !atom.getName().contains("H")).collect(Collectors.toList());
            filteredResidueAtoms.put(residue, filteredAtoms);
        });

        // Compare every filtered atom in each residue to every other filtered atom in every other residue of this protein
        Map<String, ResidueQuerySet> selectionQueries = new HashMap<>();
        filteredResidueAtoms.forEach((residue, filteredAtoms) -> {
            List<MotifSelection> motifSelections = new ArrayList<>();
            filteredResidueAtoms.forEach((compareResidue, comparefilteredAtoms) -> {
                if (residue == compareResidue) {
                    return; // Do not compare a residue to itself
                }

                filteredAtoms.forEach(atom -> {
                    comparefilteredAtoms.forEach(compareAtom -> {
                        MotifSelection motifSelection = MotifSelection.builder()
                                                                      .atomType1(atom.getName())
                                                                      .atomType2(compareAtom.getName())
                                                                      .residueName1(atom.getGroup()
                                                                                        .getChemComp()
                                                                                        .getThree_letter_code())
                                                                      .residueName2(compareAtom.getGroup()
                                                                                               .getChemComp()
                                                                                               .getThree_letter_code())
                                                                      .distance(StructureUtils.rmsd(
                                                                              atom,
                                                                              compareAtom
                                                                      ) + 2)
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
