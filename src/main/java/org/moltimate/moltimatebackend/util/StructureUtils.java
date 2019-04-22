package org.moltimate.moltimatebackend.util;

import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.rcsb.RCSBDescription;
import org.biojava.nbio.structure.rcsb.RCSBDescriptionFactory;
import org.biojava.nbio.structure.rcsb.RCSBPolymer;
import org.moltimate.moltimatebackend.constant.EcNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StructureUtils {

    /**
     * We have an error of 2 angstroms in any direction, so our margin is 2 in all directions
     * This is used to check that not only is the distance less than the query amount, but also that it is similar
     * We use the norm of the error vector to find the acceptable threshold
     * With precision factor, we make a similar vector and multiply the previous norm
     * by the norm of the new precision factor vector
     */
    private static final double distanceErrorMargin = 2d;

    /**
     * Get Residue from a structure
     *
     * @param structure:     structure to search for residue in
     * @param residueName:   three letter name of the residue (ex: ASP)
     * @param residueNumber: residue number
     * @return residue if found, null otherwise
     */
    public static Group getResidue(Structure structure, String residueName, String residueNumber) {
        for (Chain chain : structure.getChains()) {
            for (Group group : chain.getAtomGroups(GroupType.AMINOACID)) {
                if (group.getPDBName()
                    .equalsIgnoreCase(residueName)
                    && group.getResidueNumber()
                    .toString()
                    .equals(String.valueOf(residueNumber))) {
                    return group;
                }
            }
        }
        return null;
    }

    /**
     * Estimate location of residue by averaging the locations of each
     * atom in the residue
     *
     * @param residue: residue to find location of
     * @return x, y, z coordinates of the residue
     */
    private static double[] getResidueLocation(Group residue) {
        List<Atom> atoms = residue.getAtoms();
        double[] location = new double[3];
        for (Atom atom : atoms) {
            double[] coords = atom.getCoords();
            location[0] += coords[0];
            location[1] += coords[1];
            location[2] += coords[2];
        }

        location[0] = location[0] / atoms.size();
        location[1] = location[1] / atoms.size();
        location[2] = location[2] / atoms.size();

        return location;
    }

    /**
     * Find all amino acids of a certain type (ex: asp or his) inside a structure
     *
     * @param structure:   structure to search for amino acid in
     * @param residueName: name of residue to search for
     * @return list of residues with given name inside structure
     */
    private static List<Group> getResiduesByType(Structure structure, String residueName) {
        ArrayList<Group> results = new ArrayList<>();
        for (Chain chain : structure.getChains()) {
            for (Group residue : chain.getAtomGroups(GroupType.AMINOACID)) {
                if (residue.getPDBName()
                    .equals(residueName)) {
                    results.add(residue);
                }
            }
        }
        return results;
    }

    /**
     * Get all atoms of a certain type inside a structure
     *
     * @param structure: structure to search for atoms in
     * @param atomType:  type of atom to search for in structure
     * @return a list of atoms in the structure matching the specified type
     */
    private static List<Atom> getAtomByType(Structure structure, String atomType) {
        ArrayList<Atom> atoms = new ArrayList<>();
        for (Chain chain : structure.getChains()) {
            for (Group group : chain.getAtomGroups(GroupType.AMINOACID)) {
                atoms.addAll(getAtomByType(group, atomType));
            }
        }
        return atoms;
    }

    /**
     * Get all atoms of a certain type inside a residue
     *
     * @param residue:  residue to search for atoms in
     * @param atomType: type of atom to search for in structure
     * @return a list of atoms in the structure matching the specified type
     */
    private static List<Atom> getAtomByType(Group residue, String atomType) {
        List<Atom> list = new ArrayList<>();
        for (Atom atom : residue.getAtoms()) {
            if (atom.getName()
                .equals(atomType)) {
                list.add(atom);
            }
        }
        return list;
    }

    /**
     * Creates a map of residue -> location where each location is the center of each residue
     *
     * @param residues: list of residues to create locations for
     * @return map of residue -> double[] where each array is a 3 dimensional coordinate
     * representing the center of that residue.
     */
    private static Map<Group, double[]> residueToLocationMap(List<Group> residues) {
        HashMap<Group, double[]> locationMap = new HashMap<>();
        for (Group residue : residues) {
            locationMap.put(residue, getResidueLocation(residue));
        }
        return locationMap;
    }

    public static double rmsd(Atom atom1, Atom atom2) {
        return rmsd(atom1.getCoords(), atom2.getCoords());
    }

    /**
     * Root mean squared distance between two points
     *
     * @param point1: first point
     * @param point2: second point
     * @return root mean squared distance between the two points
     */
    private static double rmsd(double[] point1, double[] point2) {
        double distance = 0.0;
        for (int i = 0; i < point1.length; i++) {
            distance += (point1[i] - point2[i]) * (point1[i] - point2[i]);
        }
        return Math.sqrt(distance);
    }

    public static List<Atom> runQuery(Structure structure,
                                      String atom1Name, String atom2Name,
                                      String residue1Name, String residue2Name,
                                      double distance, double precision) {
        ArrayList<Atom> results = new ArrayList<>();

        List<Group> residue1List = getResiduesByType(structure, residue1Name);
        List<Group> residue2List = getResiduesByType(structure, residue2Name);
        List<Atom> atom1List = new ArrayList<>();
        List<Atom> atom2List = new ArrayList<>();
        for (Group group : residue1List) {
            atom1List.addAll(getAtomByType(group, atom1Name));
        }
        for (Group residue : residue2List) {
            atom2List.addAll(getAtomByType(residue, atom2Name));
        }

        for (Atom atom1 : atom1List) {
            for (Atom atom2 : atom2List) {
                double _rmsd = rmsd(atom1, atom2);
                double _errorMargin = l2Norm(
                    new double[]{distanceErrorMargin, distanceErrorMargin, distanceErrorMargin});
                double _precisionMod = l2Norm(new double[]{precision, precision, precision});
                if (atom1.getGroup() != atom2.getGroup()
                    && (_rmsd < distance * precision)
                    && (Math.abs(_rmsd - (distance * precision)) < (_errorMargin * _precisionMod))) {
                    results.add(atom1);
                }
            }
        }

        return results;
    }

    public static String residueName(Group group) {
        return group.getPDBName() + " " + group.getResidueNumber()
            .toString();
    }

    public static String ecNumber(Structure structure) {
        RCSBDescription description = RCSBDescriptionFactory.get(structure.getPDBCode());
        for(RCSBPolymer polymer: description.getPolymers()){
            if(polymer.getEnzClass() != null){
                return polymer.getEnzClass();
            }
        }
        return EcNumber.UNKNOWN;
    }

    private static double l2Norm(double[] w2v) {
        double norm = 0.0;
        for (Double x : w2v) {
            norm += x * x;
        }
        return Math.sqrt(norm);
    }
}
