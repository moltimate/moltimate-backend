package org.moltimate.moltimatebackend.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.model.ligand.PDBQT;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TestDockingService {
	@InjectMocks
	private DockingService dockingService;

	@Test
	public void testDocking() {
		Assert.assertEquals( new PDBQT(), dockingService.dockLigand( new DockingRequest() ) );
	}
}
