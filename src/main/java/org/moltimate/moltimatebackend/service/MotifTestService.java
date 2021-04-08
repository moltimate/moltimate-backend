package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.moltimate.moltimatebackend.dto.MotifFile;
import org.moltimate.moltimatebackend.dto.request.MotifTestRequest;
import org.moltimate.moltimatebackend.dto.response.MotifAlignmentResponse;
import org.moltimate.moltimatebackend.dto.response.PdbQueryResponse;
import org.moltimate.moltimatebackend.model.Alignment;
import org.moltimate.moltimatebackend.util.MotifUtils;
import org.moltimate.moltimatebackend.util.PdbXmlClient;
import org.moltimate.moltimatebackend.util.ProteinUtils;
import org.moltimate.moltimatebackend.util.StructureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MotifTestService {
    
    private static final int MAX_RANDOM_MOTIFS = 50;

    @Autowired
    private AlignmentService alignmentService;

    @Autowired
    private LigandService ligandService;

    public MotifAlignmentResponse testMotifAlignment(MotifTestRequest motifTestRequest) {
        Structure motifStructure = motifTestRequest.motifStructure();
        MotifFile testMotifFile = MotifFile.builder()
                .motif(MotifUtils.generateMotif(motifTestRequest.getPdbId(),
                        motifTestRequest.getEcNumber(),
                        motifStructure,
                        motifTestRequest.parseResidueEntries()))
                .structure(motifStructure)
                .build();

        List<Structure> structureList = new ArrayList<>();
        PdbQueryResponse pdbQueryResponse;
        MotifAlignmentResponse motifAlignmentResponse = new MotifAlignmentResponse(testMotifFile.getMotif());

        switch (motifTestRequest.getType()) {
            case SELF:
                structureList.add(motifStructure);
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());
                break;
            case LIST:
                pdbQueryResponse = motifTestRequest.callPdbForResponse();
                structureList.addAll(pdbQueryResponse.getStructures());
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());

                motifAlignmentResponse.addFailedPdbIds(pdbQueryResponse.getFailedPdbIds());
                break;
            case HOMOLOG:
                List<String> homologuePdbIds = PdbXmlClient.postEcNumberForPdbIds(testMotifFile.getMotif().getEcNumber());
                pdbQueryResponse = ProteinUtils.queryPdbResponse(homologuePdbIds, new ArrayList<>());

                structureList.addAll(pdbQueryResponse.getStructures());
                structureList.addAll(motifTestRequest.extractCustomStructuresFromFiles());

                motifAlignmentResponse.addFailedPdbIds(pdbQueryResponse.getFailedPdbIds());
                break;
            case RANDOM:
                List<String> allPdbIds = PdbXmlClient.getPdbIds();
                Collections.shuffle(allPdbIds);

                int randomCount = motifTestRequest.getRandomCount();
                if (randomCount > MAX_RANDOM_MOTIFS) {
                    randomCount = MAX_RANDOM_MOTIFS;
                }
                while (randomCount > 0) {
                    String randomPdbId = allPdbIds.get(randomCount - 1);
                    Optional<Structure> optionalStructure = ProteinUtils.queryPdbOptional(randomPdbId);
                    if (optionalStructure.isPresent()) {
                        Structure testStructure = optionalStructure.get();
                        structureList.add(testStructure);
                        randomCount--;
                    } else {
                        motifAlignmentResponse.addFailedPdbId(randomPdbId);
                    }
                }
                break;
        }

        log.info("Aligning active sites of {} with {} structures ({} custom structures).",
                testMotifFile.getMotif().getPdbId(), structureList.size(), motifTestRequest.getCustomStructures().size());

        for (Structure structure : structureList) {
            Alignment alignment = alignmentService.alignActiveSites(structure, testMotifFile.getMotif(), motifStructure, motifTestRequest.getPrecisionFactor());
            if (alignment != null) {
                motifAlignmentResponse.addSuccessfulEntry(structure, alignment);
            } else {
                motifAlignmentResponse.addFailedEntry(structure.getPDBCode(), StructureUtils.ecNumber(structure));
            }
        }
        return motifAlignmentResponse;
    }
}
