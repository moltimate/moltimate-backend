package org.moltimate.moltimatebackend.motif;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Motif's REST API
 */
@RestController
@RequestMapping(value = "/motif")
@Slf4j
public class MotifController {

    @Autowired
    private MotifService motifService;

    /**
     * Query all motifs based on a set of motif filters
     *
     * @param createMotifRequest
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Motif createMotif(@RequestBody CreateMotifRequest createMotifRequest) {
        log.info("Received request to create new motif: " + createMotifRequest);
        return motifService.createMotif(createMotifRequest);
    }
}
