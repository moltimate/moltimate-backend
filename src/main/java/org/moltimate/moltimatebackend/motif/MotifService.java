package org.moltimate.moltimatebackend.motif;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MotifService provides a way to query for and create motifs which represent the active sites of proteins.
 */
@Service
@Slf4j
public class MotifService {

    /**
     * Creates a new Motif from the CreateMotifRequest.
     *
     * @param createMotifRequest
     * @return A newly generated Motif
     */
    public Motif createMotif(CreateMotifRequest createMotifRequest) {
        //TODO: something else
        return null;//new Motif();
    }

    /**
     * @param ecNumber Enzyme commission number to filter the set of comparable motifs
     * @return List of BioJava Structure objects representing each motif
     */
    public List<Motif> queryMotifs(String ecNumber) {
        log.info("Querying for motifs in EC class: " + ecNumber);
        List<Integer> ecNumbers = Arrays.stream(ecNumber.split(".")).map(Integer::parseInt).collect(Collectors.toList());

        // TODO: implement code to query for motifs based on ecNumber

        return Collections.emptyList();
    }
}
