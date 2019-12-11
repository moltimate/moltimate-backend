package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.dto.request.ExportRequest;
import org.moltimate.moltimatebackend.model.ligand.PDBQT;
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

@RestController
@RequestMapping(value="/dock")
@Slf4j
@Api(value="/dock", description = "Docking Controller", produces="application/json")
public class DockingController {

	@Autowired
	private DockingService dockingService;

	@ApiOperation(value = "Creates a docked .pdbqt file")
	@RequestMapping(value="/dockligand", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PDBQT> dockLigand(DockingRequest request) {
		return ResponseEntity.ok( dockingService.dockLigand(request) );
	}

	@ApiOperation(value = "Exports ligand docking information to a csv file")
	@RequestMapping( value = "/exportLigands", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = "text/csv")
	public ResponseEntity<Resource> exportLigand(@RequestBody ExportRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Disposition", "attachment; filename=\"ligands.csv\"");
		return ResponseEntity.ok().headers(headers).body(dockingService.exportLigands(request));
	}
}
