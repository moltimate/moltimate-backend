package org.moltimate.moltimatebackend.util;

import org.biojava.nbio.structure.*;
import org.biojava.nbio.structure.io.mmcif.model.ChemComp;
import org.junit.Assert;
import org.junit.Test;
import org.moltimate.moltimatebackend.model.Residue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestAlignmentUtils {

	@Test
	public void testAlignmentUtilsInstance(){
		// Included for code coverage
		AlignmentUtils CuT = new AlignmentUtils();
		Assert.assertNotNull(CuT);
	}

	@Test
	public void testGroupListToResString() {
		List<Group> groups = new ArrayList<>();
		groups.add( makeGroup( "ALA" ) );
		groups.add( makeGroup( "ARG" ) );
		groups.add( makeGroup( "ASN" ) );
		String testString = AlignmentUtils.groupListToResString( groups );
		Assert.assertEquals( "ABC", testString );
	}

	@Test
	public void testResidueListToResString() {
		List<Residue> residues = new ArrayList<>();
		residues.add( makeResidue( "ALA" ) );
		residues.add( makeResidue( "ARG" ) );
		residues.add( makeResidue( "ASN" ) );
		String testString = AlignmentUtils.residueListToResString( residues );
		Assert.assertEquals( "ABC", testString );
	}

	@Test
	public void testLevenshteinDistance() {
		Assert.assertEquals( 2, AlignmentUtils.levenshteinDistance( "test", "t3$t" ) );
		Assert.assertEquals( 4, AlignmentUtils.levenshteinDistance( "test", "ok" ) );
		Assert.assertEquals( 7, AlignmentUtils.levenshteinDistance( "ligand", "protein" ) );
		Assert.assertEquals( 0, AlignmentUtils.levenshteinDistance( "test", "test" ) );
	}

	private Group makeGroup( String pdbName ) {
		return new Group() {
			@Override
			public int size() {
				return 0;
			}

			@Override
			public boolean has3D() {
				return false;
			}

			@Override
			public void setPDBFlag(boolean b) {

			}

			@Override
			public GroupType getType() {
				return null;
			}

			@Override
			public void addAtom(Atom atom) {

			}

			@Override
			public List<Atom> getAtoms() {
				return null;
			}

			@Override
			public void setAtoms(List<Atom> list) {

			}

			@Override
			public void clearAtoms() {

			}

			@Override
			public Atom getAtom(String s) {
				return null;
			}

			@Override
			public Atom getAtom(int i) {
				return null;
			}

			@Override
			public boolean hasAtom(String s) {
				return false;
			}

			@Override
			public String getPDBName() {
				return pdbName;
			}

			@Override
			public void setPDBName(String s) {

			}

			@Override
			public boolean hasAminoAtoms() {
				return false;
			}

			@Override
			public boolean isPolymeric() {
				return false;
			}

			@Override
			public boolean isAminoAcid() {
				return false;
			}

			@Override
			public boolean isNucleotide() {
				return false;
			}

			@Override
			public void setProperties(Map<String, Object> map) {

			}

			@Override
			public Map<String, Object> getProperties() {
				return null;
			}

			@Override
			public void setProperty(String s, Object o) {

			}

			@Override
			public Object getProperty(String s) {
				return null;
			}

			@Override
			public Iterator<Atom> iterator() {
				return null;
			}

			@Override
			public void setChain(Chain chain) {

			}

			@Override
			public Chain getChain() {
				return null;
			}

			@Override
			public ResidueNumber getResidueNumber() {
				return null;
			}

			@Override
			public void setResidueNumber(ResidueNumber residueNumber) {

			}

			@Override
			public void setResidueNumber(String s, Integer integer, Character character) {

			}

			@Override
			public String getChainId() {
				return null;
			}

			@Override
			public void setChemComp(ChemComp chemComp) {

			}

			@Override
			public ChemComp getChemComp() {
				return null;
			}

			@Override
			public boolean hasAltLoc() {
				return false;
			}

			@Override
			public List<Group> getAltLocs() {
				return null;
			}

			@Override
			public void addAltLoc(Group group) {

			}

			@Override
			public boolean isWater() {
				return false;
			}

			@Override
			public Group getAltLocGroup(Character character) {
				return null;
			}

			@Override
			public void trimToSize() {

			}

			@Override
			public String toSDF() {
				return null;
			}

			@Override
			public boolean isHetAtomInFile() {
				return false;
			}

			@Override
			public void setHetAtomInFile(boolean b) {

			}

			@Override
			public Object clone() {
				return null;
			}
		};
	}

	private Residue makeResidue( String name ) {
		Residue residue = new Residue();
		residue.setResidueName( name );
		return residue;
	}
}
