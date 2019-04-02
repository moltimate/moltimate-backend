package org.moltimate.moltimatebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.service.AsyncService;
import org.moltimate.moltimatebackend.service.GenerateMotifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task REST API contains tasks which are useful and often run periodically by cron jobs
 */
@RestController
@RequestMapping(value = "/tasks")
@Slf4j
public class TaskController {

    @Autowired
    private GenerateMotifService generateMotifService;

    @Autowired
    private AsyncService asyncService;

    /**
     * Updates the motif database using the CSA and RCSB PDB
     */
    @RequestMapping(value = "/updatemotifs", method = RequestMethod.GET)
    public void updateMotifs() {
        generateMotifService.updateMotifs();
    }

    @RequestMapping(value = "/updateAsync", method = RequestMethod.GET)
    private ResponseEntity asyncMethod() {
        asyncService.process();
        return ResponseEntity.ok("ok");
    }
}