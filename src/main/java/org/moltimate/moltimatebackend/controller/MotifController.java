package org.moltimate.moltimatebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.MotifSelection;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.model.ResidueQuerySet;
import org.moltimate.moltimatebackend.service.MotifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    public ResponseEntity<List<Motif>> findMotifs(@RequestParam("ecnumber") Optional<String> ecNumber) {
        return ResponseEntity.ok(motifService.queryByEcNumber(ecNumber.orElse(null)));
    }

    /**
     * Return the Motif with the given PDB ID
     */
    @RequestMapping(value = "/{pdbId}", method = RequestMethod.GET)
    public ResponseEntity<Motif> findMotifByPdbId(@PathVariable String pdbId) {
        return ResponseEntity.ok(motifService.queryByPdbId(pdbId));
    }
}
