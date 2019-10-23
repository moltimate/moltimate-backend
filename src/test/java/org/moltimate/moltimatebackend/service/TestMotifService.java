package org.moltimate.moltimatebackend.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.ResidueQuerySet;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.repository.ResidueQuerySetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class TestMotifService {
	@InjectMocks
	private MotifService motifService;

	@Mock
	private MotifRepository motifRepository;

	@Mock
	private ResidueQuerySetRepository residueQuerySetRepository;

	@Test
	public void testSaveMotifs() {
		Motif motif1 = Mockito.mock( Motif.class );
		Motif motif2 = Mockito.mock( Motif.class );

		List<Motif> motifs = Arrays.asList( motif1, motif2 );

		Mockito.when( motif1.getPdbId() ).thenReturn( "1a0j" );
		Mockito.when( motif2.getPdbId() ).thenReturn( "1sdf" );

		ResidueQuerySet set1 = Mockito.mock( ResidueQuerySet.class );
		ResidueQuerySet set2 = Mockito.mock( ResidueQuerySet.class );

		Map<String, ResidueQuerySet> resSet1 = new HashMap<>();
		resSet1.put( "ALA", set1 );
		resSet1.put( "ASN", set2 );

		Mockito.when( motif1.getSelectionQueries() ).thenReturn( resSet1 );

		ResidueQuerySet set3 = Mockito.mock( ResidueQuerySet.class );
		ResidueQuerySet set4 = Mockito.mock( ResidueQuerySet.class );

		Map<String, ResidueQuerySet> resSet2 = new HashMap<>();
		resSet1.put( "CYS", set3 );
		resSet1.put( "GLN", set4 );

		Mockito.when( motif2.getSelectionQueries() ).thenReturn( resSet2 );

		motifService.saveMotifs( motifs );
	}

	@Test
	public void testQueryByPdbId() {
		Motif motif = Mockito.mock( Motif.class );
		Mockito.when( motifRepository.findByPdbIdIgnoreCase( any() ) ).thenReturn( motif );
		Assert.assertEquals( motif, motifService.queryByPdbId( "1a0j" ) );
	}

	@Test
	public void testQueryByEcNumber() {
		Page<Motif> page1 = Mockito.mock( Page.class );
		Page<Motif> page2 = Mockito.mock( Page.class );
		Mockito.when( motifRepository.findAll( (Pageable) any() ) ).thenReturn( page1 );
		Mockito.when( motifRepository.findByEcNumberEqualsOrEcNumberStartingWith( any(), any(), any() ) ).thenReturn( page2 );
		Assert.assertEquals( page1, motifService.queryByEcNumber( null, 1 ) );
		Assert.assertEquals( page2, motifService.queryByEcNumber( "1.45.3.2", 1 ) );
	}

	// updateMotifs could not be tested because it is too closely tied to ActiveSiteUtils to test in isolation.
}
