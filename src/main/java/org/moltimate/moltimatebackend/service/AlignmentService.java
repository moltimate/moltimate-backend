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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AlignmentService provides a way to align the active sites and backbones of proteins
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
                alignmentRequest.getEcNumber()
        );
    }

    private AlignmentResponse alignActiveSites(List<Structure> sourceStructures, String ecNumber) {
        HashMap<String, List<Alignment>> results = new HashMap<>();
        sourceStructures.forEach(structure -> results.put(structure.getPDBCode(), new ArrayList<>()));

        Page<Motif> initialPage = motifService.queryByEcNumber(ecNumber, 0);
        for (int pageNumber = 0; pageNumber < initialPage.getTotalPages(); pageNumber++) {
            Page<Motif> motifStructures = motifService.queryByEcNumber(ecNumber, pageNumber);
            sourceStructures.forEach(structure -> {
                results.get(structure.getPDBCode())
                       .addAll(motifStructures.stream()
                                              .parallel()
                                              .map(motif -> alignActiveSites(
                                                      structure,
                                                      motif
                                              ))
                                              .filter(Objects::nonNull)
                                              .collect(Collectors.toList()));
            });
        }
        int resultsCount = 0;
        for(String key: results.keySet()){
            resultsCount+= results.get(key).size();
        }
        System.out.println("Found " + resultsCount + " results");

        return new AlignmentResponse(results);
    }

    private Alignment alignActiveSites(Structure structure, Motif motif) {
        Map<Residue, List<Group>> residueMap = motif.runQueries(structure, 1);
        List<Integer> distances = new ArrayList<>();

        List<Residue> seq1 = new ArrayList<>();
        List<Group> seq2 = new ArrayList<>();

        Map<Residue, Group> alignmentMapping = new HashMap<>();

        Set<Group> found = new HashSet<>();

        motif.getActiveSiteResidues().forEach(residue -> {
            List<Group> groups = residueMap.get(residue);
            if (groups != null && !groups.isEmpty()) {
                Group matchingResidue = groups.stream()
                                      .filter(group -> group.getResidueNumber().toString().equals(residue.getResidueId()))
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

        //String alignmentString = AlignmentUtils.groupListToResString(seq2);
        //String motifResString = AlignmentUtils.residueListToResString(motif.getActiveSiteResidues());
        //int distance = AlignmentUtils.levensteinDistance(alignmentString, motifResString);

        ArrayList<Residue> activeSiteOutput = new ArrayList<>();
        Set<Residue> used = new HashSet<>();

        for(int i = 0; i < seq1.size(); i++){
            activeSiteOutput.add(seq1.get(i));
            used.add(seq1.get(i));
        }

        int dist = 0;
        for(Residue residue: motif.getActiveSiteResidues()){
            if (!used.contains(residue)){
                activeSiteOutput.add(residue);
                dist++;
            }
        }

        if (seq2.size() > 1 && ( dist==0 || (dist<=activeSiteOutput.size()/3 && activeSiteOutput.size()>2) )) {
            HashSet<Group> residues = new HashSet<>();
            residueMap.values()
                      .forEach(residues::addAll);
            Alignment alignment = new Alignment();
            alignment.setActiveSiteResidues(activeSiteOutput);
            alignment.setMotifPdbId(motif.getPdbId());
            alignment.setMinDistance(dist);
            alignment.setMaxDistance(dist);
            alignment.setAlignedResidues(seq2.stream()
                                             .map(Residue::fromGroup)
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
