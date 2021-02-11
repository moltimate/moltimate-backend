package org.moltimate.moltimatebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.util.StructureUtils;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Motif {

    @Id
    @NotNull
    private String pdbId;

    @NotNull
    private String ecNumber;

    @NotNull
    @ElementCollection
    private Map<String, ResidueQuerySet> selectionQueries;

    @Valid
    @NotNull
    @ElementCollection
    private List<Residue> activeSiteResidues;

    private Residue getResidueByName(String residueName) {
        for (Residue res : activeSiteResidues) {
            if (res.getIdentifier().equals(residueName)) {
                return res;
            }
        }
        return null;
    }

    public Map<Residue, List<Group>> runQueries(Structure pdb) {
        return runQueries(pdb, 1d);
    }

    /**
     * Apply motif to pdb structure
     *
     * @param pdb:             structure of pdb to run alignment on
     * @param precisionFactor: precision factor of search
     * @return a list of amino acids that match the set of queries
     */
    public Map<Residue, List<Group>> runQueries(Structure pdb, double precisionFactor) {
        //TODO: Refactor
        Map<Residue, List<Group>> residues = new HashMap<>();
        for (Map.Entry<String, ResidueQuerySet> entry : selectionQueries.entrySet()) {
            String residueName = entry.getKey();
            Residue residueValue = getResidueByName(residueName);
            HashMap<Group, Integer> groupCount = new HashMap<>();
            for (MotifSelection query : selectionQueries.get(residueName).getSelections()) {
                List<Atom> atomsFound = StructureUtils.runQuery(
                        pdb,
                        query.getAtomType1(),
                        query.getAtomType2(),
                        query.getResidueName1(),
                        query.getResidueName2(),
                        query.getDistance(),
                        precisionFactor
                );
                Set<Group> groupsMatchingQuery = new HashSet<>();
                HashSet<String> foundNames = new HashSet<>();
                for (Atom atom : atomsFound) {
                    if (!foundNames.contains(atom.getGroup().toString())) {
                        foundNames.add(atom.getGroup().toString());
                        groupsMatchingQuery.add(atom.getGroup());
                    }
                }
                for (Group residue : groupsMatchingQuery) {
                    groupCount.merge(residue, 1, (a, b) -> a + b);
                }
            }
            List<Group> candidateGroups = new ArrayList<>();
            for (Group group : groupCount.keySet()) {
                if (groupCount.get(group) == selectionQueries.get(residueName).getSelections().size()) {
                    candidateGroups.add(group);
                }
            }
            for (Group candidate : candidateGroups) {
                residues.computeIfAbsent(residueValue, k -> new ArrayList<>());
                residues.get(residueValue).add(candidate);
            }
        }
        return residues;
    }
}
