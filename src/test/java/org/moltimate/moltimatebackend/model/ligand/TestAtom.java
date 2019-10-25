package org.moltimate.moltimatebackend.model.ligand;

import org.junit.Assert;
import org.junit.Test;

public class TestAtom {
	@Test
	public void testParseAtom() {
		String formatted = "ATOM     25 O2  AINH I   2B   -412.019-232.378-100.708440.00120.001a0j-0.644 OA \n";
		Atom testAtom = new Atom( formatted );
		Assert.assertEquals( 25, testAtom.getSerialNum() );
		Assert.assertEquals( "O2", testAtom.getName() );
		Assert.assertEquals( "A", testAtom.getAltLoc() );
		Assert.assertEquals( "INH", testAtom.getResName() );
		Assert.assertEquals( "I", testAtom.getChainID() );
		Assert.assertEquals( 2, testAtom.getResNum() );
		Assert.assertEquals( "B", testAtom.getInsCode() );
		Assert.assertEquals( -412.019, testAtom.getX(), .001 );
		Assert.assertEquals( -232.378, testAtom.getY(), .001 );
		Assert.assertEquals( -100.708, testAtom.getZ(), .001 );
		Assert.assertEquals( 440.00, testAtom.getOccupancy(), .01 );
		Assert.assertEquals( 120.00, testAtom.getTempFactor(), .01 );
		Assert.assertEquals( "1a0j", testAtom.getFootnote() );
		Assert.assertEquals( -0.644, testAtom.getPartialCharge(), .001 );
		Assert.assertEquals( "OA", testAtom.getAtomType() );
		Assert.assertEquals(formatted, testAtom.toString() );
	}
}
