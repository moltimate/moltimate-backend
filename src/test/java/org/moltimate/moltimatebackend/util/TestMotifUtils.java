package org.moltimate.moltimatebackend.util;

import org.biojava.nbio.core.sequence.template.Sequence;
import org.biojava.nbio.structure.*;
import org.biojava.nbio.structure.align.util.AtomCache;
import org.biojava.nbio.structure.io.mmcif.model.ChemComp;
import org.junit.Assert;
import org.junit.Test;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;

import javax.vecmath.Point3d;
import java.io.IOException;
import java.util.*;

public class TestMotifUtils {

	@Test
	public void testGenerateMotif() {
		Motif motif = MotifUtils.generateMotif("1a0j", "3.4.21.4", new Structure() {
			private List<Chain> chains = Collections.singletonList(new Chain() {
				@Override
				public void addGroup(Group group) {

				}

				@Override
				public String getId() {
					return null;
				}

				@Override
				public void setId(String s) {

				}

				@Override
				public void setName(String s) {

				}

				@Override
				public String getName() {
					return null;
				}

				@Override
				public Group getAtomGroup(int i) {
					return null;
				}

				@Override
				public Group getSeqResGroup(int i) {
					return null;
				}

				@Override
				public List<Group> getAtomGroups() {
					return null;
				}

				@Override
				public void setAtomGroups(List<Group> list) {

				}

				@Override
				public List<Group> getAtomGroups(GroupType groupType) {
					return Collections.singletonList(group);
				}

				@Override
				public Group getGroupByPDB(ResidueNumber residueNumber) throws StructureException {
					return null;
				}

				@Override
				public Group[] getGroupsByPDB(ResidueNumber residueNumber, ResidueNumber residueNumber1) throws StructureException {
					return new Group[0];
				}

				@Override
				public Group[] getGroupsByPDB(ResidueNumber residueNumber, ResidueNumber residueNumber1, boolean b) throws StructureException {
					return new Group[0];
				}

				@Override
				public int getAtomLength() {
					return 0;
				}

				@Override
				public int getSeqResLength() {
					return 0;
				}

				@Override
				public void setEntityInfo(EntityInfo entityInfo) {

				}

				@Override
				public EntityInfo getEntityInfo() {
					return null;
				}

				@Override
				public void setChainID(String s) {

				}

				@Override
				public String getChainID() {
					return null;
				}

				@Override
				public String getInternalChainID() {
					return null;
				}

				@Override
				public void setInternalChainID(String s) {

				}

				@Override
				public Sequence<?> getBJSequence() {
					return null;
				}

				@Override
				public String getAtomSequence() {
					return null;
				}

				@Override
				public String getSeqResSequence() {
					return null;
				}

				@Override
				public void setSwissprotId(String s) {

				}

				@Override
				public String getSwissprotId() {
					return null;
				}

				@Override
				public List<Group> getSeqResGroups(GroupType groupType) {
					return null;
				}

				@Override
				public List<Group> getSeqResGroups() {
					return null;
				}

				@Override
				public void setSeqResGroups(List<Group> list) {

				}

				@Override
				public void setParent(Structure structure) {

				}

				@Override
				public void setStructure(Structure structure) {

				}

				@Override
				public Structure getParent() {
					return null;
				}

				@Override
				public Structure getStructure() {
					return null;
				}

				@Override
				public List<Group> getAtomLigands() {
					return null;
				}

				@Override
				public String toPDB() {
					return null;
				}

				@Override
				public String toMMCIF() {
					return null;
				}

				@Override
				public void setSeqMisMatches(List<SeqMisMatch> list) {

				}

				@Override
				public List<SeqMisMatch> getSeqMisMatches() {
					return null;
				}

				@Override
				public EntityType getEntityType() {
					return null;
				}

				@Override
				public boolean isWaterOnly() {
					return false;
				}

				@Override
				public boolean isPureNonPolymer() {
					return false;
				}

				@Override
				public GroupType getPredominantGroupType() {
					return null;
				}

				@Override
				public boolean isProtein() {
					return false;
				}

				@Override
				public boolean isNucleicAcid() {
					return false;
				}

				@Override
				public Object clone() {
					return null;
				}
			});

			private Group group = new Group() {
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
					return Arrays.asList(new Atom() {
						@Override
						public void setName(String s) {

						}

						@Override
						public String getName() {
							return "N";
						}

						@Override
						public void setElement(Element element) {

						}

						@Override
						public Element getElement() {
							return Element.N;
						}

						@Override
						public void setPDBserial(int i) {

						}

						@Override
						public int getPDBserial() {
							return 0;
						}

						@Override
						public void setCoords(double[] doubles) {

						}

						@Override
						public double[] getCoords() {
							return new double[0];
						}

						@Override
						public Point3d getCoordsAsPoint3d() {
							return null;
						}

						@Override
						public void setX(double v) {

						}

						@Override
						public void setY(double v) {

						}

						@Override
						public void setZ(double v) {

						}

						@Override
						public double getX() {
							return 0;
						}

						@Override
						public double getY() {
							return 0;
						}

						@Override
						public double getZ() {
							return 0;
						}

						@Override
						public void setAltLoc(Character character) {

						}

						@Override
						public Character getAltLoc() {
							return null;
						}

						@Override
						public void setOccupancy(float v) {

						}

						@Override
						public float getOccupancy() {
							return 0;
						}

						@Override
						public void setTempFactor(float v) {

						}

						@Override
						public float getTempFactor() {
							return 0;
						}

						@Override
						public void setGroup(Group group) {

						}

						@Override
						public Group getGroup() {
							return null;
						}

						@Override
						public void addBond(Bond bond) {

						}

						@Override
						public List<Bond> getBonds() {
							return null;
						}

						@Override
						public void setBonds(List<Bond> list) {

						}

						@Override
						public boolean hasBond(Atom atom) {
							return false;
						}

						@Override
						public short getCharge() {
							return 0;
						}

						@Override
						public void setCharge(short i) {

						}

						@Override
						public String toPDB() {
							return null;
						}

						@Override
						public void toPDB(StringBuffer stringBuffer) {

						}

						@Override
						public Object clone() {
							return null;
						}
					}, new Atom() {
						@Override
						public void setName(String s) {

						}

						@Override
						public String getName() {
							return "CB";
						}

						@Override
						public void setElement(Element element) {

						}

						@Override
						public Element getElement() {
							return Element.C;
						}

						@Override
						public void setPDBserial(int i) {

						}

						@Override
						public int getPDBserial() {
							return 0;
						}

						@Override
						public void setCoords(double[] doubles) {

						}

						@Override
						public double[] getCoords() {
							return new double[0];
						}

						@Override
						public Point3d getCoordsAsPoint3d() {
							return null;
						}

						@Override
						public void setX(double v) {

						}

						@Override
						public void setY(double v) {

						}

						@Override
						public void setZ(double v) {

						}

						@Override
						public double getX() {
							return 0;
						}

						@Override
						public double getY() {
							return 0;
						}

						@Override
						public double getZ() {
							return 0;
						}

						@Override
						public void setAltLoc(Character character) {

						}

						@Override
						public Character getAltLoc() {
							return null;
						}

						@Override
						public void setOccupancy(float v) {

						}

						@Override
						public float getOccupancy() {
							return 0;
						}

						@Override
						public void setTempFactor(float v) {

						}

						@Override
						public float getTempFactor() {
							return 0;
						}

						@Override
						public void setGroup(Group group) {

						}

						@Override
						public Group getGroup() {
							return null;
						}

						@Override
						public void addBond(Bond bond) {

						}

						@Override
						public List<Bond> getBonds() {
							return null;
						}

						@Override
						public void setBonds(List<Bond> list) {

						}

						@Override
						public boolean hasBond(Atom atom) {
							return false;
						}

						@Override
						public short getCharge() {
							return 0;
						}

						@Override
						public void setCharge(short i) {

						}

						@Override
						public String toPDB() {
							return null;
						}

						@Override
						public void toPDB(StringBuffer stringBuffer) {

						}

						@Override
						public Object clone() {
							return null;
						}
					});
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
					return "Asp";
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
					return new ResidueNumber( "1a0j", 7, null );
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

			@Override
			public Structure clone() {
				return this;
			}

			@Override
			public void setPDBCode(String s) {

			}

			@Override
			public String getPDBCode() {
				return "1a0j";
			}

			@Override
			public void setName(String s) {

			}

			@Override
			public String getName() {
				return "1a0j";
			}

			@Override
			public StructureIdentifier getStructureIdentifier() {
				return new StructureIdentifier() {
					@Override
					public String getIdentifier() {
						return "1a0j";
					}

					@Override
					public Structure loadStructure(AtomCache atomCache) throws StructureException, IOException {
						return null;
					}

					@Override
					public SubstructureIdentifier toCanonical() throws StructureException {
						return null;
					}

					@Override
					public Structure reduce(Structure structure) throws StructureException {
						return null;
					}
				};
			}

			@Override
			public void setStructureIdentifier(StructureIdentifier structureIdentifier) {

			}

			@Override
			public int size() {
				return 0;
			}

			@Override
			public int size(int i) {
				return 0;
			}

			@Override
			public int nrModels() {
				return 0;
			}

			@Override
			public boolean isNmr() {
				return false;
			}

			@Override
			public boolean isCrystallographic() {
				return false;
			}

			@Override
			public void addModel(List<Chain> list) {

			}

			@Override
			public void setModel(int i, List<Chain> list) {

			}

			@Override
			public List<Chain> getModel(int i) {
				return chains;
			}

			@Override
			public List<Chain> getChains() {
				return chains;
			}

			@Override
			public void setChains(List<Chain> list) {

			}

			@Override
			public List<Chain> getChains(int i) {
				return chains;
			}

			@Override
			public void setChains(int i, List<Chain> list) {

			}

			@Override
			public List<Chain> getPolyChains() {
				return chains;
			}

			@Override
			public List<Chain> getPolyChains(int i) {
				return chains;
			}

			@Override
			public List<Chain> getNonPolyChains() {
				return chains;
			}

			@Override
			public List<Chain> getNonPolyChains(int i) {
				return chains;
			}

			@Override
			public List<Chain> getWaterChains() {
				return chains;
			}

			@Override
			public List<Chain> getWaterChains(int i) {
				return chains;
			}

			@Override
			public void addChain(Chain chain) {

			}

			@Override
			public void addChain(Chain chain, int i) {

			}

			@Override
			public Chain getChainByIndex(int i) {
				return chains.get( i );
			}

			@Override
			public Chain getChainByIndex(int i, int i1) {
				return chains.get( i );
			}

			@Override
			public Chain findChain(String s) throws StructureException {
				return chains.get( 0 );
			}

			@Override
			public Chain findChain(String s, int i) throws StructureException {
				return chains.get( 0 );
			}

			@Override
			public boolean hasChain(String s) {
				return false;
			}

			@Override
			public boolean hasNonPolyChain(String s) {
				return false;
			}

			@Override
			public boolean hasPdbChain(String s) {
				return false;
			}

			@Override
			public Group findGroup(String s, String s1) throws StructureException {
				return group;
			}

			@Override
			public Group findGroup(String s, String s1, int i) throws StructureException {
				return group;
			}

			@Override
			public Chain getChainByPDB(String s) throws StructureException {
				return chains.get( 0 );
			}

			@Override
			public Chain getChainByPDB(String s, int i) throws StructureException {
				return chains.get( 0 );
			}

			@Override
			public Chain getChain(String s) {
				return chains.get( 0 );
			}

			@Override
			public Chain getChain(String s, int i) {
				return chains.get( 0 );
			}

			@Override
			public Chain getPolyChain(String s) {
				return chains.get( 0 );
			}

			@Override
			public Chain getPolyChain(String s, int i) {
				return chains.get( 0 );
			}

			@Override
			public Chain getPolyChainByPDB(String s) {
				return chains.get( 0 );
			}

			@Override
			public Chain getPolyChainByPDB(String s, int i) {
				return chains.get( 0 );
			}

			@Override
			public Chain getNonPolyChain(String s) {
				return chains.get( 0 );
			}

			@Override
			public Chain getNonPolyChain(String s, int i) {
				return chains.get( 0 );
			}

			@Override
			public List<Chain> getNonPolyChainsByPDB(String s) {
				return chains;
			}

			@Override
			public List<Chain> getNonPolyChainsByPDB(String s, int i) {
				return chains;
			}

			@Override
			public Chain getWaterChain(String s) {
				return chains.get( 0 );
			}

			@Override
			public Chain getWaterChain(String s, int i) {
				return chains.get( 0 );
			}

			@Override
			public Chain getWaterChainByPDB(String s) {
				return chains.get( 0 );
			}

			@Override
			public Chain getWaterChainByPDB(String s, int i) {
				return chains.get( 0 );
			}

			@Override
			public String toPDB() {
				return "1a0j";
			}

			@Override
			public String toMMCIF() {
				return "1a0j";
			}

			@Override
			public void setEntityInfos(List<EntityInfo> list) {

			}

			@Override
			public List<EntityInfo> getEntityInfos() {
				return null;
			}

			@Override
			public void addEntityInfo(EntityInfo entityInfo) {

			}

			@Override
			public void setDBRefs(List<DBRef> list) {

			}

			@Override
			public List<DBRef> getDBRefs() {
				return null;
			}

			@Override
			public EntityInfo getCompoundById(int i) {
				return null;
			}

			@Override
			public EntityInfo getEntityById(int i) {
				return null;
			}

			@Override
			public PDBHeader getPDBHeader() {
				return null;
			}

			@Override
			public boolean hasJournalArticle() {
				return false;
			}

			@Override
			public JournalArticle getJournalArticle() {
				return null;
			}

			@Override
			public void setJournalArticle(JournalArticle journalArticle) {

			}

			@Override
			public List<Bond> getSSBonds() {
				return null;
			}

			@Override
			public void setSSBonds(List<Bond> list) {

			}

			@Override
			public void addSSBond(Bond bond) {

			}

			@Override
			public void setPDBHeader(PDBHeader pdbHeader) {

			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public void setId(Long aLong) {

			}

			@Override
			public void setSites(List<Site> list) {

			}

			@Override
			public List<Site> getSites() {
				return null;
			}

			@Override
			public void setBiologicalAssembly(boolean b) {

			}

			@Override
			public boolean isBiologicalAssembly() {
				return false;
			}

			@Override
			public void setCrystallographicInfo(PDBCrystallographicInfo pdbCrystallographicInfo) {

			}

			@Override
			public PDBCrystallographicInfo getCrystallographicInfo() {
				return null;
			}

			@Override
			public void resetModels() {

			}

			@Override
			public String getPdbId() {
				return null;
			}

			@Override
			public List<? extends ResidueRange> getResidueRanges() {
				return null;
			}

			@Override
			public List<String> getRanges() {
				return null;
			}

			@Override
			public String getIdentifier() {
				return null;
			}
		}, Collections.singletonList(
				new Residue("Asp", "A", "7", "")
		));

		Assert.assertNotNull( motif );
	}
}
