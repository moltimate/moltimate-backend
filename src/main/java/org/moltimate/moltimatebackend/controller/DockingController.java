package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.model.ligand.PDBQT;
import org.moltimate.moltimatebackend.service.DockingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
}
