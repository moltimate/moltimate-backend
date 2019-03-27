package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.request.ActiveSiteAlignmentRequest;
import org.moltimate.moltimatebackend.response.ActiveSiteAlignmentResponse;
import org.moltimate.moltimatebackend.service.AlignmentService;
import org.moltimate.moltimatebackend.validation.exceptions.InvalidPdbIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@Api(value = "/align", description = "Alignment Controller", produces = "application/json")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class AlignmentController {

    @Autowired
    private AlignmentService alignmentService;

    @ApiOperation(value = "Active Site Alignment", response = ActiveSiteAlignmentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of active site alignments", response = ActiveSiteAlignmentResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "All PDB ids invalid", response = String.class)
    })
    @RequestMapping(value = "/activesite", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActiveSiteAlignmentResponse> activeSiteAlignment(ActiveSiteAlignmentRequest alignmentRequest) {
        log.info("Received request to align active sites: " + alignmentRequest);
        ActiveSiteAlignmentResponse response;
        try {
            // TODO: Collect failed ids and return so a toast can trigger
            response = alignmentService.alignActiveSites(alignmentRequest);
        } catch (InvalidPdbIdException e) {
            return new ResponseEntity(
                    String.format("Could not find structures for the following PDB ids: %s", alignmentRequest.getPdbIds()),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }
}
