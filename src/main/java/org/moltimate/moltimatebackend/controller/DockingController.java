package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.dto.request.ExportRequest;
import org.moltimate.moltimatebackend.exception.DockingJobFailedException;
import org.moltimate.moltimatebackend.service.DockingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

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

	@ApiOperation( value = "Uses supplied storage hash and pdbId to query autodock for docked ligand then passes files to openbabel to be converted and combined")
	@RequestMapping( value = "/dockligand", method = RequestMethod.GET )
	public ResponseEntity<Object> retrieveDocking( String jobId, String pdbId ) throws IOException {
		try {
			return ResponseEntity.ok( dockingService.getDockingResult( jobId, pdbId ) );
		} catch( DockingJobFailedException ex) {
			return ResponseEntity.status(500).body(ex.getError().getBytes());
		}
	}

	@ApiOperation( value = "Uses supplied storage hash to query openbabel for a completed job." )
	@RequestMapping( value = "/retrievefile", method = RequestMethod.GET )
	public ResponseEntity<Object> retrieveCombinedFile( String babelJobId ) throws IOException {
		try {
			return ResponseEntity.ok( dockingService.getBabelResult( babelJobId ).getBytes() );
		} catch( DockingJobFailedException ex) {
			return ResponseEntity.status(500).body(ex.getError().getBytes());
		}
	}

	@ApiOperation(value = "Exports ligand docking information to a csv file")
	@RequestMapping( value = "/exportLigands", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/zip")
	public ResponseEntity<Resource> exportLigand(@RequestBody ExportRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Disposition", "attachment; filename=\"moltimate.zip\"");
		return ResponseEntity.ok().headers(headers).body(dockingService.exportLigands(request));
	}
}
