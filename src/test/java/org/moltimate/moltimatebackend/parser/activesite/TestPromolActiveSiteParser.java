package org.moltimate.moltimatebackend.parser.activesite;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moltimate.moltimatebackend.model.ActiveSite;

import java.util.List;

public class TestPromolActiveSiteParser {
	private ActiveSiteParser parser;

	@Before
	public void setUp() {
		parser = new PromolActiveSiteParser();
	}

	@Test
	public void testParseMotifs() {
		List<ActiveSite> activeSites = parser.parseMotifs();
		Assert.assertNotNull( activeSites );
		Assert.assertFalse( activeSites.isEmpty() );
		Assert.assertNotNull( activeSites.get( 0 ).getPdbId() );
		Assert.assertNotNull( activeSites.get( 0 ).getResidues() );
	}
}
