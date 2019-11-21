package org.moltimate.moltimatebackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.moltimate.moltimatebackend.model.ligand.PDBQT;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DockingRequest {
	private PDBQT receptor;
	private PDBQT ligand;
	private double centerX;
	private double centerY;
	private double centerZ;
	private double sizeX;
	private double sizeY;
	private double sizeZ;
}
