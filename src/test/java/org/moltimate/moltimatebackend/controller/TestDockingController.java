package org.moltimate.moltimatebackend.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.moltimate.moltimatebackend.service.DockingService;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TestDockingController {

	@InjectMocks
	private DockingController dockingController;

	@Mock
	private DockingService dockingService;

	@Test
	public void testDocking() {
		// todo Implement test here
	}
}
