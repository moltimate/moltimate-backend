package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.request.ActiveSiteAlignmentRequest;
import org.moltimate.moltimatebackend.request.BackboneAlignmentRequest;
import org.moltimate.moltimatebackend.response.AlignmentResponse;
import org.moltimate.moltimatebackend.util.AlignmentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AlignmentService provides a way to align the active sites and backbones of
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
     * @return AlignmentResponse which contains all alignments and their relevant data
     */
    public AlignmentResponse alignActiveSites(ActiveSiteAlignmentRequest alignmentRequest) {
        return alignActiveSites(
                proteinService.queryPdb(alignmentRequest.getPdbIds()),
                motifService.queryByEcNumber(alignmentRequest.getEcNumber())
        );
    }

    public AlignmentResponse alignActiveSites(List<Structure> sourceStructures, List<Motif> motifStructures) {
        HashMap<String, List<Alignment>> results = new HashMap<>();
        sourceStructures.forEach(structure ->
                                         results.put(structure.getPDBCode(), motifStructures.stream()
                                                 .parallel()
                                                 .map(motif -> alignActiveSites(
                                                         structure,
                                                         motif
                                                 ))
                                                 .filter(Objects::nonNull)
                                                 .collect(Collectors.toList())));
        return new AlignmentResponse(results);
    }

    private Alignment alignActiveSites(Structure structure, Motif motif) {
        Map<String, List<Group>> residueMap = motif.runQueries(structure, 1);
        List<Integer> distances = new ArrayList<>();

        residueMap.values()
                .forEach(residueList -> {
                    String alignmentString = AlignmentUtils.groupListToResString(residueList);
                    String motifResString = AlignmentUtils.residueListToResString(motif.getActiveSiteResidues());
                    distances.add(AlignmentUtils.levensteinDistance(alignmentString, motifResString));
                });

        if (!distances.isEmpty() && (motif.getActiveSiteResidues()
                .size()) > Collections.min(distances)) {
            HashSet<Group> residues = new HashSet<>();

            residueMap.values()
                    .forEach(residues::addAll);
            if(residues.size() >2 && residues.size() <= motif.getActiveSiteResidues().size()) {
                Alignment alignment = new Alignment();
                alignment.setActiveSiteResidues(motif.getActiveSiteResidues());
                alignment.setMotifPdbId(motif.getPdbId());
                alignment.setMinDistance(Collections.min(distances));
                alignment.setMaxDistance(Collections.max(distances));
                alignment.setAlignedResidues(residues.stream()
                                                     .map(Residue::fromGroup)
                                                     .collect(Collectors.toList()));
                return alignment;
            }
        }

        return null;
    }

    /**
     * Executes the BackboneAlignmentRequest on protein backbones.
     *
     * @param alignmentRequest The alignment request JSON mapped to an object
     * @return AlignmentResponse which contains all alignments and their relevant data
     */
    public AlignmentResponse alignBackbones(BackboneAlignmentRequest alignmentRequest) {
        return alignBackbones(
                proteinService.queryPdb(alignmentRequest.getSourcePdbIds()),
                proteinService.queryPdb(alignmentRequest.getCompareToPdbIds())
        );
    }

    private AlignmentResponse alignBackbones(List<Structure> sourceStructures, List<Structure> compareToStructures) {
        // ... for each sourceStructures, call alignBackbones() against all compareToStructures

        return new AlignmentResponse(/* populate me with a list of Alignments */);
    }

    private Alignment alignBackbones(Structure structure1, Structure structure2) {
        // ... logic to perform one backbone alignment between two structures

        return new Alignment();
    }
}
