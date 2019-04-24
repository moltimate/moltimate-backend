package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.geometry.SuperPositionSVD;
import org.moltimate.moltimatebackend.dto.Alignment.QueryAlignmentResponse;
import org.moltimate.moltimatebackend.dto.Alignment.QueryResponseData;
import org.moltimate.moltimatebackend.dto.MotifFile;
import org.moltimate.moltimatebackend.dto.PdbQueryResponse;
import org.moltimate.moltimatebackend.dto.Request.AlignmentRequest;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.AlignmentUtils;
import org.moltimate.moltimatebackend.util.ProteinUtils;
import org.moltimate.moltimatebackend.util.StructureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AlignmentService provides a way to align the active sites of proteins
 */
@Service
@Slf4j
public class AlignmentService {

    @Autowired
    private MotifService motifService;

    @Autowired
    private CacheService cacheService;

    /**
     * Executes the AlignmentRequest on protein active sites.
     *
     * @param alignmentRequest The alignment request JSON mapped to an object
     * @return QueryAlignmentResponse which contains all alignments and their relevant data
     */
    public QueryAlignmentResponse alignActiveSites(AlignmentRequest alignmentRequest) {
        PdbQueryResponse pdbResponse = alignmentRequest.callPdbForResponse();
        List<Structure> sourceStructures = pdbResponse.getStructures();
        List<MotifFile> customMotifFileList = alignmentRequest.extractCustomMotifFileList();
        String motifEcNumberFilter = alignmentRequest.getEcNumber();
        double precision = alignmentRequest.getPrecisionFactor();

        QueryAlignmentResponse response = new QueryAlignmentResponse();

        // Align structures with motifs from the database
        for (Structure structure : sourceStructures) {
            String cacheKey = String.format("%s_%s", structure.getPDBCode(), precision);
            QueryAlignmentResponse structureResponse;
            if (motifEcNumberFilter != null) {
                structureResponse = cacheService.cache.getIfPresent(cacheKey);
                if (structureResponse == null) {
                    structureResponse = generateStructureResponse(structure, precision);
                }
            } else {
                structureResponse = cacheService.cache.get(
                    cacheKey,
                    k -> generateStructureResponse(structure, precision)
                );
            }
            response.merge(structureResponse);
        }
        if (motifEcNumberFilter != null) {
            log.info(String.format("Filtering results of structures by EC Class \"%s\"", motifEcNumberFilter));
            response = response.clone();
            response.filterEcNumber(motifEcNumberFilter);
        }

        // Align structures with custom uploaded motifs
        if (customMotifFileList.size() > 0) {
            for (Structure structure : sourceStructures) {
                QueryResponseData queryResponseData = new QueryResponseData(structure);
                customMotifFileList.stream()
                    .parallel()
                    .forEach(motifFile -> {
                        Alignment alignment = alignActiveSites(
                            structure, motifFile.getMotif(), motifFile.getStructure(), precision);
                        if (alignment != null) {
                            queryResponseData.addSuccessfulEntry(motifFile.getMotif(), alignment);
                        } else {
                            queryResponseData.addFailedEntry(
                                motifFile.getMotif()
                                    .getPdbId(), motifFile.getMotif()
                                    .getEcNumber());
                        }
                    });
                response.addQueryResponseData(queryResponseData);
            }
        }
        for (QueryResponseData responseData : response.getEntries()) {
            log.info(String.format("Found %d results for %s", responseData.getAlignments()
                .size(), responseData.getPdbId()));
        }

        if (pdbResponse.getFailedPdbIds()
            .size() > 0) {
            log.error(String.format(
                "Could not find PDB structures for the following ids: %s",
                pdbResponse.getFailedPdbIds()
            ));
            response.addFailedPdbIds(pdbResponse.getFailedPdbIds());
        }
        return response;
    }

    private QueryAlignmentResponse generateStructureResponse(Structure structure, double precision) {
        String cacheKey = String.format("%s_%s", structure.getPDBCode(), precision);
        QueryAlignmentResponse alignmentResponse = cacheService.findQueryAlignmentResponse(cacheKey);
        if (alignmentResponse != null) {
            return alignmentResponse;
        }

        int pageNumber = 0;
        Page<Motif> motifs = motifService.queryByEcNumber(null, pageNumber);
        log.info(String.format("Aligning the structure %s with %d motifs.", structure.getPDBCode(),
                               motifs.getTotalElements()
        ));

        alignmentResponse = new QueryAlignmentResponse();
        alignmentResponse.setCacheKey(cacheKey);

        // Align structures with motifs from the database
        while (motifs.hasContent()) {
            QueryResponseData queryResponseData = new QueryResponseData(structure);
            motifs.stream()
                .parallel()
                .forEach(motif -> {
                    Alignment alignment = alignActiveSites(
                        structure, motif, ProteinUtils.queryPdb(motif.getPdbId()), precision);
                    if (alignment != null) {
                        queryResponseData.addSuccessfulEntry(motif, alignment);
                    }
                });
            alignmentResponse.addQueryResponseData(queryResponseData);
            pageNumber++;
            motifs = motifService.queryByEcNumber(null, pageNumber);
        }
        return alignmentResponse;
    }

    /**
     * Attempt to align the active site of a motif with a structure
     *
     * @param queryStructure  :  structure to align
     * @param motif           :  motif that we search for in the structure
     * @param motifStructure  :  structure of motif for comparison
     * @param precisionFactor :  precision factor modifier
     * @return an alignment (if one exists) or null (if none found)
     */
    public Alignment alignActiveSites(Structure queryStructure, Motif motif, Structure motifStructure, double precisionFactor) {
        Map<Residue, List<Group>> residueMap = motif.runQueries(queryStructure, precisionFactor);
        List<Map<Residue, Group>> permutations = findAllPermutations(residueMap);
        Map<Residue, Group> residueMapping = findBestPermutation(motif, motifStructure, permutations);

        Set<Group> found = new HashSet<>();
        List<Residue> activeSiteResidueList = new ArrayList<>();
        List<Group> alignedResidueList = new ArrayList<>();

        for (Residue _residue : motif.getActiveSiteResidues()) {
            Group group = residueMapping.get(_residue);
            if (group != null) {
                if (!found.contains(group)) {
                    activeSiteResidueList.add(_residue);
                    alignedResidueList.add(group);
                    found.add(group);
                }
            }
        }

        List<Group> alignedResidueListSorted = new ArrayList<>(alignedResidueList);
        alignedResidueListSorted.sort(Comparator.comparingInt(o -> o.getResidueNumber()
            .getSeqNum()));
        String alignmentString = AlignmentUtils.groupListToResString(alignedResidueListSorted);
        String motifResString = AlignmentUtils.residueListToResString(motif.getActiveSiteResidues());

        int distance = AlignmentUtils.levensteinDistance(alignmentString, motifResString);

        if (alignedResidueList.size() > 1 && acceptableDistance(activeSiteResidueList.size(), distance)) {
            Alignment alignment = new Alignment();
            alignment.setActiveSiteResidues(activeSiteResidueList);
            alignment.setMotifPdbId(motif.getPdbId());
            alignment.setLevenstein(distance);
            alignment.setAlignedResidues(alignedResidueList.stream()
                                             .map(Residue::fromGroup)
                                             .collect(Collectors.toList()));
            alignment.setRmsd(rmsd(motifStructure, motif.getActiveSiteResidues(), alignedResidueList));
            alignment.setEcNumber(motif.getEcNumber());
            return alignment;
        }

        return null;
    }

    /**
     * This is a simple heuristic for deciding whether or not our alignment
     * has an acceptable levenstein distance.
     *
     * @param activeSiteSize: number of residues in the active site
     * @param distance:       calculated levenstein distance
     * @return true if the distance is acceptable
     */
    private boolean acceptableDistance(int activeSiteSize, int distance) {
        if (activeSiteSize == 2) {
            return false;
        } else if (activeSiteSize >= 3) {
            return distance == 0;
        }
        return distance <= 1;
    }

    /**
     * Calculate the RMSD (root mean squared distance) between atoms in an alignment.
     * Tl;dr: average atom distance of an alignment. The lower it is, the better the alignment.
     * <p>
     * We do this by applying an SVD superposition and finding the RMSD of that
     *
     * @param motifStructure:     Structure object for the motif
     * @param activeSiteResidues: List of active site residues
     * @param alignedResidues:    list of residues aligned with active site
     * @return a floating point value representing the RMSD of the superposition alignment of the active site of
     * the motif and the aligned residues.
     */
    private double rmsd(Structure motifStructure, List<Residue> activeSiteResidues, List<Group> alignedResidues) {
        List<Group> activeSite = new ArrayList<>();
        for (Residue residue : activeSiteResidues) {
            activeSite.add(StructureUtils.getResidue(motifStructure, residue.getResidueName(), residue.getResidueId()));
        }

        Point3d[] activeSitePoints = atomListFromResidueSet(activeSite);
        Point3d[] alignedResiduePoints = atomListFromResidueSet(alignedResidues);
        SuperPositionSVD superPositionSVD = new SuperPositionSVD(false);
        if (activeSitePoints.length != alignedResiduePoints.length) {
            return -1;
        }
        return superPositionSVD.getRmsd(activeSitePoints, alignedResiduePoints);
    }

    /**
     * Get atoms as 3d points from a set of biojava groups (residues)
     *
     * @param residues: residues to get atoms from
     * @return the atoms in the residues presented as an array of 3d points representing their locations
     */
    private Point3d[] atomListFromResidueSet(List<Group> residues) {
        List<Atom> atoms = new ArrayList<>();
        for (Group residue : residues) {
            atoms.addAll(getAtomsFromGroup(residue));
        }
        List<Point3d> points = new ArrayList<>();
        for (Atom atom : atoms) {
            Point3d coordsAsPoint3d = atom.getCoordsAsPoint3d();
            points.add(coordsAsPoint3d);
        }
        Point3d[] point3ds = new Point3d[points.size()];
        return points.toArray(point3ds);
    }

    /**
     * Get all atoms from a group (residue). We filter some atoms out, specifically
     * hydrogen and backbone atoms. This is so we can reliably use the atoms to calculate RMSD
     *
     * @param group: biojava group to get atoms from
     * @return filtered list of atoms from the group
     */
    private List<Atom> getAtomsFromGroup(Group group) {
        List<Atom> atoms = group.getAtoms();
        atoms = atoms.stream()
            .filter(atom ->
                        //Remove hydrogen atoms
                        !atom.getName()
                            .contains("H") &&
                            //These ones also get in the way
                            !atom.getName()
                                .startsWith("D") &&
                            //Remove backbone atoms
                            !atom.getName()
                                .equals("N") &&
                            !atom.getName()
                                .equals("C") &&
                            !atom.getName()
                                .equals("O"))
            .collect(Collectors.toList());
        return atoms;
    }

    /**
     * Finds all permutations of a residue mapping
     *
     * @param residueMapping: mapping of residue to its candidate matches for alignment
     * @return a list of mappings from residue to group where each map is a possible alignment permutation
     */
    private List<Map<Residue, Group>> findAllPermutations(Map<Residue, List<Group>> residueMapping) {
        List<Map<Residue, Group>> result = new ArrayList<>();
        List<List<Group>> groupLists = new ArrayList<>(residueMapping.values());
        List<List<Group>> results = cartesianProduct(groupLists);
        List<Residue> residues = new ArrayList<>(residueMapping.keySet());

        for (List<Group> res : results) {
            Map<Residue, Group> mapping = new HashMap<>();
            for (int i = 0; i < res.size(); i++) {
                mapping.put(residues.get(i), res.get(i));
            }
            result.add(mapping);
        }
        return result;
    }

    /**
     * Finds the best permutation of a list of permutations of matches to an active site
     * of a motif
     *
     * @param motif:          motif active site information
     * @param motifStructure: biojava structure representing the motif
     * @param permutations:   data structure containing your permutations
     * @return best fit permutation for alignment. We calculate this by checking if the permutation fits
     * the constraint we have for levenstein distance and then find the one with minimum RMSD that
     * fits this requirement.
     */
    private Map<Residue, Group> findBestPermutation(Motif motif, Structure motifStructure, List<Map<Residue, Group>> permutations) {
        if (permutations.size() == 1) {
            return permutations.get(0);
        }
        double min_rmsd = Double.MAX_VALUE;
        Map<Residue, Group> best_match = new HashMap<>();
        for (Map<Residue, Group> permutation : permutations) {
            List<Group> alignmentSeq = new ArrayList<>(permutation.values());
            alignmentSeq.sort(Comparator.comparingInt(o -> o.getResidueNumber()
                .getSeqNum()));
            String alignmentString = AlignmentUtils.groupListToResString(alignmentSeq);
            String motifResString = AlignmentUtils.residueListToResString(motif.getActiveSiteResidues());

            int distance = AlignmentUtils.levensteinDistance(alignmentString, motifResString);

            if (acceptableDistance(motif.getActiveSiteResidues()
                                       .size(), distance)) {
                double rmsd = rmsd(motifStructure, motif.getActiveSiteResidues(), alignmentSeq);
                if (rmsd != -1 && rmsd < min_rmsd) {
                    min_rmsd = rmsd;
                    best_match = permutation;
                }
            }
        }
        return best_match;
    }

    /**
     * Finds the cartesian product of multiple lists.
     * This is used to get all combinations of a set of lists
     * for example:
     * <p>
     * consider the following three lists:
     * [a]
     * [b c]
     * [d e]
     * <p>
     * the total combinations in the cartesian product would be:
     * [a b d]
     * [a b e]
     * [a c d]
     * [a c e]
     */
    private <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<T>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }
}
