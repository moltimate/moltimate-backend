package org.moltimate.moltimatebackend.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.Request.MotifTestRequest;
import org.moltimate.moltimatebackend.dto.Alignment.AlignmentMotifResponse;
import org.moltimate.moltimatebackend.service.MotifTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.beans.PropertyEditorSupport;

@RestController
@RequestMapping(value = "/test")
@Slf4j
@Api(value = "/test", description = "Alignment Controller", produces = "application/json")
public class MotifTestController {

    @Autowired
    private MotifTestService motifTestService;

    @RequestMapping(value = "/motif", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlignmentMotifResponse> testMotif(MotifTestRequest motifTestRequest) {
        log.info("Received request to test motif: " + motifTestRequest);
        return ResponseEntity.ok(motifTestService.testMotifAlignment(motifTestRequest));
    }

    /**
     * Ignore case sensitivity when parsing test type
     */
    @InitBinder
    public void initBinder(final WebDataBinder webdataBinder) {
        webdataBinder.registerCustomEditor(MotifTestRequest.Type.class, new TestTypeConverter());
    }

    private class TestTypeConverter extends PropertyEditorSupport {
        public void setAsText(final String text) throws IllegalArgumentException {
            setValue(MotifTestRequest.Type.fromName(text));
        }
    }
}
