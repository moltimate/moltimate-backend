package org.moltimate.moltimatebackend.dto.response;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.dto.alignment.SuccessfulAlignment;

import java.util.ArrayList;

public class TestQueryAlignmentResponse {

	@Test
	public void testAddQueryResponseData(){
		QueryAlignmentResponse CuT = new QueryAlignmentResponse();
		QueryResponseData data = Mockito.mock( QueryResponseData.class );
		Mockito.when( data.similar(data) ).thenReturn( true );
		ArrayList alignments = new ArrayList<SuccessfulAlignment>();
		alignments.add( new SuccessfulAlignment());
		Mockito.when( data.getAlignments() ).thenReturn( alignments );
		ArrayList entries = new ArrayList<QueryResponseData>();
		entries.add( data );
		CuT.setEntries( entries );

		CuT.addQueryResponseData( data );
	}

	@Test
	public void testQueryAlignmentResponseBuilder(){
		ArrayList list = new ArrayList<String>();
		String key = "abc123";
		QueryAlignmentResponse CuT = QueryAlignmentResponse.builder()
				.failedPdbIds(list)
				.cacheKey( key )
				.entries( list )
				.build();

		Assert.assertEquals( list, CuT.getEntries() );
		Assert.assertEquals( key, CuT.getCacheKey());
		Assert.assertEquals( list, CuT.getEntries());
	}
}
