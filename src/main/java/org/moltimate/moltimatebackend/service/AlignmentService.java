package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.request.ActiveSiteAlignmentRequest;
import org.moltimate.moltimatebackend.response.ActiveSiteAlignmentResponse;
import org.moltimate.moltimatebackend.util.AlignmentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AlignmentService provides a way to align the active sites of proteins
 */
@Service
@Slf4j
public class AlignmentService {

    @Autowired
    private ProteinService proteinService;

    @Autowired
    private MotifService motifService;

    /**
     * Executes the ActiveSiteAlignmentRequest on protein active sites.
     *
     * @param alignmentRequest The alignment request JSON mapped to an object
     * @return ActiveSiteAlignmentResponse which contains all alignments and their relevant data
     */
    public ActiveSiteAlignmentResponse alignActiveSites(ActiveSiteAlignmentRequest alignmentRequest) {
        return alignActiveSites(
                proteinService.queryPdb(alignmentRequest.getPdbIds()),
                alignmentRequest.getEcNumber()
        );
    }

    private ActiveSiteAlignmentResponse alignActiveSites(List<Structure> sourceStructures, String motifEcNumberFilter) {
        HashMap<String, List<Alignment>> results = new HashMap<>();
        sourceStructures.forEach(structure -> results.put(structure.getPDBCode(), new ArrayList<>()));

        int pageNumber = 0;
        Page<Motif> motifs = motifService.queryByEcNumber(motifEcNumberFilter, pageNumber);
        log.info(
                "Aligning active sites of " + sourceStructures.size() + " PDB entries with " + motifs.getTotalElements() + " motifs.");
        while (motifs.hasContent()) {
            for (Structure structure : sourceStructures) {
                results.get(structure.getPDBCode())
                        .addAll(motifs.stream()
                                        .parallel()
                                        .map(motif -> alignActiveSites(
                                                structure,
                                                motif
                                        ))
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList()));
            }
            pageNumber++;
            motifs = motifService.queryByEcNumber(motifEcNumberFilter, pageNumber);
        }

        int resultsCount = 0;
        for (String key : results.keySet()) {
            resultsCount += results.get(key)
                    .size();
        }

        log.info("Found " + resultsCount + " results");
        return new ActiveSiteAlignmentResponse(results);
    }

    private Alignment alignActiveSites(Structure structure, Motif motif) {
        Map<Residue, List<Group>> residueMap = motif.runQueries(structure, 1);

        List<Residue> seq1 = new ArrayList<>();
        List<Group> seq2 = new ArrayList<>();

        Map<Residue, Group> alignmentMapping = new HashMap<>();
        Set<Group> found = new HashSet<>();

        motif.getActiveSiteResidues()
                .forEach(residue -> {
                    List<Group> groups = residueMap.get(residue);
                    if (groups != null && !groups.isEmpty()) {
                        Group matchingResidue = groups.stream()
                                .filter(group -> group.getResidueNumber()
                                        .toString()
                                        .equals(residue.getResidueId()))
                                .findFirst()
                                .orElse(groups.get(0));

                        if (!found.contains(matchingResidue)) {
                            seq1.add(residue);
                            seq2.add(matchingResidue);
                            alignmentMapping.put(residue, matchingResidue);
                            found.add(matchingResidue);
                        }
                    }
                });

        ArrayList<Residue> activeSiteOutput = new ArrayList<>();
        Set<Residue> used = new HashSet<>();

        for (int i = 0; i < seq1.size(); i++) {
            activeSiteOutput.add(seq1.get(i));
            used.add(seq1.get(i));
        }

        int missingResidues = 0;
        for (Residue residue : motif.getActiveSiteResidues()) {
            if (!used.contains(residue)) {
                activeSiteOutput.add(residue);
                missingResidues++;
            }
        }

        List<Group> seq2Sorted = new ArrayList<>();
        seq2Sorted.addAll(seq2);
        Collections.sort(seq2Sorted, Comparator.comparingInt(o -> o.getResidueNumber()
                .getSeqNum()));

        String alignmentString = AlignmentUtils.groupListToResString(seq2Sorted);
        String motifResString = AlignmentUtils.residueListToResString(motif.getActiveSiteResidues());
        int distance = AlignmentUtils.levensteinDistance(alignmentString, motifResString);

        if (seq2.size() > 1 && acceptableDistance(activeSiteOutput.size(), distance)) {
            HashSet<Group> residues = new HashSet<>();
            residueMap.values()
                    .forEach(residues::addAll);
            Alignment alignment = new Alignment();
            alignment.setActiveSiteResidues(activeSiteOutput);
            alignment.setMotifPdbId(motif.getPdbId());
            alignment.setMinDistance(distance);
            alignment.setMaxDistance(distance);
            alignment.setAlignedResidues(seq2.stream()
                                                 .map(Residue::fromGroup)
                                                 .collect(Collectors.toList()));
            return alignment;
        }

        return null;
    }

    private boolean acceptableDistance(int activeSiteSize, int distance) {
        if (activeSiteSize == 2) {
            return false;
        } else if (activeSiteSize >= 3) {
            return distance == 0;
        }
        return distance <= 1;
    }
}
