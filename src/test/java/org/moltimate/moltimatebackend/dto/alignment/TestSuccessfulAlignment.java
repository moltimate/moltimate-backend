package org.moltimate.moltimatebackend.dto.alignment;

import com.sun.net.httpserver.Authenticator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;

import java.util.ArrayList;

public class TestSuccessfulAlignment {

	@Test
	public void testNoArgConstructor(){
		SuccessfulAlignment CuT = new SuccessfulAlignment();
		Assert.assertNotNull( CuT ); // No-argument constructor makes empty component
	}

	@Test
	public void testSuccessfulAlignmentBuilder(){
		ArrayList list = new ArrayList<Residue>();
		SuccessfulAlignment CuT = SuccessfulAlignment.builder()
				.pdbId("1a0j")
				.ecNumber("3.4.21.4")
				.levenshtein(0)
				.rmsd( 1.0 )
				.activeSiteResidues( list )
				.alignedResidues( list ).build();

		Assert.assertEquals( "1a0j", CuT.getPdbId() );
		Assert.assertEquals( "3.4.21.4", CuT.getEcNumber() );
		Assert.assertEquals( 0, CuT.getLevenshtein() );
		Assert.assertTrue( 1.0 == CuT.getRmsd() );
		Assert.assertEquals( list, CuT.getActiveSiteResidues() );
		Assert.assertEquals( list, CuT.getAlignedResidues() );
	}

	@Test
	public void testSuccessfulAlignmentClone(){
		SuccessfulAlignment CuT;

		Motif motif = Mockito.mock( Motif.class );
		Mockito.when( motif.getPdbId() ).thenReturn( "1a0j" );
		Mockito.when( motif.getEcNumber() ).thenReturn( "3.4.21.4" );

		Alignment alignment = Mockito.mock( Alignment.class );
		Mockito.when( alignment.getRmsd() ).thenReturn( 0.0 );
		Mockito.when( alignment.getLevenshtein() ).thenReturn( 0 );

		Residue res = Mockito.mock( Residue.class );
		ArrayList<Residue> list = new ArrayList<Residue>();
		list.add( res );
		Mockito.when( alignment.getActiveSiteResidues() ).thenReturn( list );
		Mockito.when( alignment.getAlignedResidues() ).thenReturn( list );

		CuT = new SuccessfulAlignment(motif, alignment);
		SuccessfulAlignment clone = CuT.clone();
		Assert.assertEquals( CuT.getEcNumber(), clone.getEcNumber() );
	}
}
