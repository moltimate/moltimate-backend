package org.moltimate.moltimatebackend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PdbxmlClient {

    private static final String PDB_LOCATION = "http://www.rcsb.org/pdb/rest/search";

    public static List<String> postEcNumberForPdbIds(String ecNumber) {
        String xml = "<orgPdbQuery>" +
                "<queryType>org.pdb.query.simple.EntriesOfEntitiesQuery</queryType>" +
                "<description>Entries of :EnzymeClassificationQuery Search for " + ecNumber + "</description>" +
                "<parent><![CDATA[<orgPdbQuery>" +
                "<queryType>org.pdb.query.simple.EnzymeClassificationQuery</queryType>" +
                "<description>Enzyme Classification Search : EC=" + ecNumber + "</description>" +
                "<Enzyme_Classification>" + ecNumber + "</Enzyme_Classification>" +
                "</orgPdbQuery>]]></parent>" +
                "</orgPdbQuery>";
        try {
            return postXMLQuery(xml).stream()
                    .map(s -> s.split(":")[0])
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * post am XML query (PDB XML query format)  to the RESTful RCSB web service
     *
     * @param xml
     * @return a list of PDB ids.
     */
    private static List<String> postXMLQuery(String xml) throws IOException {
        URL url = new URL(PDB_LOCATION);
        String encodedXML = URLEncoder.encode(xml, "UTF-8");

        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

        wr.write(encodedXML);
        wr.flush();

        InputStream in = conn.getInputStream();

        List<String> pdbIds = new ArrayList<>();
        BufferedReader rd = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = rd.readLine()) != null) {
            pdbIds.add(line);
        }
        rd.close();

        return pdbIds;
    }
}
