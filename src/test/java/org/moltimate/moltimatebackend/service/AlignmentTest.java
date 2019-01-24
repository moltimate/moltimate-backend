package org.moltimate.moltimatebackend.service;

import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.moltimate.moltimatebackend.model.Motif;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class AlignmentTest {

    @InjectMocks
    private AlignmentService alignmentService;

    @InjectMocks
    private ProteinService proteinService;

    @Mock
    private MotifService motifService;

    private List<Motif> motifs = new ArrayList<>();
    private List<Structure> structures = new ArrayList<>();
    
    @Test
    public void skeletonTest(){
        return;
    }
}
