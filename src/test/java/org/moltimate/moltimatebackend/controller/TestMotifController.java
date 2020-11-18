package org.moltimate.moltimatebackend.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.exception.InvalidMotifException;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.service.MotifService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

public class TestMotifController {

	private MotifService service;
	private MotifController controller;

	@Before
	public void setUp() {
		service = Mockito.mock( MotifService.class );
		controller = new MotifController( service );
	}

	@After
	public void tearDown() {
		service = null;
		controller = null;
	}

	@Test
	public void testFindMotifs() {
		Mockito.when( service.queryByEcNumber( anyString(), anyInt() ) ).thenReturn(null);
		ResponseEntity<Page<Motif>> response = controller.findMotifs( Optional.of( "test" ), 1 );
		Assert.assertEquals( HttpStatus.OK, response.getStatusCode() );
	}

	@Test
	public void testFindByPdbId() {
		Mockito.when( service.queryByPdbId( anyString() ) ).thenReturn( new Motif() );
		ResponseEntity<Motif> response = controller.findMotifByPdbId( "test" );
		Assert.assertEquals( HttpStatus.OK, response.getStatusCode() );
	}

	@Test( expected = InvalidMotifException.class )
	public void testFindByPdbIdException() {
		Mockito.when( service.queryByPdbId( anyString() ) ).thenReturn( null );
		ResponseEntity<Motif> response = controller.findMotifByPdbId( "test" );
	}
}
