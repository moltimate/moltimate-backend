package org.moltimate.moltimatebackend.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.dto.alignment.FailedAlignment;
import org.moltimate.moltimatebackend.dto.alignment.SuccessfulAlignment;
import org.moltimate.moltimatebackend.dto.response.QueryAlignmentResponse;
import org.moltimate.moltimatebackend.dto.response.QueryResponseData;
import org.moltimate.moltimatebackend.repository.FailedAlignmentRespository;
import org.moltimate.moltimatebackend.repository.QueryAlignmentResponseRepository;
import org.moltimate.moltimatebackend.repository.QueryResponseDataRepository;
import org.moltimate.moltimatebackend.repository.SuccessfulAlignmentRepository;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class TestCacheService {
	@InjectMocks
	private CacheService cacheService;

	@Mock
	private QueryAlignmentResponseRepository queryAlignmentResponseRepository;

	@Mock
	private QueryResponseDataRepository queryResponseDataRepository;

	@Mock
	private SuccessfulAlignmentRepository successfulAlignmentRepository;

	@Mock
	private FailedAlignmentRespository failedAlignmentRespository;

	@Test
	public void testCache() {
		QueryAlignmentResponse response = new QueryAlignmentResponse();
		QueryResponseData entry = Mockito.mock( QueryResponseData.class );
		SuccessfulAlignment success = Mockito.mock( SuccessfulAlignment.class );
		Mockito.when( entry.getAlignments() ).thenReturn( Collections.singletonList( success ) );
		FailedAlignment failure = Mockito.mock( FailedAlignment.class );
		Mockito.when( entry.getFailedAlignments() ).thenReturn( Collections.singletonList( failure ) );
		response.setEntries( Collections.singletonList( entry ) );
		cacheService.cache.put( "TEST", response );
		Assert.assertEquals( response, cacheService.cache.getIfPresent( "TEST" ) );
		cacheService.cache.invalidate( "TEST" );
		Assert.assertNull( cacheService.cache.getIfPresent( "TEST" ) );
	}

	@Test
	public void testQueryAlignmentResponse() {
		QueryAlignmentResponse response = new QueryAlignmentResponse();
		Mockito.when( queryAlignmentResponseRepository.findByCacheKey( any() ) ).thenReturn( response );
		Assert.assertEquals( response, cacheService.findQueryAlignmentResponse( "TEST" ) );
	}
}
