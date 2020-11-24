package org.moltimate.moltimatebackend.util;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PdbXmlClient {

    private static final String PDB_GET_CURRENT_PDB_IDS_URL = "https://data.rcsb.org/rest/v1/holdings/current/entry_ids";

    //get the pdb ids that are in the same ec class
    public static List<String> postEcNumberForPdbIds(String ecNumber) {
        log.info("Post EC number for PDB Ids");
        List<String> pdbIds = new ArrayList<>();

        String USER_AGENT = "Mozilla/5.0";

        String baseURL = "https://search.rcsb.org/rcsbsearch/v1/query?json=";
        String jsonConverted = "{\"query\":{\"type\":\"terminal\",\"service\":\"text\",\"parameters\":{\"attribute\":\"rcsb_polymer_entity." +
                "rcsb_ec_lineage.id\",\"negation\":false,\"operator\":\"exact_match\",\"value\":\"" + ecNumber +
                "\"},\"node_id\":0},\"return_type\":\"entry\",\"request_" +
                "options\":{\"pager\":{\"start\":0,\"rows\":1000},\"scoring_strategy\":\"combined\",\"sort\":[{\"sort" +
                "_by\":\"score\",\"direction\":\"desc\"}]},\"request_info\":{\"src\":\"ui\"}}";
        try {
            jsonConverted = URLEncoder.encode(jsonConverted, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            log.error(ex.getMessage());
        }
        StringBuffer response = new StringBuffer();
        try {
            URL obj = new URL(baseURL + jsonConverted);
            HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                log.error("GET request did not work");
                return new ArrayList<>();
            }
        } catch (IOException e) {
            log.error("Request Failed ");
        }
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try {
            log.info("Parsing response into json object");
            jsonObject = ((JSONObject) parser.parse(response.toString()));
            log.info("Json Object " + jsonObject.toString());
            JSONArray results = (JSONArray) jsonObject.get("result_set");
            log.info("Result Set: ");
            log.info(results.toString());
            Iterator<String> iterator = results.iterator();
            while (iterator.hasNext()) {
                Object pdbJson = iterator.next();
                JSONObject pdbId = (JSONObject) pdbJson;
                String identifier = (String) pdbId.get("identifier");
                pdbIds.add(identifier);
            }
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        return pdbIds;
    }

    //get all current pdb ids in the pdb
    //returns a list of all the pdb ids currently active in the pdb
    public static List<String> getPdbIds() {
        log.info("Get Pdb Ids 48 PdbXmlClient");
        List<String> pdbResponse;
        try {
            pdbResponse = getAllCurrentPdbIds();
        } catch (Exception e) {
            log.error("Could not get current pdb ids, ");
            pdbResponse = new ArrayList<>();
        }
        return pdbResponse;
    }

    //the actual call to the pdb to get all current pdb ids
    private static List<String> getAllCurrentPdbIds() throws IOException {
        List<String> pdbIds = new ArrayList<>();
        log.info("get all current Pdb Ids 90 pdb xml client");
        String USER_AGENT = "Mozilla/5.0";
        URL url = new URL(PDB_GET_CURRENT_PDB_IDS_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = httpURLConnection.getResponseCode();
        StringBuffer response = new StringBuffer();

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            } in.close();
        } else {
            log.error("GET request did not work");
        }
        log.info("Response " + response.toString());
        JSONParser parser = new JSONParser();
        JSONArray returnList = new JSONArray();
        try {
            returnList = ((JSONArray)parser.parse(response.toString()));
            log.info("Return List: " + returnList.toString());
            Iterator<String> iterator = returnList.iterator();
            while (iterator.hasNext()) {
                Object pdbJson = iterator.next();
                //log.info("pdb JSON: " + pdbJson.toString());
                pdbIds.add(pdbJson.toString());
            }
        } catch (ParseException | NullPointerException e) {
            log.error(e.getMessage());
        }
        log.info("Pdb IDs: {}", pdbIds);
        return pdbIds;
    }

}
