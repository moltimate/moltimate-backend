package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.dto.request.ExportLigand;
import org.moltimate.moltimatebackend.dto.request.ExportRequest;
import org.moltimate.moltimatebackend.model.ligand.PDBQT;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DockingService {
	public PDBQT dockLigand(DockingRequest request) {
		return new PDBQT();
	}

	public Resource exportLigands(ExportRequest request) {
		StringBuilder csvOutput = new StringBuilder();
		csvOutput.append("Name,Mode Number,Binding Energy,RMSD Lower,RMSD Upper\n");

		for(ExportLigand ligand : request.getLigands()) {
			csvOutput.append("\"");
			csvOutput.append(ligand.getName());
			csvOutput.append("\"");
			csvOutput.append(",");
			csvOutput.append(ligand.getModeNumber());
			csvOutput.append(",");
			csvOutput.append(ligand.getBindingEnergy());
			csvOutput.append(",");
			csvOutput.append(ligand.getRmsdLower());
			csvOutput.append(",");
			csvOutput.append(ligand.getRmsdUpper());
			csvOutput.append("\n");
		}

		return new ByteArrayResource( csvOutput.toString().getBytes() );
	}
}
