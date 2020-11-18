package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.service.LigandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.biojava.nbio.structure.rcsb.RCSBLigand;

import java.util.List;

/**
 * Ligand REST API
 */
@RestController
@RequestMapping(value = "/ligands")
@Slf4j
public class LigandController {

    @Autowired
    private LigandService ligandService;

    LigandController(LigandService service) {
        this.ligandService = service;
    }

    /**
     * Return all Ligands associated with known proteins of the given EC
     */
    @ApiOperation(value = "Find ligands by EC number")
    @RequestMapping(value = "/{ecnumber}", method = RequestMethod.GET)
    public ResponseEntity<List<RCSBLigand>> findLigands(
            @ApiParam(name = "ecnumber", value = "Find Ligands by EC number.")
            @PathVariable(name = "ecnumber") String ecNumber) {
        log.info("Retrieving Ligands associated with Enzyme Class {}", ecNumber);
        return ResponseEntity.ok(ligandService.getByEcNumber(ecNumber));
    }
}
