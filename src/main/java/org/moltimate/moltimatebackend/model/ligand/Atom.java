package org.moltimate.moltimatebackend.model.ligand;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Atom {
	private int serialNum;
	private String name;
	private String altLoc;
	private String resName;
	private String chainID;
	private int resNum;
	private String insCode;
	private double x;
	private double y;
	private double z;
	private double occupancy;
	private double tempFactor;
	private String footnote;
	private double partialCharge;
	private String atomType;
	private String type;

	@Setter( AccessLevel.NONE )
	@Getter( AccessLevel.NONE )
	private final int ATOM = 6;

	public Atom( String atomDef ) {
		this.type = atomDef.substring( 0, ATOM ).trim();
		String trimmed = atomDef.substring( ATOM );
		try {
			serialNum = Integer.parseInt(trimmed.substring(0, 5).trim());
		} catch( NumberFormatException e ) {
			serialNum = 0;
		}
		name = trimmed.substring( 6, 10 ).trim();
		altLoc = trimmed.substring( 10, 11 ).trim();
		resName = trimmed.substring( 11, 14 ).trim();
		chainID = trimmed.substring( 15, 16 ).trim();
		try {
			resNum = Integer.parseInt(trimmed.substring(16, 20).trim());
		} catch( NumberFormatException e ) {
			resNum = 0;
		}
		insCode = trimmed.substring( 20, 21 ).trim();
		try {
			x = Double.parseDouble(trimmed.substring(24, 32).trim());
		} catch( NumberFormatException e ) {
			x = 0;
		}
		try {
			y = Double.parseDouble(trimmed.substring(32, 40).trim());
		} catch( NumberFormatException e ) {
			y = 0;
		}
		try {
			z = Double.parseDouble(trimmed.substring(40, 48).trim());
		} catch( NumberFormatException e ) {
			z = 0;
		}
		try {
			occupancy = Double.parseDouble(trimmed.substring(48, 54).trim());
		} catch( NumberFormatException e ) {
			occupancy = 0;
		}
		try {
			tempFactor = Double.parseDouble(trimmed.substring(54, 60).trim());
		} catch( NumberFormatException e ) {
			tempFactor = 0;
		}
		footnote = trimmed.substring( 60, 64 ).trim();
		try {
			partialCharge = Double.parseDouble(trimmed.substring(64, 70).trim());
		} catch( NumberFormatException e ) {
			partialCharge = 0;
		}
		atomType = trimmed.substring( 71, 73 ).trim();
	}

	public String toString() {
		return String.format( "%-6s%5d %-4s%1s%-3s %1s%4d%1s   %8.3f%8.3f%8.3f%6.2f%6.2f%4s%6.3f %2s \n",
				type, serialNum, name, altLoc, resName, chainID, resNum, insCode, x, y, z,
				occupancy, tempFactor, footnote, partialCharge, atomType );
	}
}
