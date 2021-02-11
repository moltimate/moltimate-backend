package org.moltimate.moltimatebackend.model.ligand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PDBQT {
	private String compound;
	private List<String> remarks;
	private List<Atom> root;
	private List<Branch> branches;
	private int torsDOF;

	public static PDBQT createLigand(String pdbqt ) {
		String[] lines = pdbqt.split("\n");
		PDBQT PDBQT = new PDBQT();
		PDBQT.setRemarks( new ArrayList<>() );
		PDBQT.setRoot( new ArrayList<>() );
		PDBQT.setBranches( new ArrayList<>() );

		boolean buildingRoot = false;
		LinkedList<Branch> branchStack = new LinkedList<>();

		for( String line: lines ) {
			String command = line.substring( 0, line.indexOf(" ") ).trim();
			switch( command.trim() ){
				case( "COMPND" ):
					PDBQT.setCompound( line.substring( 6 ).trim() );
					break;
				case( "REMARK" ):
					PDBQT.getRemarks().add( line.substring( 6 ).trim() );
					break;
				case( "ROOT" ):
					buildingRoot = true;
					break;
				case( "ATOM" ):
				case( "HETATM" ):
					if( buildingRoot ) {
						PDBQT.getRoot().add( new Atom( line ) );
					}
					else {
						branchStack.getFirst().getAtoms().add( new Atom( line ) );
					}
					break;
				case( "ENDROOT" ):
					buildingRoot = false;
					break;
				case( "BRANCH" ):
					String numbers = line.substring( 6 ).trim();
					String start = numbers.substring( 0, numbers.indexOf( " " ) );
					String end = numbers.substring( numbers.indexOf(" ") ).trim();
					Branch newBranch = new Branch();
					newBranch.setStart( Integer.parseInt( start ) );
					newBranch.setEnd( Integer.parseInt( end ) );
					if( branchStack.isEmpty() ) {
						branchStack.push(newBranch);
						PDBQT.getBranches().add( newBranch );
					}
					else {
						branchStack.getFirst().getEmbeddedBranches().add( newBranch );
						branchStack.push( newBranch );
					}
					break;
				case( "ENDBRANCH" ):
					branchStack.pop();
					break;
				case( "TORSDOF" ):
					PDBQT.setTorsDOF( Integer.parseInt( line.substring( 7 ).trim() ) );
					break;
			}
		}
		return PDBQT;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( String.format( "COMPND  %-72s\n", compound ) );
		for( String remark: remarks ) {
			builder.append( String.format( "REMARK  %-72s\n", remark ) );
		}
		builder.append(String.format( "%-80s\n", "ROOT" ) );
		for( Atom atom: root ) {
			builder.append( atom.toString() );
		}
		builder.append(String.format( "%-80s\n", "ENDROOT" ) );
		for( Branch branch: branches ) {
			builder.append( branch.toString() );
		}
		builder.append( String.format( "TORSDOF %-72d\n", torsDOF ) );
		return builder.toString();
	}
}
