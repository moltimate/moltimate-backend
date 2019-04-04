package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.dto.ActiveSiteAlignmentResponse;
import org.moltimate.moltimatebackend.dto.MotifStructure;
import org.moltimate.moltimatebackend.dto.MotifTestRequest;
import org.moltimate.moltimatebackend.dto.PdbQueryResponse;
import org.moltimate.moltimatebackend.model.Alignment;
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

    private static final int maxRandom = 50;

    public ActiveSiteAlignmentResponse testMotifAlignment(MotifTestRequest motifTestRequest) {
        log.info("Received request to test motif: " + motifTestRequest);
        MotifStructure testMotifStructure = MotifStructure.builder()
                .motif(motifService.generateMotif(
                        motifTestRequest.getPdbId(),
                        motifTestRequest.getEcNumber(),
                        motifTestRequest.motifStructure(),
                        parseResidueEntries(motifTestRequest.getActiveSiteResidues())))
                .motifStructure(motifTestRequest.motifStructure())
                .build();

        List<Structure> structureList = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();
        HashMap<String, List<Alignment>> results = new HashMap<>();
        PdbQueryResponse pdbQueryResponse;

        switch (motifTestRequest.getType()) {
            case SELF:
                structureList.add(testMotifStructure.getMotifStructure());
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());

                for (Structure _structure : structureList) {
                    results.put(_structure.getPDBCode(), new ArrayList<>());
                }
                break;
            case LIST:
                pdbQueryResponse = motifTestRequest.callPdbForResponse();
                structureList.addAll(pdbQueryResponse.getStructures());
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());

                for (Structure _structure : structureList) {
                    results.put(_structure.getPDBCode(), new ArrayList<>());
                }
                failedIds.addAll(pdbQueryResponse.getFailedPdbIds());
                break;
            case HOMOLOGUE:
                List<String> homologuePdbIds = PdbXmlClient.postEcNumberForPdbIds(testMotifStructure.getMotif().getEcNumber());
                pdbQueryResponse = ProteinUtils.queryPdbResponse(homologuePdbIds);

                structureList.addAll(pdbQueryResponse.getStructures());
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());

                for (Structure _structure : structureList) {
                    results.put(_structure.getPDBCode(), new ArrayList<>());
                }
                failedIds.addAll(pdbQueryResponse.getFailedPdbIds());
                break;
            case RANDOM:
                List<String> allPdbIds = PdbXmlClient.getPdbIds();
                Collections.shuffle(allPdbIds);

                int max;
                if (motifTestRequest.getRandomCount() < maxRandom) {
                    max = motifTestRequest.getRandomCount();
                } else {
                    max = maxRandom;
                }
                while (max >= 0) {
                    String randomPdbId = allPdbIds.get(max - 1);
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
                testMotifStructure.getMotif().getPdbId(),
                structureList.size(),
                motifTestRequest.getCustomStructures().size()));
        alignmentService.alignActiveSiteStructureList(testMotifStructure, structureList, motifTestRequest.getPrecisionFactor(), results);
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
