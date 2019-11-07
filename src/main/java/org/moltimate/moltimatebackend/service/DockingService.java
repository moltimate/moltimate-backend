package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.model.ligand.PDBQT;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DockingService {
	public PDBQT dockLigand(DockingRequest request) {
		return new PDBQT();
	}
}
