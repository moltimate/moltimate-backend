package org.moltimate.moltimatebackend.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.biojava.nbio.structure.Structure;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;


public class TestQueryResponseData {

    @Test
    public void testQueryResponseData() {
        //depends on StructureUtils.ecNumber Api call to rcsb
        Structure struct = Mockito.mock(Structure.class);
        Mockito.when(struct.getPDBCode()).thenReturn("1a0j");
        QueryResponseData qrd = new QueryResponseData(struct);
        Assert.assertEquals(qrd.getEcNumber(), "3.4.21.4");
        Assert.assertEquals(qrd.getPdbId(), "1a0j");
    }

    @Test
    public void testSuccessfulEntry() {
        Structure struct = Mockito.mock(Structure.class);
        Mockito.when(struct.getPDBCode()).thenReturn("1a0j");
        QueryResponseData qrd = new QueryResponseData(struct);
        Motif motif = Mockito.mock(Motif.class);
        Alignment alignment = Mockito.mock(Alignment.class);
        qrd.addSuccessfulEntry(motif,alignment);
        Assert.assertEquals(1, qrd.getAlignments().size());
    }

    @Test
    public void testFailedEntry() {
        Structure struct = Mockito.mock(Structure.class);
        Mockito.when(struct.getPDBCode()).thenReturn("1a0j");
        QueryResponseData qrd = new QueryResponseData(struct);
        Motif motif = Mockito.mock(Motif.class);
        Alignment alignment = Mockito.mock(Alignment.class);
        qrd.addFailedEntry("1a0j", "3.4.21.4");
        Assert.assertEquals(1, qrd.getFailedAlignments().size());
    }
    @Test
    public void testSimilar() {
        Structure struct = Mockito.mock(Structure.class);
        Mockito.when(struct.getPDBCode()).thenReturn("1a0j");
        QueryResponseData qrd = new QueryResponseData(struct);
        QueryResponseData qrd2 = new QueryResponseData(struct);
        Assert.assertTrue(qrd.similar(qrd2));
        QueryResponseData qrd3 = new QueryResponseData();
        Assert.assertFalse(qrd.similar(qrd3));
        qrd3.setPdbId("1a0j");
        qrd3.setEcNumber("3.4.21.4");
        Assert.assertTrue(qrd.similar(qrd3));
    }

    @Test
    public void testMerge() {
        Structure struct = Mockito.mock(Structure.class);
        Mockito.when(struct.getPDBCode()).thenReturn("1a0j");
        QueryResponseData qrd = new QueryResponseData(struct);
        Motif motif = Mockito.mock(Motif.class);
        Alignment alignment = Mockito.mock(Alignment.class);
        qrd.addSuccessfulEntry(motif,alignment);
        QueryResponseData qrd2 = new QueryResponseData(struct);
        qrd2.addSuccessfulEntry(motif, alignment);
        qrd.addFailedEntry("1mk4", "2.1.4.5");
        qrd2.addFailedEntry("2trq", "1.21.4.3");
        qrd.merge(qrd2);
        Assert.assertEquals(2, qrd.getAlignments().size());
        Assert.assertEquals(2, qrd.getFailedAlignments().size());
    }
}

