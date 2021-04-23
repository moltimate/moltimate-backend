package org.moltimate.moltimatebackend.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.moltimate.moltimatebackend.dto.request.ExportLigand;
import org.moltimate.moltimatebackend.dto.request.ExportRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
public class TestDockingService {
	@InjectMocks
	private DockingService dockingService;

	@Test
	public void testDocking() {
		// todo Implement test here
	}

	@Test
	public void testExport() {
		ExportRequest request = new ExportRequest( new ArrayList<>(), null, null );
		request.getLigands().add( new ExportLigand("Name1", 10, 1, 0.1, 0.01) );
		request.getLigands().add( new ExportLigand("Name2,", 20, 2, 0.2, 0.02) );
		request.getLigands().add( new ExportLigand("Name3", 30, 3, 0.3, 0.03) );

		Resource exported = dockingService.createCSV(request);
		String csv = new String(((ByteArrayResource)exported).getByteArray());
		Assert.assertEquals("Name,Mode Number,Binding Energy,RMSD Lower,RMSD Upper\n" +
				"\"Name1\",1.0,10.0,0.01,0.1\n" +
				"\"Name2,\",2.0,20.0,0.02,0.2\n" +
				"\"Name3\",3.0,30.0,0.03,0.3\n", csv);
	}
}
