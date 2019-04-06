package org.moltimate.moltimatebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.util.PdbXmlClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Task REST API contains tasks which are useful and often run periodically by cron jobs
 */
@RestController
@RequestMapping(value = "/pdb")
@Slf4j
public class PdbController {


    @RequestMapping(value = "/ids", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getCurrentPdbIds() {
        return new ResponseEntity<>(PdbXmlClient.getPdbIds(), HttpStatus.OK);
    }
}