package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.dto.ActiveSiteAlignmentResponse;
import org.moltimate.moltimatebackend.dto.MotifTestRequest;
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
        // Make motif to be tested
        String pdbId = motifTestRequest.getPdbId();
        String ecNumber = motifTestRequest.getEcNumber();
        Structure motifStructure = motifTestRequest.motifStructure();
        List<Residue> residues = parseResidueEntries(motifTestRequest.getActiveSiteResidues());
        Motif testMotif = motifService.generateMotif(pdbId, ecNumber, motifStructure, residues);

        // Accumulate Test Data
        int precisionFactor = motifTestRequest.getPrecisionFactor();

        List<Structure> structureList = new ArrayList<>();

        // Build structure list based on test
        HashMap<String, List<Alignment>> results = new HashMap<>();

        switch (motifTestRequest.getType()) {
            case SELF:
                results.put(pdbId, new ArrayList<>());
                structureList.add(motifStructure);
                break;
            case LIST:
//                pdbResponse.getFoundPdbIds().forEach(_pdbId -> results.put(_pdbId, new ArrayList<>()));
//                structureList.addAll(pdbResponse.getStructures());
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
        alignmentService.alignActiveSiteStructureList(results, testMotif, structureList, precisionFactor);

        return new ActiveSiteAlignmentResponse(results, new ArrayList<>());
    }

    private List<Residue> parseResidueEntries(List<String> residueEntries) {
//        Request : His C 57, Asp C 102, Gly E 193, Ser E 195, Gly E 196
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
