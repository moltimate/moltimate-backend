package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.dto.ActiveSiteAlignmentResponse;
import org.moltimate.moltimatebackend.dto.MotifTestRequest;
import org.moltimate.moltimatebackend.dto.PdbQueryResponse;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class MotifTestService {

    @Autowired
    private MotifService motifService;

    @Autowired
    private AlignmentService alignmentService;

    public ActiveSiteAlignmentResponse testMotifAlignment(MotifTestRequest motifTestRequest) {
        String motifPdbId = motifTestRequest.getPdbId();
        String motifEcNumber = motifTestRequest.getEcNumber();
        Structure motifStructure = motifTestRequest.motifStructure();
        List<Residue> motifResidues = parseResidueEntries(motifTestRequest.getActiveSiteResidues());
        Motif testMotif = motifService.generateMotif(motifPdbId, motifEcNumber, motifStructure, motifResidues);

        List<Structure> structureList = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();
        HashMap<String, List<Alignment>> results = new HashMap<>();

        switch (motifTestRequest.getType()) {
            case SELF:
                // TODO: confirm custom structure is used
                results.put(motifPdbId, new ArrayList<>());
                structureList.add(motifStructure);
                break;
            case LIST:
                // TODO: confirm custom structure is included in search
                PdbQueryResponse pdbQueryResponse = motifTestRequest.callPdbForResponse();
                structureList.addAll(pdbQueryResponse.getStructures());
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());

                structureList.forEach(structure -> results.put(structure.getPDBCode(), new ArrayList<>()));
                failedIds.addAll(pdbQueryResponse.getFailedPdbIds());
                break;
            case HOMOLOGUE:
                // TODO: Using the given EC number find its HOMOLOGUE structures (from the PDB) and test alignment
                break;
            case RANDOM:
                // TODO: Obtain a random PDB id and then test alignment
                break;
            case BULK_RANDOM:
                // TODO: Generate a list of random PDB ids and then test alignment
                break;
        }

        alignmentService.alignActiveSiteStructureList(results, testMotif, structureList, motifTestRequest.getPrecisionFactor());
        return new ActiveSiteAlignmentResponse(results, failedIds);
    }

    private List<Residue> parseResidueEntries(List<String> residueEntries) {
        List<Residue> activeSiteResidues = new ArrayList<>();
        for (String residueEntry : residueEntries) {
            String[] res = residueEntry.split(" ");
            Residue residue = Residue.builder()
                    .residueName(res[0])
                    .residueChainName(res[1])
                    .residueId(res[2])
                    .build();
            activeSiteResidues.add(residue);
        }
        activeSiteResidues.sort(Comparator.comparingInt(r -> Integer.parseInt(r.getResidueId())));
        return activeSiteResidues;
    }
}
