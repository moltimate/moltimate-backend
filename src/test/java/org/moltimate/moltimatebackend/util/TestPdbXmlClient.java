package org.moltimate.moltimatebackend.util;

import org.junit.*;

import java.util.List;

public class TestPdbXmlClient {
    @Test
    public void testPostEcNumberForPdbIds() {
        List<String> pdbIds = PdbXmlClient.postEcNumberForPdbIds("3.4.21.4");
        Assert.assertTrue(pdbIds.contains("1A0J"));
    }
    @Test
    public void testGetPdbIds() {
        List<String> pdbIds = PdbXmlClient.getPdbIds();
        Assert.assertTrue(pdbIds.contains("1A0J"));
    }

}
