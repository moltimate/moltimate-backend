package org.moltimate.moltimatebackend.model;

import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.ResidueNumber;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

public class TestResidue {
	@Test
	public void testFromGroup() {
		Group testGroup = Mockito.mock( Group.class );
		Mockito.when( testGroup.getPDBName() ).thenReturn( "testName" );
		Mockito.when( testGroup.getResidueNumber()).thenReturn( new ResidueNumber( "Test chain", 1, 'a' ) );
		Atom testAtom = Mockito.mock( Atom.class );
		Mockito.when( testAtom.getAltLoc() ).thenReturn( 'a' );
		Mockito.when( testGroup.getAtoms() ).thenReturn(Arrays.asList(testAtom, testAtom) );
		Mockito.when( testGroup.hasAltLoc() ).thenReturn( true );
		Residue test = Residue.fromGroup(testGroup);
		Assert.assertEquals( "testName", test.getResidueName() );
		Assert.assertEquals( "1a", test.getResidueId() );
		Assert.assertEquals( "Test chain", test.getResidueChainName() );
		Assert.assertEquals( "a", test.getResidueAltLoc() );
		Assert.assertEquals( "testName Test chain 1a", test.getIdentifier() );
		Assert.assertEquals( test, test.clone() );
	}
}
