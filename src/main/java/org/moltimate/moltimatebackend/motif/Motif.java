package org.moltimate.moltimatebackend.motif;

import lombok.Builder;
import lombok.Data;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.Structure.StructureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
public class Motif {
    Map<String, List<MotifSelection>> selectionQueries;
    List<String> residues;
    int[] ecClass;
    String name;
    List<String> activeSite;

    public Map<String, List<Group>> runQueries(Structure pdb) {
        return runQueries(pdb, 1d);
    }

    /**
     * Apply motif to pdb structure
     *
     * @param pdb:             structure of pdb to run alignment on
     * @param precisionFactor: precision factor of search
     * @return a list of amino acids that match the set of queries
     */
    public Map<String, List<Group>> runQueries(Structure pdb, double precisionFactor) {
        //TODO: Refactor
        Map<String, List<Group>> residues = new HashMap<>();
        selectionQueries.keySet().forEach(residueName -> {
            HashSet<Atom> atoms = new HashSet<>();
            HashMap<Group, Integer> groupCount = new HashMap<>();
            selectionQueries.get(residueName).forEach(query -> {
                List<Atom> atomsFound = StructureUtils.runQuery(
                        pdb,
                        query.atom1Name,
                        query.atom2Name,
                        query.residue1Name,
                        query.residue2Name,
                        query.distance,
                        precisionFactor
                );
                atoms.addAll(atomsFound);
                List<Group> groupsMatchingQuery = new ArrayList<>();
                HashSet<String> foundNames = new HashSet<>();
                atomsFound.forEach(atom -> {
                    if (!foundNames.contains(atom.getGroup().toString())) {
                        foundNames.add(atom.getGroup().toString());
                        groupsMatchingQuery.add(atom.getGroup());
                    }
                });
                groupsMatchingQuery.forEach(residue -> groupCount.merge(residue, 1, (a, b) -> a + b));
            });
            List<Group> candidateGroups = groupCount.keySet().stream()
                                                    .filter(group -> groupCount.get(group) == selectionQueries.get(
                                                            residueName).size())
                                                    .collect(Collectors.toList());
            candidateGroups.forEach(candidate -> {
                String chainName = candidate.getChainId();
                residues.computeIfAbsent(chainName, k -> new ArrayList<>());
                residues.get(chainName).add(candidate);
            });
        });
        return residues;
    }
}
