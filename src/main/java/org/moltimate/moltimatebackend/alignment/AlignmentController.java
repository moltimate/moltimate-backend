package org.moltimate.moltimatebackend.alignment;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.alignment.requests.ActiveSiteAlignmentRequest;
import org.moltimate.moltimatebackend.alignment.requests.BackboneAlignmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Alignment REST API
 */
@RestController
@RequestMapping(value = "/align")
@Slf4j
public class AlignmentController {

    @Autowired
    private AlignmentService alignmentService;

    @RequestMapping(value = "/activesite", method = RequestMethod.POST)
    public AlignmentResponse activeSiteAlignment(@RequestBody ActiveSiteAlignmentRequest alignmentRequest) {
        log.info("Received request to align active sites: " + alignmentRequest);
        return alignmentService.alignActiveSites(alignmentRequest);
    }

    @RequestMapping(value = "/backbone", method = RequestMethod.POST)
    public AlignmentResponse backboneAlignment(@RequestBody BackboneAlignmentRequest alignmentRequest) {
        log.info("Received request to align active sites: " + alignmentRequest);
        return alignmentService.alignBackbones(alignmentRequest);
    }
}
