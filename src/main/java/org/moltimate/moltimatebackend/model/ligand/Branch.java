package org.moltimate.moltimatebackend.model.ligand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
	private List<Atom> atoms = new ArrayList<>();
	private List<Branch> embeddedBranches = new ArrayList<>();
	private int start;
	private int end;

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( String.format( "BRANCH  %5d  %5d%60s\n", start, end, "" ) );
		for( Atom atom: atoms ) {
			builder.append( atom.toString() );
		}
		for( Branch subBranch: embeddedBranches ) {
			builder.append( subBranch.toString() );
		}
		builder.append( String.format( "ENDBRANCH  %5d  %5d%57s\n", start, end, "" ) );
		return builder.toString();
	}
}
