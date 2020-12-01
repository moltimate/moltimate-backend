package org.moltimate.moltimatebackend.dto.request;

import org.biojava.nbio.structure.Structure;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.dto.response.PdbQueryResponse;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.ProteinUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class TestMotifTestRequest {

    @Test
    public void testMotifStructure() {
        MotifTestRequest mtr = new MotifTestRequest();
        mtr.setPdbId("1a0j");
        Structure struct = mtr.motifStructure();
        Assert.assertEquals("1A0J", struct.getPDBCode());
    }

    @Test
    public void testGetType() {
        MotifTestRequest mtr = new MotifTestRequest();
        Assert.assertEquals(MotifTestRequest.Type.SELF, mtr.getType());
        mtr.setType(MotifTestRequest.Type.HOMOLOG);
        Assert.assertEquals(MotifTestRequest.Type.HOMOLOG, mtr.getType());
    }

    @Test
    public void testGetPrecisionFactor() {
        MotifTestRequest mtr = new MotifTestRequest();
        mtr.setPrecisionFactor(10);
        Assert.assertEquals(10, mtr.getPrecisionFactor());
        mtr.setPrecisionFactor(-1);
        Assert.assertEquals(1, mtr.getPrecisionFactor());
    }
    @Test
    public void testGetRandomCount() {
        MotifTestRequest mtr = new MotifTestRequest();
        mtr.setRandomCount(10);
        Assert.assertEquals(10, mtr.getRandomCount());
        mtr.setRandomCount(-1);
        Assert.assertEquals(1, mtr.getRandomCount());
    }

   @Test
    public void testResidueList() {
        MotifTestRequest mtr = new MotifTestRequest();
        mtr.setActiveSiteResidues(Arrays.asList("ALA A 100", "ASN A 50"));
        List<Residue> residues = mtr.parseResidueEntries();
        Assert.assertEquals(2, residues.size());
   }
}
