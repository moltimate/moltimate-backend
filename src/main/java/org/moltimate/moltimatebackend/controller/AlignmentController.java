package org.moltimate.moltimatebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.request.ActiveSiteAlignmentRequest;
import org.moltimate.moltimatebackend.response.ActiveSiteAlignmentResponse;
import org.moltimate.moltimatebackend.service.AlignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    @RequestMapping(value = "/activesite", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActiveSiteAlignmentResponse> activeSiteAlignment(ActiveSiteAlignmentRequest alignmentRequest) {
        log.info("Received request to align active sites: " + alignmentRequest);
        return ResponseEntity.ok(alignmentService.alignActiveSites(alignmentRequest));
    }
}
