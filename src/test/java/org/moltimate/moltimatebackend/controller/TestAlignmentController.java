package org.moltimate.moltimatebackend.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.dto.request.AlignmentRequest;
import org.moltimate.moltimatebackend.dto.response.QueryAlignmentResponse;
import org.moltimate.moltimatebackend.service.AlignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;

public class TestAlignmentController {

	private AlignmentService service;
	private AlignmentController controller;

	@Before
	public void setUp() {
		service = Mockito.mock( AlignmentService.class );
		controller = new AlignmentController( service );
	}

	@After
	public void tearDown() {
		service = null;
		controller = null;
	}

	@Test
	public void testOKStatus() {
		Mockito.when( service.alignActiveSites( any() ) ).thenReturn( new QueryAlignmentResponse() );
		ResponseEntity<QueryAlignmentResponse> response = controller.activeSiteAlignment( new AlignmentRequest() );
		Assert.assertEquals( HttpStatus.OK, response.getStatusCode() );
	}
}
