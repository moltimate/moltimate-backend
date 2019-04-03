package org.moltimate.moltimatebackend.util;

import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.MotifSelection;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.model.ResidueQuerySet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MotifUtils {

    public static Motif generateMotif(String pdbId, String ecNumber, Structure structure, List<Residue> residues) {
        for (Residue res : residues) {
            Group group = StructureUtils.getResidue(
                    structure, res.getResidueName(), res.getResidueId());
            assert group != null;
            res.setResidueChainName(group.getResidueNumber().getChainName());
            res.setResidueAltLoc(Residue.getAltLocFromGroup(group));
        }
        return Motif.builder()
                .pdbId(pdbId)
                .activeSiteResidues(residues)
                .ecNumber(ecNumber)
                .selectionQueries(generateSelectionQueries(structure, residues))
                .build();

    }

    /**
     * Generate a map where keys are PDB IDs and values are ResidueQuerySets
     *
     * @param structure          Structure (protein) to generate selection queries for
     * @param activeSiteResidues List of active site Residue objects for this protein
     * @return
     */
    private static Map<String, ResidueQuerySet> generateSelectionQueries(Structure structure, List<Residue> activeSiteResidues) {
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
