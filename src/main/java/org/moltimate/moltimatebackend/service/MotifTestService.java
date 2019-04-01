package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.dto.ActiveSiteAlignmentResponse;
import org.moltimate.moltimatebackend.dto.MotifTestRequest;
import org.moltimate.moltimatebackend.dto.PdbQueryResponse;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.model.Motif;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.PdbXmlClient;
import org.moltimate.moltimatebackend.util.ProteinUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MotifTestService {

    @Autowired
    private MotifService motifService;

    @Autowired
    private AlignmentService alignmentService;

    public ActiveSiteAlignmentResponse testMotifAlignment(MotifTestRequest motifTestRequest) {
        log.info("Received request to test motif: " + motifTestRequest);
        String motifPdbId = motifTestRequest.getPdbId();
        String motifEcNumber = motifTestRequest.getEcNumber();
        Structure motifStructure = motifTestRequest.motifStructure();
        List<Residue> motifResidues = parseResidueEntries(motifTestRequest.getActiveSiteResidues());
        Motif testMotif = motifService.generateMotif(motifPdbId, motifEcNumber, motifStructure, motifResidues);

        List<Structure> structureList = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();
        HashMap<String, List<Alignment>> results = new HashMap<>();
        PdbQueryResponse pdbQueryResponse;

        switch (motifTestRequest.getType()) {
            case SELF:
                structureList.add(motifStructure);
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());

                structureList.forEach(structure -> results.put(structure.getPDBCode(), new ArrayList<>()));
                break;
            case LIST:
                pdbQueryResponse = motifTestRequest.callPdbForResponse();
                structureList.addAll(pdbQueryResponse.getStructures());
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());

                structureList.forEach(structure -> results.put(structure.getPDBCode(), new ArrayList<>()));
                failedIds.addAll(pdbQueryResponse.getFailedPdbIds());
                break;
            case HOMOLOGUE:
                List<String> homologuePdbIds = PdbXmlClient.postEcNumberForPdbIds(motifEcNumber);
                pdbQueryResponse = ProteinUtils.queryPdbResponse(homologuePdbIds);

                structureList.addAll(pdbQueryResponse.getStructures());
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());

                structureList.forEach(structure -> results.put(structure.getPDBCode(), new ArrayList<>()));
                failedIds.addAll(pdbQueryResponse.getFailedPdbIds());
                break;
            case RANDOM:
                int max = motifTestRequest.getRandomCount();
                List<String> allPdbIds = PdbXmlClient.getPdbIds();
                Collections.shuffle(allPdbIds);
                while (max > 0) {
                    String randomPdbId = allPdbIds.get(max);
                    Optional<Structure> optionalStructure = ProteinUtils.queryPdbOptional(randomPdbId);
                    if (optionalStructure.isPresent()) {
                        Structure testStructure = optionalStructure.get();
                        structureList.add(testStructure);
                        results.put(testStructure.getPDBCode(), new ArrayList<>());
                        max--;
                    } else {
                        failedIds.add(randomPdbId);
                    }
                }
                break;
        }

        log.info(String.format("Aligning active sites of %s with %d structures (%d custom structures).",
                motifPdbId, structureList.size(), motifTestRequest.getCustomStructures().size()));
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
