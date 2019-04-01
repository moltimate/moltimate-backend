package org.moltimate.moltimatebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.service.MotifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Task REST API contains tasks which are useful and often run periodically by cron jobs
 */
@RestController
@RequestMapping(value = "/tasks")
@Slf4j
public class TaskController {

    @Autowired
    private MotifService motifService;

    /**
     * Updates the motif database using the CSA and RCSB PDB
     */
    @ApiIgnore // This doesn't need to be publicly listed in API documentation
    @RequestMapping(value = "/updatemotifs", method = RequestMethod.GET)
    public void updateMotifs() {
        motifService.updateMotifs();
    }
}
