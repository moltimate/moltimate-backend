package org.moltimate.moltimatebackend.model;

import org.junit.Assert;
import org.junit.Test;

public class TestMotifSelection {
    @Test
    public void testMotifSelection() {
        MotifSelection ms = new MotifSelection();
        ms.setAtomType1("C");
        ms.setAtomType2("H");
        ms.setDistance(0.03);
        ms.setResidueName1("ASA");
        ms.setResidueName2("GLY");
        Assert.assertEquals("C", ms.getAtomType1());
        Assert.assertEquals("H", ms.getAtomType2());
        assert(0.03 == ms.getDistance());
        Assert.assertEquals("ASA", ms.getResidueName1());
        Assert.assertEquals("GLY", ms.getResidueName2());
    }

}
