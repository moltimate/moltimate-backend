package org.moltimate.moltimatebackend.controller;

import org.moltimate.moltimatebackend.util.PdbXmlClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * PDB API pass through
 */
@RestController
@RequestMapping(value = "/pdb")
public class PdbController {


    @RequestMapping(value = "/ids", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getCurrentPdbIds() {
        return new ResponseEntity<>(PdbXmlClient.getPdbIds(), HttpStatus.OK);
    }

    @RequestMapping(value = "/ids/{id}", method = RequestMethod.GET)
    public ResponseEntity checkPdbId(@PathVariable String id) {
        if (PdbXmlClient.getPdbIds().contains(id)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}