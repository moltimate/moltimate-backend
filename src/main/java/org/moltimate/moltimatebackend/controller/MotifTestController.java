package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.ActiveSiteAlignmentResponse;
import org.moltimate.moltimatebackend.dto.MotifTestRequest;
import org.moltimate.moltimatebackend.service.MotifTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
@Slf4j
@Api(value = "/test", description = "Alignment Controller", produces = "application/json")
public class MotifTestController {

    @Autowired
    private MotifTestService motifTestService;

    @RequestMapping(value = "/motif", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActiveSiteAlignmentResponse> testMotif(MotifTestRequest motifTestRequest) {
        return ResponseEntity.ok(motifTestService.testMotifAlignment(motifTestRequest));
    }
}
