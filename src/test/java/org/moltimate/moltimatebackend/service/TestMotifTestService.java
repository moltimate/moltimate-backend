package org.moltimate.moltimatebackend.service;

import org.biojava.nbio.structure.Structure;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.moltimate.moltimatebackend.dto.request.MotifTestRequest;

import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.repository.ResidueQuerySetRepository;

import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class TestMotifTestService {

    @InjectMocks
    private MotifTestService motifTestService;

    @Mock
    private MotifService motifService;

    @Mock
    private MotifRepository motifRepository;

    @Mock
    private ResidueQuerySetRepository residueQuerySetRepository;

    @Test
    public void testMotifAlignment() {

        MotifTestRequest motifTestRequest = Mockito.mock(MotifTestRequest.class);
        Structure structure = Mockito.mock( Structure.class );
        Mockito.when( structure.getPDBCode() ).thenReturn( "1a0j" );
        Mockito.when(motifTestRequest.motifStructure()).thenReturn(structure);
        Mockito.when(motifTestRequest.getPdbId()).thenReturn("1a0j");
        Mockito.when(motifTestRequest.getEcNumber()).thenReturn("3.4.21.4");
        Residue residue = Mockito.mock(Residue.class);
        List<Residue> residues = new ArrayList<>();
        residues.add(residue);
        Mockito.when(motifTestRequest.parseResidueEntries()).thenReturn(residues);

        //motifTestService.testMotifAlignment(motifTestRequest);

    }

}
