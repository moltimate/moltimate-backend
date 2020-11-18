package org.moltimate.moltimatebackend.model.ligand;

import org.junit.Assert;
import org.junit.Test;

public class TestPDBQT {

	@Test
	public void testParse() {
		String file =
				"COMPND  NSC7810                                                                 \n" +
				"REMARK  1 remark 1                                                              \n" +
				"REMARK  2 remark 2                                                              \n" +
				"ROOT                                                                            \n" +
				"ATOM      1 O2  AINH I   2B   -412.019-232.378-100.708440.00120.001a0j-0.644 OA \n" +
				"HETATM    2 O3  AISH I   2C   -416.019-231.378-120.708340.00120.001a0j-0.644 OA \n" +
				"ENDROOT                                                                         \n" +
				"BRANCH      2      3                                                            \n" +
				"ATOM      3 O2  AINH I   2B   -412.019-232.378-100.708440.00120.001a0j-0.644 OA \n" +
				"BRANCH      3      5                                                            \n" +
				"ATOM      4 O2  AINH I   2B   -412.019-232.378-100.708440.00120.001a0j-0.644 OA \n" +
				"ATOM      5 O2  AINH I   2B   -412.019-232.378-100.708440.00120.001a0j-0.644 OA \n" +
				"ENDBRANCH      3      5                                                         \n" +
				"ENDBRANCH      2      3                                                         \n" +
				"TORSDOF 3                                                                       \n";

		PDBQT testPDBQT = PDBQT.createLigand(file);
		Assert.assertEquals( "NSC7810", testPDBQT.getCompound() );
		Assert.assertEquals( 3, testPDBQT.getTorsDOF() );
		Assert.assertEquals( 2, testPDBQT.getRoot().size() );
		Assert.assertEquals( "O2", testPDBQT.getRoot().get(0).getName() );
		Assert.assertEquals( 2, testPDBQT.getRemarks().size() );
		Assert.assertEquals( "1 remark 1", testPDBQT.getRemarks().get(0) );
		Assert.assertEquals( 1, testPDBQT.getBranches().size() );
		Assert.assertEquals( 1, testPDBQT.getBranches().get(0).getAtoms().size() );
		Assert.assertEquals( 1, testPDBQT.getBranches().get(0).getEmbeddedBranches().size() );
		Assert.assertEquals( 3, testPDBQT.getBranches().get(0).getEmbeddedBranches().get(0).getStart() );
		Assert.assertEquals( 5, testPDBQT.getBranches().get(0).getEmbeddedBranches().get(0).getEnd() );
		Assert.assertEquals( 2, testPDBQT.getBranches().get(0).getEmbeddedBranches().get(0).getAtoms().size() );
		Assert.assertEquals( 2, testPDBQT.getBranches().get(0).getStart() );
		Assert.assertEquals( 3, testPDBQT.getBranches().get(0).getEnd() );
		Assert.assertEquals(file, testPDBQT.toString() );
	}
}
