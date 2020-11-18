package org.moltimate.moltimatebackend.util;

import org.junit.Assert;
import org.junit.Test;
import org.moltimate.moltimatebackend.model.ActiveSite;

import java.util.List;

public class TestActiveSiteUtils {

	@Test
	public void testGetActiveSites() {
		List<ActiveSite> testSites = ActiveSiteUtils.getActiveSites();
		Assert.assertNotNull( testSites );
	}
}
