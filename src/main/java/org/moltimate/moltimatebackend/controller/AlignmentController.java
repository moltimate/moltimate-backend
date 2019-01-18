package org.moltimate.moltimatebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.request.ActiveSiteAlignmentRequest;
import org.moltimate.moltimatebackend.request.BackboneAlignmentRequest;
import org.moltimate.moltimatebackend.response.AlignmentResponse;
import org.moltimate.moltimatebackend.service.AlignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Alignment REST API
 */
@RestController
@RequestMapping(value = "/align")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class AlignmentController {

    @Autowired
    private AlignmentService alignmentService;

    @RequestMapping(value = "/activesite", method = RequestMethod.POST)
    public ResponseEntity<AlignmentResponse> activeSiteAlignment(@RequestBody ActiveSiteAlignmentRequest alignmentRequest) {
        log.info("Received request to align active sites: " + alignmentRequest);
        return ResponseEntity.ok(alignmentService.alignActiveSites(alignmentRequest));
    }

    @RequestMapping(value = "/backbone", method = RequestMethod.POST)
    public ResponseEntity<AlignmentResponse> backboneAlignment(@RequestBody BackboneAlignmentRequest alignmentRequest) {
        log.info("Received request to align active sites: " + alignmentRequest);
        return ResponseEntity.ok(alignmentService.alignBackbones(alignmentRequest));
    }
}
