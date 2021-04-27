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
import java.util.List;

@RunWith(SpringRunner.class)
public class TestDockingService {
	@InjectMocks
	private DockingService dockingService;

	@Test
	public void testDocking() {
		// todo Implement test here
	}

	@Test
	public void testCSV() {
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

	@Test
	public void testFileSeparation(){
		String testFile = "MODEL       1\n" +
				"HEADER    HYDROLASE                               24-MAR-04   1SSX   \n"+
				"ENDMDL\n" +
				"MODEL       2\n" +
				"REMARK   3   CROSS-VALIDATION METHOD          : THROUGHOUT  \n" +
				"ENDMDL\n" +
				"MODEL       3\n" +
				"ATOM   2107  CG  ARG A 192     -12.067   6.579  -3.577  1.00  0.00           C  \n" +
				"ENDMDL\n" +
				"MODEL       4\n" +
				"CONECT  856  859  857  855     \n" +
				"ENDMDL";
		String[] testFileLines = testFile.split("\n");

		List<Boolean> selectedConfigs = new ArrayList<Boolean>();
		selectedConfigs.add(true);
		selectedConfigs.add(true);
		selectedConfigs.add(false);
		selectedConfigs.add(false);
		selectedConfigs.add(true);

		ArrayList<String> separatedFile = dockingService.separatePDBFile(testFileLines, selectedConfigs);

		ArrayList<String> expected = new ArrayList<String>();
		expected.add("MODEL       1");
		expected.add("HEADER    HYDROLASE                               24-MAR-04   1SSX   ");
		expected.add("ENDMDL");
		expected.add("MODEL       2");
		expected.add("REMARK   3   CROSS-VALIDATION METHOD          : THROUGHOUT  ");
		expected.add("ENDMDL");
		expected.add("MODEL       3");
		expected.add("ATOM   2107  CG  ARG A 192     -12.067   6.579  -3.577  1.00  0.00           C  ");
		expected.add("ENDMDL");
		expected.add("MODEL       4");
		expected.add("CONECT  856  859  857  855     ");
		expected.add("ENDMDL");

		Assert.assertEquals(expected, separatedFile);
	}
}
