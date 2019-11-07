package org.moltimate.moltimatebackend.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.model.ligand.PDBQT;
import org.moltimate.moltimatebackend.service.DockingService;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class TestDockingController {

	@InjectMocks
	private DockingController dockingController;

	@Mock
	private DockingService dockingService;

	@Test
	public void testDocking() {
		PDBQT dockedFile = new PDBQT();
		Mockito.when( dockingService.dockLigand( any() ) ).thenReturn( dockedFile );
		ResponseEntity<PDBQT> response = dockingController.dockLigand( new DockingRequest() );
		Assert.assertEquals( dockedFile, response.getBody() );
	}
}
