package org.moltimate.moltimatebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.service.MotifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Page<Motif>> findMotifs(@RequestParam("ecnumber") Optional<String> ecNumber) {
        return ResponseEntity.ok(motifService.queryByEcNumber(ecNumber.orElse(null), 0));
    }

    /**
     * Return the Motif with the given PDB ID
     */
    @RequestMapping(value = "/{pdbId}", method = RequestMethod.GET)
    public ResponseEntity<Motif> findMotifByPdbId(@PathVariable String pdbId) {
        Motif motif = motifService.queryByPdbId(pdbId.toLowerCase());
        if (motif != null) {
            return ResponseEntity.ok(motif);
        }
        return new ResponseEntity(String.format("Motif with the id '%s' not found", pdbId), HttpStatus.NOT_FOUND);
    }
}
