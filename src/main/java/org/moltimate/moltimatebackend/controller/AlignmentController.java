package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.request.AlignmentRequest;
import org.moltimate.moltimatebackend.dto.response.QueryAlignmentResponse;
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

    AlignmentController(AlignmentService service) {
        this.alignmentService = service;
    }

    @ApiOperation(value = "Active Site alignment", response = QueryAlignmentResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of active site alignments", response = QueryAlignmentResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "All PDB ids invalid", response = InvalidPdbIdException.class)
    })
    @RequestMapping(value = "/activesite", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QueryAlignmentResponse> activeSiteAlignment(AlignmentRequest alignmentRequest) {
        log.info("Aligning active sites: {}", alignmentRequest);
        return ResponseEntity.ok(alignmentService.alignActiveSites(alignmentRequest));
    }
}
