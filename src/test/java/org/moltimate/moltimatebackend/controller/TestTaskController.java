package org.moltimate.moltimatebackend.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.service.AsyncService;
import org.moltimate.moltimatebackend.service.MotifService;

public class TestTaskController {

	private MotifService motifService;
	private AsyncService asyncService;
	private TaskController controller;

	@Before
	public void setUp() {
		motifService = Mockito.mock( MotifService.class );
		asyncService = Mockito.mock( AsyncService.class );
		controller = new TaskController( motifService, asyncService );
	}

	@After
	public void tearDown() {
		motifService = null;
		asyncService = null;
		controller = null;
	}

	@Test
	public void testUpdateMotifs() {
		Assert.assertEquals( "complete", controller.updateMotifs() );
	}
}
