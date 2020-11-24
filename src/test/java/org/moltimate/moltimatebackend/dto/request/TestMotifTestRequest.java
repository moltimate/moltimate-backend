package org.moltimate.moltimatebackend.dto.request;

import org.biojava.nbio.structure.Structure;
import org.mockito.Mockito;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moltimate.moltimatebackend.exception.InvalidPdbIdException;
import org.moltimate.moltimatebackend.model.MotifTest;

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
    public void TestGetRandomCount() {
        MotifTestRequest mtr = new MotifTestRequest();
        mtr.setRandomCount(10);
        Assert.assertEquals(10, mtr.getRandomCount());
        mtr.setRandomCount(-1);
        Assert.assertEquals(1, mtr.getRandomCount());
    }
}
