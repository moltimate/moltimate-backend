package org.moltimate.moltimatebackend.model;

import org.biojava.nbio.structure.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class MotifTest {

    private Motif motif1;

    @Before
    public void init(){

        String pdbId = "0001";
        String ecId = "1.1.1.1";

        MotifSelection selection1 = mock(MotifSelection.class);
        when(selection1.getAtomType1()).thenReturn("N");
        when(selection1.getAtomType2()).thenReturn("N");
        when(selection1.getResidueName1()).thenReturn("ALA");
        when(selection1.getResidueName2()).thenReturn("L");
        when(selection1.getDistance()).thenReturn(2.0);

        MotifSelection selection2 = mock(MotifSelection.class);
        when(selection2.getAtomType1()).thenReturn("N");
        when(selection2.getAtomType2()).thenReturn("CA");
        when(selection2.getResidueName1()).thenReturn("ALA");
        when(selection2.getResidueName2()).thenReturn("L");
        when(selection2.getDistance()).thenReturn(2.0);

        MotifSelection selection3 = mock(MotifSelection.class);
        when(selection3.getAtomType1()).thenReturn("CA");
        when(selection3.getAtomType2()).thenReturn("N");
        when(selection3.getResidueName1()).thenReturn("ALA");
        when(selection3.getResidueName2()).thenReturn("L");
        when(selection3.getDistance()).thenReturn(2.0);

        MotifSelection selection4 = mock(MotifSelection.class);
        when(selection4.getAtomType1()).thenReturn("CA");
        when(selection4.getAtomType2()).thenReturn("CA");
        when(selection4.getResidueName1()).thenReturn("ALA");
        when(selection4.getResidueName2()).thenReturn("L");
        when(selection4.getDistance()).thenReturn(2.0);

        List<MotifSelection> alaSelections = new ArrayList<>();
        alaSelections.add(selection1);
        alaSelections.add(selection2);
        alaSelections.add(selection3);
        alaSelections.add(selection4);

        ResidueQuerySet alaQuerySet = mock (ResidueQuerySet.class);
        when(alaQuerySet.getSelections()).thenReturn(alaSelections);

        Map<String, ResidueQuerySet> selectionQueries = new HashMap<>();
        selectionQueries.put("ALA",alaQuerySet);

        Residue alaResidue = mock(Residue.class);
        when(alaResidue.getIdentifier()).thenReturn("ALA");

        Residue lResidue = mock(Residue.class);
        when(lResidue.getIdentifier()).thenReturn("L");

        List<Residue> activeSiteResidues = new ArrayList<>();
        activeSiteResidues.add(alaResidue);
        activeSiteResidues.add(lResidue);

        motif1 = new Motif(pdbId,ecId,selectionQueries, activeSiteResidues);

    }

    @Test //Note: cannot isolate Motif from StructureUtils without refactoring
    public void runMotifQueriesTest(){

        Atom n1 = mock(Atom.class);
        when(n1.getName()).thenReturn("N");
        when(n1.getCoords()).thenReturn(new double[]{0.0, 0.0});

        Atom ca1 = mock(Atom.class);
        when(ca1.getName()).thenReturn("CA");
        when(ca1.getCoords()).thenReturn(new double[]{1.0, 0.0});

        Atom n2 = mock(Atom.class);
        when(n2.getName()).thenReturn("N");
        when(n2.getCoords()).thenReturn(new double[]{0.0, 1.0});

        Atom ca2 = mock(Atom.class);
        when(ca2.getName()).thenReturn("CA");
        when(ca2.getCoords()).thenReturn(new double[]{1.0, 1.0});

        List<Atom> atoms1 = new ArrayList<>();
        atoms1.add(n1);
        atoms1.add(ca1);

        List<Atom> atoms2 = new ArrayList<>();
        atoms2.add(n2);
        atoms2.add(ca2);

        Group groupAla = mock(Group.class);
        when(groupAla.getPDBName()).thenReturn("ALA");
        when(groupAla.getAtoms()).thenReturn(atoms1);
        when(n1.getGroup()).thenReturn(groupAla);
        when(ca1.getGroup()).thenReturn(groupAla);

        Group groupL = mock(Group.class);
        when(groupL.getPDBName()).thenReturn("L");
        when(groupL.getAtoms()).thenReturn(atoms2);
        when(n2.getGroup()).thenReturn(groupL);
        when(ca2.getGroup()).thenReturn(groupL);

        ArrayList<Group> groupsArrayList = new ArrayList<>();
        groupsArrayList.add(groupAla);
        groupsArrayList.add(groupL);

        Chain aminoAcidChain = mock(Chain.class);
        when(aminoAcidChain.getAtomGroups(GroupType.AMINOACID)).thenReturn(groupsArrayList);

        List<Chain> chains = new ArrayList<>();
        chains.add(aminoAcidChain);

        Structure protein = mock(Structure.class);
        when(protein.getChains()).thenReturn(chains);

        Map<Residue, List<Group>> queryResults;

        queryResults = motif1.runQueries(protein);

        Assert.assertEquals( 1, queryResults.size() );
    }
}
