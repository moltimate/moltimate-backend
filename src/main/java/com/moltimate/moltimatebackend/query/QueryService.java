package com.moltimate.moltimatebackend.query;

import com.moltimate.moltimatebackend.alignment.AlignmentResponse;
import com.moltimate.moltimatebackend.alignment.AlignmentService;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService {
    private static final Logger log = LoggerFactory.getLogger(QueryService.class);
    private static final PDBFileReader pdbFileReader = new PDBFileReader();

    @Autowired
    private AlignmentService alignmentService;

    /**
     * Executes the received QueryRequest using this service and the AlignmentService.
     *
     * @param queryRequest QueryRequest that maps the received JSON to usable data
     * @return AlignmentResponse which contains all alignments and their relevant data
     */
    public AlignmentResponse query(QueryRequest queryRequest) {
        return alignmentService.performActiveSiteAlignments(
                queryPdb(queryRequest.getPdbIds()),
                queryMotifs(queryRequest.getEcNumber())
        );
    }

    /**
     * @param pdbIds Varargs array of PDB ID strings
     * @return List of BioJava Structure objects representing each PDB ID
     */
    public List<Structure> queryPdb(String... pdbIds) {
        return queryPdb(Arrays.asList(pdbIds));
    }

    /**
     * @param pdbIds List of PDB ID strings
     * @return List of BioJava Structure objects representing each PDB ID
     */
    public List<Structure> queryPdb(List<String> pdbIds) {
        return pdbIds.stream()
                     .map(this::queryPdb)
                     .collect(Collectors.toList());
    }

    /**
     * @param pdbId PDB ID string
     * @return BioJava Structure object representing the PDB ID
     */
    public Structure queryPdb(String pdbId) {
        log.info("Querying for structure with id: " + pdbId);
        try {
            return pdbFileReader.getStructureById(pdbId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot find structure with PDB id " + pdbId);
        }
    }

    /**
     * @param ecNumber Enzyme commission number to filter the set of comparable motifs
     * @return List of BioJava Structure objects representing each motif
     */
    public List<Structure> queryMotifs(String ecNumber) {
        List<Integer> ecNumbers = Arrays.stream(ecNumber.split(".")).map(Integer::parseInt).collect(Collectors.toList());

        // TODO: implement code to query for motifs based on ecNumber

        return Collections.emptyList();
    }

    /**
     * KEEP THIS, but it's not currently used. We may need this if we decide to use the RCSB PDB search API
     */
    @Deprecated
    public String pdbAdvancedSearch() {
        final String PDB_SEARCH_URL = "https://www.rcsb.org/pdb/rest/search";
        String xml = "<orgPdbQuery><queryType>org.pdb.query.simple.EntityIdQuery</queryType><entityIdList>4HHB:1 1ATP:1</entityIdList></orgPdbQuery>";
        return HttpHelper.doPostGetBody(PDB_SEARCH_URL, xml);
    }
}
