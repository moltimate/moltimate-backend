package org.moltimate.moltimatebackend.dto.response;

import org.biojava.nbio.structure.Structure;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;

import java.util.Arrays;

public class TestMotifAlignmentResponse {

    @Test
    public void testMotifAlignmentResponse() {
        Motif motif = Mockito.mock(Motif.class);
        Residue residue = Mockito.mock(Residue.class);
        Mockito.when(motif.getActiveSiteResidues()).thenReturn(Arrays.asList(residue));
        Mockito.when(motif.getEcNumber()).thenReturn("3.4.21.4");
        Mockito.when(motif.getPdbId()).thenReturn("1a0j");
        MotifAlignmentResponse mar = new MotifAlignmentResponse(motif);
        Assert.assertEquals("1a0j", mar.getPdbId());
        Assert.assertEquals("3.4.21.4", mar.getEcNumber());
        Assert.assertEquals(1, mar.getActiveSiteResidues().size());
    }

    @Test
    public void testAddSuccessfulEntry() {
        Motif motif = Mockito.mock(Motif.class);
        Residue residue = Mockito.mock(Residue.class);
        Mockito.when(motif.getActiveSiteResidues()).thenReturn(Arrays.asList(residue));
        Mockito.when(motif.getEcNumber()).thenReturn("3.4.21.4");
        Mockito.when(motif.getPdbId()).thenReturn("1a0j");
        MotifAlignmentResponse mar = new MotifAlignmentResponse(motif);
        Structure struct = Mockito.mock(Structure.class);
        Mockito.when(struct.getPDBCode()).thenReturn("1a0j");
        Alignment alignment = Mockito.mock(Alignment.class);
        Mockito.when(alignment.getRmsd()).thenReturn(0.23);
        Mockito.when(alignment.getLevenshtein()).thenReturn(0);
        Mockito.when(alignment.getAlignedResidues()).thenReturn(Arrays.asList(residue));
        mar.addSuccessfulEntry(struct, alignment);
        Assert.assertEquals(1, mar.getActiveSiteResidues().size());
    }
    @Test
    public void testAddFailedEntry() {
        Motif motif = Mockito.mock(Motif.class);
        Residue residue = Mockito.mock(Residue.class);
        Mockito.when(motif.getActiveSiteResidues()).thenReturn(Arrays.asList(residue));
        Mockito.when(motif.getEcNumber()).thenReturn("3.4.21.4");
        Mockito.when(motif.getPdbId()).thenReturn("1a0j");
        MotifAlignmentResponse mar = new MotifAlignmentResponse(motif);
        mar.addFailedEntry("1a0j", "3.4.21.4");
        int sizeFailed = mar.getFailedAlignments().size();
        Assert.assertEquals(1, sizeFailed);
    }
    @Test
    public void testAddFailedPdbId() {
        Motif motif = Mockito.mock(Motif.class);
        Residue residue = Mockito.mock(Residue.class);
        Mockito.when(motif.getActiveSiteResidues()).thenReturn(Arrays.asList(residue));
        Mockito.when(motif.getEcNumber()).thenReturn("3.4.21.4");
        Mockito.when(motif.getPdbId()).thenReturn("1a0j");
        MotifAlignmentResponse mar = new MotifAlignmentResponse(motif);
        mar.addFailedPdbId("1a0j");
        Assert.assertEquals(1, mar.getFailedPdbIds().size());
        mar.addFailedPdbIds(Arrays.asList("1MK4", "2trq"));
        Assert.assertEquals(3, mar.getFailedPdbIds().size());

    }

}
