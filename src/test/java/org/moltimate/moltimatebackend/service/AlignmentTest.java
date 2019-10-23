package org.moltimate.moltimatebackend.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.biojava.nbio.structure.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.dto.alignment.SuccessfulAlignment;
import org.moltimate.moltimatebackend.dto.request.AlignmentRequest;
import org.moltimate.moltimatebackend.dto.response.PdbQueryResponse;
import org.moltimate.moltimatebackend.dto.response.QueryAlignmentResponse;
import org.moltimate.moltimatebackend.dto.response.QueryResponseData;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import javax.vecmath.Point3d;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
public class AlignmentTest {

    @InjectMocks
    private AlignmentService alignmentService;

    @Mock
    private MotifService motifService;

    @Mock
    private CacheService cacheService;

    private List<Motif> motifs = new ArrayList<>();
    private List<Structure> structures = new ArrayList<>();

    @Test
    public void testAlignActiveSitesRequest() {
        AlignmentRequest request = Mockito.mock( AlignmentRequest.class );
        Mockito.when( request.getPdbIds() ).thenReturn( Arrays.asList( "1a0j", "d3g1" ) );
        Mockito.when( request.getOptions() ).thenReturn( Collections.emptyList() );
        Mockito.when( request.getFilters() ).thenReturn(Collections.singletonList("1.34.5.2"));
        Mockito.when( request.getCustomMotifs() ).thenReturn( Collections.emptyList() );
        Mockito.when( request.getEcNumber() ).thenReturn( "1.34.5.2" );
        Mockito.when( request.getPrecisionFactor() ).thenReturn( .1 );

        Structure structure = Mockito.mock( Structure.class );
        Mockito.when( request.callPdbForResponse() ).thenReturn( new PdbQueryResponse(
                Collections.singletonList( structure ),
                Collections.singletonList( "1a0j" ),
                Collections.singletonList( "d3g1" )
        ) );
        Mockito.when( request.extractCustomMotifFileList() ).thenReturn( Collections.emptyList() );
        Mockito.when( structure.getPDBCode() ).thenReturn( "1a0j" );

        QueryAlignmentResponse alignmentResponse = Mockito.mock( QueryAlignmentResponse.class );
        cacheService.cache = Mockito.mock( Cache.class );
        Mockito.when( cacheService.cache.getIfPresent( any() ) ).thenReturn( alignmentResponse );
        QueryResponseData data = Mockito.mock( QueryResponseData.class );
        Mockito.when( data.getAlignments() ).thenReturn( Collections.singletonList( new SuccessfulAlignment() ) );
        Mockito.when( data.getFailedAlignments() ).thenReturn( Collections.emptyList() );
        Mockito.when( data.getEcNumber() ).thenReturn( "1.34.5.2" );
        Mockito.when( data.getPdbId() ).thenReturn( "1a0j" );
        Mockito.when( data.clone() ).thenReturn( data );
        Mockito.when( alignmentResponse.getEntries() ).thenReturn( Collections.singletonList( data ) );
        Mockito.when( alignmentResponse.clone() ).thenReturn( alignmentResponse );
        QueryAlignmentResponse activeSiteResponse = alignmentService.alignActiveSites( request );
        Assert.assertNotNull( activeSiteResponse );
        Assert.assertEquals( 1, activeSiteResponse.getEntries().size() );
        Assert.assertEquals( 1, activeSiteResponse.getFailedPdbIds().size() );

        Mockito.when( request.getEcNumber() ).thenReturn( null );
        Mockito.when( cacheService.cache.get( any(), any() ) ).thenReturn( alignmentResponse );

        activeSiteResponse = alignmentService.alignActiveSites( request );
        Assert.assertNotNull( activeSiteResponse );
        Assert.assertEquals( 1, activeSiteResponse.getEntries().size() );
        Assert.assertEquals( 1, activeSiteResponse.getFailedPdbIds().size() );

        Mockito.when( request.getEcNumber() ).thenReturn( "1.34.5.2" );
        Mockito.when( cacheService.cache.getIfPresent( any() ) ).thenReturn( null );
        Mockito.when( cacheService.findQueryAlignmentResponse( any() ) ).thenReturn( alignmentResponse );

        activeSiteResponse = alignmentService.alignActiveSites( request );
        Assert.assertNotNull( activeSiteResponse );
        Assert.assertEquals( 1, activeSiteResponse.getEntries().size() );
        Assert.assertEquals( 1, activeSiteResponse.getFailedPdbIds().size() );

        Mockito.when( cacheService.findQueryAlignmentResponse( any() ) ).thenReturn( null );
        Page<Motif> motifs = Mockito.mock( Page.class );
        Mockito.when( motifs.getTotalElements() ).thenReturn( Long.valueOf( 1 ) );
        Mockito.when( motifService.queryByEcNumber( any(), eq( 0 ) ) ).thenReturn( motifs );

        activeSiteResponse = alignmentService.alignActiveSites( request );
        Assert.assertNotNull( activeSiteResponse );
        Assert.assertEquals( 0, activeSiteResponse.getEntries().size() );
        Assert.assertEquals( 1, activeSiteResponse.getFailedPdbIds().size() );

        Mockito.when( motifs.hasContent() ).thenReturn( true );
        Page<Motif> stopMotif = Mockito.mock( Page.class );
        Mockito.when( stopMotif.hasContent() ).thenReturn( false );
        Mockito.when( motifService.queryByEcNumber( any(), eq( 1 ) ) ).thenReturn( stopMotif );

        activeSiteResponse = alignmentService.alignActiveSites( request );
        Assert.assertNotNull( activeSiteResponse );
        Assert.assertEquals( 0, activeSiteResponse.getEntries().size() );
        Assert.assertEquals( 1, activeSiteResponse.getFailedPdbIds().size() );
    }

    @Test
    public void testActiveSitesNoRequest() {
        Structure queryStructure = Mockito.mock( Structure.class );
        Motif motif = Mockito.mock( Motif.class );
        Structure motifStructure = Mockito.mock( Structure.class );

        Residue residue1 = Mockito.mock( Residue.class );
        Mockito.when( residue1.getResidueName() ).thenReturn( "ALA" );
        Mockito.when( residue1.getResidueId() ).thenReturn( "Ala" );
        Residue residue2 = Mockito.mock( Residue.class );
        Mockito.when( residue2.getResidueName() ).thenReturn( "ASN" );
        Mockito.when( residue2.getResidueId() ).thenReturn( "Asn" );
        Residue residue3 = Mockito.mock( Residue.class );
        Mockito.when( residue3.getResidueName() ).thenReturn( "CYS" );
        Mockito.when( residue3.getResidueId() ).thenReturn( "Cys" );

        Mockito.when( motif.getActiveSiteResidues() ).thenReturn( Arrays.asList( residue1, residue2, residue3 ) );

        Map<Residue, List<Group>> residueMap = new HashMap<>();
        residueMap.put( residue1, new ArrayList<>() );

        Group group1 = Mockito.mock( Group.class );
        ResidueNumber num1 = Mockito.mock( ResidueNumber.class );
        Mockito.when( num1.toString() ).thenReturn( "Ala" );
        Mockito.when( group1.getResidueNumber() ).thenReturn( num1 );
        Mockito.when( group1.getPDBName() ).thenReturn( "Ala" );
        Mockito.when( num1.getSeqNum() ).thenReturn( 1 );

        Group group2 = Mockito.mock( Group.class );
        ResidueNumber num2 = Mockito.mock( ResidueNumber.class );
        Mockito.when( num2.toString() ).thenReturn( "Arg" );
        Mockito.when( group2.getResidueNumber() ).thenReturn( num2 );
        Mockito.when( group2.getPDBName() ).thenReturn( "Arg" );
        Mockito.when( num2.getSeqNum() ).thenReturn( 2 );

        residueMap.get( residue1 ).add( group1 );
        residueMap.get( residue1 ).add( group2 );

        residueMap.put( residue2, new ArrayList<>() );

        Group group3 = Mockito.mock( Group.class );
        ResidueNumber num3 = Mockito.mock( ResidueNumber.class );
        Mockito.when( num3.toString() ).thenReturn( "Asn" );
        Mockito.when( group3.getResidueNumber() ).thenReturn( num3 );
        Mockito.when( group3.getPDBName() ).thenReturn( "Asn" );
        Mockito.when( num3.getSeqNum() ).thenReturn( 3 );

        Group group4 = Mockito.mock( Group.class );
        ResidueNumber num4 = Mockito.mock( ResidueNumber.class );
        Mockito.when( num4.toString() ).thenReturn( "Asp" );
        Mockito.when( group4.getResidueNumber() ).thenReturn( num4 );
        Mockito.when( group4.getPDBName() ).thenReturn( "Asp" );
        Mockito.when( num4.getSeqNum() ).thenReturn( 4 );

        residueMap.get( residue2 ).add( group3 );
        residueMap.get( residue2 ).add( group4 );

        residueMap.put( residue3, new ArrayList<>() );

        Group group5 = Mockito.mock( Group.class );
        ResidueNumber num5 = Mockito.mock( ResidueNumber.class );
        Mockito.when( num5.toString() ).thenReturn( "Cys" );
        Mockito.when( group5.getResidueNumber() ).thenReturn( num5 );
        Mockito.when( group5.getPDBName() ).thenReturn( "Cys" );
        Mockito.when( num5.getSeqNum() ).thenReturn( 5 );

        Group group6 = Mockito.mock( Group.class );
        ResidueNumber num6 = Mockito.mock( ResidueNumber.class );
        Mockito.when( num6.toString() ).thenReturn( "Gln" );
        Mockito.when( group6.getResidueNumber() ).thenReturn( num6 );
        Mockito.when( group6.getPDBName() ).thenReturn( "Gln" );
        Mockito.when( num6.getSeqNum() ).thenReturn( 6 );

        residueMap.get( residue3 ).add( group5 );
        residueMap.get( residue3 ).add( group6 );

        Mockito.when( motif.runQueries( any(), anyDouble() ) ).thenReturn( residueMap );

        Atom atom1 = Mockito.mock( Atom.class );
        Atom atom2 = Mockito.mock( Atom.class );
        Atom atom3 = Mockito.mock( Atom.class );
        Atom atom4 = Mockito.mock( Atom.class );
        Atom atom5 = Mockito.mock( Atom.class );
        Atom atom6 = Mockito.mock( Atom.class );

        Mockito.when( atom1.getName() ).thenReturn( "H" );
        Mockito.when( atom2.getName() ).thenReturn( "D" );
        Mockito.when( atom3.getName() ).thenReturn( "N" );
        Mockito.when( atom4.getName() ).thenReturn( "C" );
        Mockito.when( atom5.getName() ).thenReturn( "O" );
        Mockito.when( atom6.getName() ).thenReturn( "Au" );

        Mockito.when( atom1.getCoordsAsPoint3d() ).thenReturn( new Point3d( 0, 0, 0 ) );
        Mockito.when( atom2.getCoordsAsPoint3d() ).thenReturn( new Point3d( 0, 1, 0 ) );
        Mockito.when( atom3.getCoordsAsPoint3d() ).thenReturn( new Point3d( 0, 0, 0 ) );
        Mockito.when( atom4.getCoordsAsPoint3d() ).thenReturn( new Point3d( 0, 1, 0 ) );
        Mockito.when( atom5.getCoordsAsPoint3d() ).thenReturn( new Point3d( 0, 0, 0 ) );
        Mockito.when( atom6.getCoordsAsPoint3d() ).thenReturn( new Point3d( 0, 1, 0 ) );

        List<Atom> atoms = Arrays.asList( atom1, atom2, atom3, atom4, atom5, atom6 );

        Mockito.when( group1.getAtoms() ).thenReturn( atoms );
        Mockito.when( group2.getAtoms() ).thenReturn( atoms );
        Mockito.when( group3.getAtoms() ).thenReturn( atoms );
        Mockito.when( group4.getAtoms() ).thenReturn( atoms );
        Mockito.when( group5.getAtoms() ).thenReturn( atoms );
        Mockito.when( group6.getAtoms() ).thenReturn( atoms );

        Chain chain1 = Mockito.mock( Chain.class );
        Chain chain2 = Mockito.mock( Chain.class );
        Chain chain3 = Mockito.mock( Chain.class );
        Mockito.when( motifStructure.getChains() ).thenReturn( Arrays.asList( chain1, chain2, chain3 ) );
        Mockito.when( chain1.getAtomGroups( GroupType.AMINOACID ) ).thenReturn( Arrays.asList( group1, group2 ) );
        Mockito.when( chain2.getAtomGroups( GroupType.AMINOACID ) ).thenReturn( Arrays.asList( group3, group4 ) );
        Mockito.when( chain3.getAtomGroups( GroupType.AMINOACID ) ).thenReturn( Arrays.asList( group5, group6 ) );

        Alignment alignment = alignmentService.alignActiveSites( queryStructure, motif, motifStructure, .1 );
    }
}
