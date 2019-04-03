package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.service.MotifService;
import org.moltimate.moltimatebackend.validation.exceptions.InvalidMotifException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Motif REST API
 */
@RestController
@RequestMapping(value = "/motifs")
@Slf4j
public class MotifController {

    @Autowired
    private MotifService motifService;

    /**
     * Return all Motifs, optionally filtered by EC number
     */
    @ApiOperation(value = "Find motifs by EC number")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Page<Motif>> findMotifs(
            @ApiParam(name = "ecnumber", value = "Filter motifs by EC number. If 'ecnumber' is empty return all motifs")
            @RequestParam(name = "ecnumber", required = false) Optional<String> ecNumber,
            @ApiParam(name = "page", value = "Page number", defaultValue = "0")
            @RequestParam(name = "page", required = false, defaultValue = "0") int pageNumber)
    {
        return ResponseEntity.ok(motifService.queryByEcNumber(ecNumber.orElse(null), pageNumber));
    }

    /**
     * Return the Motif with the given PDB ID
     */
    @ApiOperation(value = "Find motif by PDB id")
    @RequestMapping(value = "/{pdbId}", method = RequestMethod.GET)
    public ResponseEntity<Motif> findMotifByPdbId(
            @ApiParam(name = "pdbId", value = "PDB id of motif")
            @PathVariable(name = "pdbId") String pdbId)
    {
        Motif motif = motifService.queryByPdbId(pdbId.toLowerCase());
        if (motif != null) {
            return ResponseEntity.ok(motif);
        }
        throw new InvalidMotifException(pdbId);
    }
}
