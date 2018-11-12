package org.moltimate.moltimatebackend.alignment;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.Structure.StructureUtils;
import org.moltimate.moltimatebackend.alignment.requests.ActiveSiteAlignmentRequest;
import org.moltimate.moltimatebackend.alignment.requests.BackboneAlignmentRequest;
import org.moltimate.moltimatebackend.motif.Motif;
import org.moltimate.moltimatebackend.motif.MotifService;
import org.moltimate.moltimatebackend.protein.ProteinService;
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
                motifService.queryMotifs(alignmentRequest.getEcNumber())
        );
    }

    public AlignmentResponse alignActiveSites(List<Structure> sourceStructures, List<Motif> motifStructures) {
        // ... for each sourceStructure, call alignActiveSites() against all motifStructures
        HashMap<String, List<Alignment>> results = new HashMap<>();
        sourceStructures.forEach(structure ->
                                         results.put(structure.getPDBCode(), motifStructures.stream().parallel()
                                                                                            .map(motif -> alignActiveSites(
                                                                                                    structure,
                                                                                                    motif
                                                                                            ))
                                                                                            .filter(Objects::nonNull)
                                                                                            .collect(Collectors.toList())));
        return new AlignmentResponse(results);
    }

    private Alignment alignActiveSites(Structure structure1, Motif motif) {
        // ... logic to perform one active site alignment between two structures

        Map<String, List<Group>> residueMap = motif.runQueries(structure1, 1);
        List<Integer> distances = new ArrayList<>();

        residueMap.values().forEach(residueList -> {
            String alignmentString = AlignmentUtils.groupListToResString(residueList);
            String motifResString = AlignmentUtils.residueListToResString(motif.getResidues());
            distances.add(AlignmentUtils.levensteinDistance(alignmentString, motifResString));
        });

        if (!distances.isEmpty() && (motif.getResidues().size()) > Collections.min(distances)) {
            HashSet<Group> residues = new HashSet<>();
            residueMap.values().forEach(residues::addAll);
            Alignment alignment = new Alignment();
            alignment.setActiveSite(motif.getActiveSite());
            alignment.setProteinName(structure1.getPDBCode());
            alignment.setMotifName(motif.getPdbId());
            alignment.setResidues(residues.stream()
                                          .map(StructureUtils::residueName)
                                          .collect(Collectors.toList()));
            return alignment;
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
