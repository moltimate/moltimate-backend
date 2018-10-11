package com.moltimate.moltimatebackend.service;

import com.moltimate.moltimatebackend.model.PdbQuery;
import com.moltimate.moltimatebackend.model.PdbResponse;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PdbService {
    private static final Logger log = LoggerFactory.getLogger(PdbService.class);

    private static final String PDB_SEARCH_URL = "https://www.rcsb.org/pdb/rest/search";
    private static final PDBFileReader pdbFileReader = new PDBFileReader();

    public PdbResponse query(PdbQuery pdbQuery) {
        log.info("Received PDB query: " + pdbQuery.getPdbIds());
        return new PdbResponse(pdbQuery);
    }

//    public String pdbAdvancedSearch() {
//        String xml = "<orgPdbQuery><queryType>org.pdb.query.simple.EntityIdQuery</queryType><entityIdList>4HHB:1 1ATP:1</entityIdList></orgPdbQuery>";
//        return HttpHelper.doPostGetBody(PDB_SEARCH_URL, xml);
//    }
}
