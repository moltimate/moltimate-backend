package org.moltimate.moltimatebackend.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestAlignment {

	@Test
	public void testAlignment() {
		Alignment CuT = new Alignment();
		ArrayList list = new ArrayList<Residue>();
		CuT.setMotifPdbId("1a0j");
		CuT.setEcNumber("3.4.21.4");
		CuT.setLevenshtein( 3 );
		CuT.setRmsd( 5.2 );
		CuT.setActiveSiteResidues(list);
		CuT.setAlignedResidues(list);

		Assert.assertEquals("1a0j", CuT.getMotifPdbId());
		Assert.assertEquals("3.4.21.4", CuT.getEcNumber());
		Assert.assertEquals(3, CuT.getLevenshtein());
		Assert.assertTrue(5.2 == CuT.getRmsd()); // assertDouble is deprecated
		Assert.assertEquals(list, CuT.getActiveSiteResidues());
		Assert.assertEquals(list, CuT.getAlignedResidues());
	}
}
