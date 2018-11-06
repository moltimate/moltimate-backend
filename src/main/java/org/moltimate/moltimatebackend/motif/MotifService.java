package org.moltimate.moltimatebackend.motif;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.repository.ResidueQuerySetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MotifService provides a way to query for and create motifs which represent the active sites of proteins.
 */
@Service
@Slf4j
public class MotifService {

    @Autowired
    private MotifRepository motifRepository;

    @Autowired
    private ResidueQuerySetRepository residueQuerySetRepository;

    /**
     * Creates a new Motif from the CreateMotifRequest.
     *
     * @param motif
     * @return A newly generated Motif
     */
    public Motif createMotif(Motif motif) {
        motif.getSelectionQueries().values().forEach(collectionSet -> residueQuerySetRepository.save(collectionSet));
        return motifRepository.save(motif);
    }

    /**
     * @param ecNumber Enzyme commission number to filter the set of comparable motifs
     * @return List of BioJava Structure objects representing each motif
     */
    public List<Motif> queryMotifs(String ecNumber) {
        log.info("Querying for motifs in EC class: " + ecNumber);
        return motifRepository.findAll();
    }
}
