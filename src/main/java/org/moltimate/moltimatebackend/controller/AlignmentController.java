package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.Alignment.QueryAlignmentResponse;
import org.moltimate.moltimatebackend.dto.Request.AlignmentRequest;
import org.moltimate.moltimatebackend.exception.InvalidPdbIdException;
import org.moltimate.moltimatebackend.service.AlignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Alignment REST API
 */
@RestController
@RequestMapping(value = "/align")
@Slf4j
@Api(value = "/align", description = "Alignment Controller", produces = "application/json")
public class AlignmentController {

    @Autowired
    private AlignmentService alignmentService;

    @ApiOperation(value = "Active Site Alignment", response = QueryAlignmentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of active site alignments", response = QueryAlignmentResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "All PDB ids invalid", response = InvalidPdbIdException.class)
    })
    @RequestMapping(value = "/activesite", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QueryAlignmentResponse> activeSiteAlignment(AlignmentRequest alignmentRequest) {
        log.info("Received request to align active sites: " + alignmentRequest);
        return ResponseEntity.ok(alignmentService.alignActiveSites(alignmentRequest));
    }
}
