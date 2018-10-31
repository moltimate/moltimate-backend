package org.moltimate.moltimatebackend.pdb;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.moltimate.moltimatebackend.alignment.AlignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PdbService provides a way to query proteins from the PDB and convert them to BioJava Structure objects.
 */
@Service
@Slf4j
public class PdbService {

    private static final PDBFileReader pdbFileReader = new PDBFileReader();

    @Autowired
    private AlignmentService alignmentService;

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
     * KEEP THIS, but it's not currently used. We may need this if we decide to use the RCSB PDB search API.
     */
    @Deprecated
    public String pdbAdvancedSearch() {
        final String PDB_SEARCH_URL = "https://www.rcsb.org/pdb/rest/search";
        String xml = "<orgPdbQuery><queryType>org.pdb.pdb.simple.EntityIdQuery</queryType><entityIdList>4HHB:1 1ATP:1</entityIdList></orgPdbQuery>";
        return doPostGetBody(PDB_SEARCH_URL, xml);
    }

    /**
     * KEEP THIS, but it's not currently used. We may need this if we decide to use the RCSB PDB search API.
     */
    @Deprecated
    private static String doPostGetBody(String urlString, String body) {
        try {
            byte[] xmlContent = body.getBytes(StandardCharsets.UTF_8);

            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setFixedLengthStreamingMode(xmlContent.length);
            httpURLConnection.connect();

            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(xmlContent);
            }

            return IOUtils.toString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
