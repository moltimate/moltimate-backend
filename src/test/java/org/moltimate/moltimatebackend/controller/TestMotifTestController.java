package org.moltimate.moltimatebackend.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.dto.request.MotifTestRequest;
import org.moltimate.moltimatebackend.dto.response.MotifAlignmentResponse;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.service.MotifTestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class TestMotifTestController {
    private MotifTestService service;
    private MotifTestController controller;


    @Before
    public void setUp() {
        service = Mockito.mock( MotifTestService.class );
        controller = new MotifTestController(service);
    }

    @After
    public void tearDown() {
        service = null;
        controller = null;
    }

    @Test
    public void testOKStatus() {
        Motif motif1 = Mockito.mock( Motif.class );
        Mockito.when( motif1.getPdbId() ).thenReturn( "1a0j" );
        Mockito.when(motif1.getEcNumber()).thenReturn("3.4.21.4");
        Residue residue = Mockito.mock(Residue.class);
        List<Residue> residues = new ArrayList<>();
        residues.add(residue);
        Mockito.when( motif1.getActiveSiteResidues() ).thenReturn( residues);

        MotifAlignmentResponse motifAlignment = Mockito.mock(MotifAlignmentResponse.class);
        Mockito.when(service.testMotifAlignment(any())).thenReturn(motifAlignment);

        ResponseEntity<MotifAlignmentResponse> response = controller.testMotif( new MotifTestRequest());
        Assert.assertEquals( HttpStatus.OK, response.getStatusCode() );
    }


}
