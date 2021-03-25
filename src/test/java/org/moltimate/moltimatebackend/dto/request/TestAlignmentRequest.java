package org.moltimate.moltimatebackend.dto.request;

import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.dto.response.PdbQueryResponse;
import org.moltimate.moltimatebackend.util.ProteinUtils;

import java.util.ArrayList;

public class TestAlignmentRequest {

	@Test
	public void testPrecisionNonDefault(){
		AlignmentRequest CuT = new AlignmentRequest();
		CuT.setPrecisionFactor( 3.14 );
		Assert.assertTrue( 3.14 == CuT.getPrecisionFactor() );
	}

	@Test
	public void testExtractMotif(){
		AlignmentRequest CuT = new AlignmentRequest();

		Assert.assertNotNull( CuT.extractCustomMotifFileList() ); // Empty list
	}

	@Test
	public void testQueryPdb(){

		AlignmentRequest CuT = new AlignmentRequest();
		ArrayList list = new ArrayList<String>();
		CuT.setPdbIds( list );
		PdbQueryResponse response = new PdbQueryResponse();
		Mockito.when( ProteinUtils.queryPdbResponse(list, list) ).thenReturn( null );

		Assert.assertEquals(response, CuT.callPdbForResponse());
	}
}
