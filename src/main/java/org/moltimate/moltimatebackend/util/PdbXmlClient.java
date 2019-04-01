package org.moltimate.moltimatebackend.util;

import lombok.extern.slf4j.Slf4j;

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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class PdbXmlClient {

    private static final String PDB_POST_SEARCH_URL = "http://www.rcsb.org/pdb/rest/search";
    private static final String PDB_GET_CURRENT_PDB_IDS_URL = "https://www.rcsb.org/pdb/rest/getCurrent";

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
            return postXMLQuery(xml).stream().parallel()
                    .map(s -> s.split(":")[0])
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static List<String> getPdbIds() {
        try {
            return getAllCurrentPdbIds().stream().parallel()
                    .map(s -> {
                        Matcher m = Pattern.compile(".*structureId=\"(.{4})\".*").matcher(s);
                        if (m.matches()) {
                            return m.group(1);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not get current pdb ids, falling back on random generation");
            return new ArrayList<>();
        }
    }


    /**
     * post am XML query (PDB XML query format) to the REST RCSB web service
     *
     * @param xml XML search query
     * @return a list of PDB ids.
     */
    private static List<String> postXMLQuery(String xml) throws IOException {
        URL url = new URL(PDB_POST_SEARCH_URL);
        String encodedXML = URLEncoder.encode(xml, "UTF-8");

        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

        wr.write(encodedXML);
        wr.flush();

        return extractResponse(conn);
    }

    private static List<String> getAllCurrentPdbIds() throws IOException {
        URL url = new URL(PDB_GET_CURRENT_PDB_IDS_URL);

        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);

        return extractResponse(conn);
    }

    private static List<String> extractResponse(URLConnection conn) throws IOException {
        InputStream in = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(in));

        List<String> pdbIds = new ArrayList<>();
        String line;
        while ((line = rd.readLine()) != null) {
            pdbIds.add(line);
        }
        rd.close();
        return pdbIds;
    }
}
