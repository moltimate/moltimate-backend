package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.exception.DockingJobFailedException;
import org.moltimate.moltimatebackend.service.DockingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(value="/dock")
@Slf4j
@Api( value = "/dock", description = "Docking Controller", produces = "application/json")
public class DockingController {

	@Autowired
	private DockingService dockingService;

	@ApiOperation( value = "Retrieves a storage hash for future access to autodock server." )
	@RequestMapping( value = "/dockligand", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public ResponseEntity<Object> dockLigand(DockingRequest request) throws IOException {
		try {
			return ResponseEntity.ok( dockingService.dockLigand( request ) );
		} catch(DockingJobFailedException ex) {
			return ResponseEntity.status(500).body(ex.getError().getBytes());
		}
	}

	@ApiOperation( value = "Uses supplied storage hash to access an autodock output file.")
	@RequestMapping( value = "/dockligand", method = RequestMethod.GET )
	public ResponseEntity<Object> retrieveDocking( String storage_hash ) throws IOException {
		try {
			return ResponseEntity.ok( dockingService.getDockingResult( storage_hash ).getBytes() );
		} catch( DockingJobFailedException ex) {
			return ResponseEntity.status(500).body(ex.getError().getBytes());
		}
	}

	@ApiIgnore
	@RequestMapping( value = "/testAutoDock", method=RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public String testAutoDock( DockingRequest request ) {
		return request.getMacromolecule().getOriginalFilename();
	}

	@ApiIgnore
	@RequestMapping( value = "/testAutoDock", method = RequestMethod.GET)
	public ResponseEntity<byte[]> testGetAutodock(String storage_hash) throws IOException {
		return testGetBabel( storage_hash );
	}

	@ApiIgnore
	@RequestMapping( value = "/testOpenBabel", method=RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public String testOpenBabel() {
		return "testhash";
	}

	@ApiIgnore
	@RequestMapping( value = "/testOpenBabel", method = RequestMethod.GET )
	public ResponseEntity<byte[]> testGetBabel(String storage_hash) throws IOException {
		byte[] data1 = "TEST DATA 1".getBytes();
		byte[] data2 = "TEST DATA 2".getBytes();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream( output );
		ZipEntry entry1 = new ZipEntry("macromolecule.pdbqt");
		ZipEntry entry2 = new ZipEntry( "ligand.pdbqt" );

		zipOutputStream.putNextEntry(entry1);
		zipOutputStream.write( data1, 0, data1.length );
		zipOutputStream.closeEntry();

		zipOutputStream.putNextEntry(entry2);
		zipOutputStream.write( data2, 0, data2.length );
		zipOutputStream.closeEntry();

		zipOutputStream.close();

		if( storage_hash.equalsIgnoreCase("Fail") ) {
			return ResponseEntity.status(500).body(
					new MockMultipartFile( "job.zip", output.toByteArray() ).getBytes() );
		}

		if( storage_hash.equalsIgnoreCase("inprogress") ) {
			return ResponseEntity.status(203).body(String.format( "Job %s not completed", storage_hash).getBytes());
		}

		return ResponseEntity.ok(new MockMultipartFile( "job.zip", output.toByteArray() ).getBytes());
	}
}
