package org.moltimate.moltimatebackend.service;

import org.biojava.nbio.structure.Structure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.MotifSelection;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.model.ResidueQuerySet;
import org.moltimate.moltimatebackend.response.AlignmentResponse;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
}
