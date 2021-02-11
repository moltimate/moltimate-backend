package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.rcsb.RCSBLigand;
import org.moltimate.moltimatebackend.constant.EcNumber;
import org.moltimate.moltimatebackend.exception.InvalidFileException;
import org.moltimate.moltimatebackend.exception.InvalidPdbIdException;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.util.DockingUtils;
import org.moltimate.moltimatebackend.util.DockingUtils.InMemoryMultipartFile;
import org.moltimate.moltimatebackend.validation.EcNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.*;

import javax.validation.constraints.Null;
import java.io.BufferedReader;

import javax.validation.constraints.Null;
import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.*;
import java.util.stream.Collectors;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * Ligand Service provides methods for interfacing with the PDB
 */
@Service
@Slf4j
public class LigandService {

    @Autowired
    private MotifRepository motifRepository;

    /**
     * @param ecNumber EC number to find associated Ligands for
     * @return List of Ligands associated with Proteins of this EC
     */
    public List<RCSBLigand> getByEcNumber(String ecNumber)  {
        if (ecNumber == null || EcNumber.UNKNOWN.equals(ecNumber)) {
            return Collections.emptyList();
        }
        EcNumberValidator.validate(ecNumber);

        log.info("Retrieving Ligands associated EC Class " + ecNumber);
        List<String> pdbIds = new ArrayList<>();
        try {
            pdbIds = getPdbIdsFromEcClass(ecNumber);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("Found PDB Ids {}", pdbIds);
        //remove unknown ec class pdb ids
        Map<String, RCSBLigand> uniqueLigands = new HashMap<>();

        log.info("Retrieving Ligands associated with PDB IDs {}", pdbIds);
        for(String pdb : pdbIds) {
            String url = "https://data.rcsb.org/graphql?query=%7B%0A%20%20entry(entry_id:%20%22"+ pdb +"%22)%20%7B%0A%20%20%20%20nonpolymer_entities%20%7B%0A%20%20%20%20%20%20rcsb_nonpolymer_entity_container_identifiers%20%7B%0A%20%20%20%20%20%20%20%20entry_id%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20nonpolymer_comp%20%7B%0A%20%20%20%20%20%20%20%20chem_comp%20%7B%0A%20%20%20%20%20%20%20%20%20%20id%0A%20%20%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20%20%20formula_weight%0A%20%20%20%20%20%20%20%20%20%20formula%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20rcsb_chem_comp_descriptor%20%7B%0A%20%20%20%20%20%20%20%20%20%20InChI%0A%20%20%20%20%20%20%20%20%20%20InChIKey%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20pdbx_chem_comp_descriptor%20%7B%0A%20%20%20%20%20%20%20%20%20%20descriptor%0A%20%20%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20%20%20program%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D";
            try{
                String USER_AGENT = "Mozilla/5.0";
                URL obj = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = httpURLConnection.getResponseCode();
                log.info("GET Response Code :: " + responseCode);
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
                JSONParser parser = new JSONParser();
                log.info("Ligand Response for PDB Id " + pdb);
                log.info(response.toString());
                JSONObject responseObj = (JSONObject)parser.parse(response.toString());
                JSONObject data = (JSONObject) responseObj.get("data");
                JSONObject entry = (JSONObject) data.get("entry");
                JSONArray ligands = (JSONArray) entry.get("nonpolymer_entities");
                Iterator<String> iterator = ligands.iterator();
                while (iterator.hasNext()) {
                    Object ligandParts = iterator.next();
                    JSONObject ligandJSON = (JSONObject)ligandParts;
                    JSONObject chem_comp = (JSONObject)((JSONObject)ligandJSON.get("nonpolymer_comp")).get("chem_comp");
                    JSONObject descriptor = (JSONObject)((JSONObject)ligandJSON.get("nonpolymer_comp")).get("rcsb_chem_comp_descriptor");
                    RCSBLigand ligand = new RCSBLigand();
                    //TODO: Possibly have to add the SMILE field- not sure what smiles is.
                    //also id is being copied over to the name
                    ligand.setFormula(chem_comp.get("formula").toString());
                    ligand.setId(chem_comp.get("id").toString());
                    ligand.setInChI(descriptor.get("InChI").toString());
                    ligand.setInChIKey(descriptor.get("InChIKey").toString());
                    ligand.setName(ligand.getId());
                    ligand.setType(chem_comp.get("type").toString());
                    ligand.setWeight((Double)chem_comp.get("formula_weight"));
                    uniqueLigands.put(ligand.getFormula(), ligand);
                }

            } catch (IOException e) {
                continue;
            } catch (ParseException e) {
                continue;
            } catch (NullPointerException e){
                continue;
            }
        }
        List<RCSBLigand> returnLigands = new ArrayList<>();
        for(RCSBLigand lig : uniqueLigands.values()){
            returnLigands.add(lig);
        }
        return returnLigands;

    }

    /**
     * Fetch the Ligand File needed for the supplied Docking Request
     * @param request
     */
    public static MultipartFile fetchLigand(String ligandID) {
        if (ligandID == null) {
            throw new InvalidFileException("Unable to fetch remote Ligand File: no ligandID provided");
        }

        log.info("Fetching Ligand {} for Docking Request", ligandID);

        try {
            URL fileLocation = new URL(String.format(DockingUtils.SDF_URL, ligandID));
            BufferedReader read = new BufferedReader( new InputStreamReader(fileLocation.openStream()));

            String line;
            String entireFileString = "";
            while((line = read.readLine()) != null){
                entireFileString = entireFileString + line + "\n";

            }

            byte[] file = entireFileString.getBytes();
            return new InMemoryMultipartFile(ligandID+".sdf", file);
        }
        catch (IOException e) {
            throw new InvalidFileException("Unable to fetch remote Ligand File");
        }
    }

    /**
     * Fetch a structure's Enzyme Classification Number from the PDB,
     * allowing for retries in the case of error.
     * @param structure
     * @return
     * @throws PDBFetchException
     */
    @Retryable(maxAttempts = 2, backoff = @Backoff(5000))
    public String getEcNumber(Structure structure) throws InvalidPdbIdException, IOException {
        String USER_AGENT = "Mozilla/5.0";
        String url = "https://data.rcsb.org/rest/v1/core/polymer_entity/" + structure.getPDBCode() + "/1";
        URL obj = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = httpURLConnection.getResponseCode();
        log.info("GET Response Code :: " + responseCode);
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
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = ((JSONObject) parser.parse(response.toString()));
            JSONObject entity = (JSONObject)jsonObject.get("rcsb_polymer_entity");
            String ecClass = entity.get("pdbx_ec").toString();
            return ecClass;
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return EcNumber.UNKNOWN;
    }

    /**
     * call the Protein Data bank's Search api to get the PDB Ids from the input EC Class
     * @param ecNumber
     * @return List of pdbIds in the EC class
     */
    public List<String> getPdbIdsFromEcClass(String ecNumber) throws IOException {

        List<String> pdbIds = new ArrayList<>();
        if (ecNumber == EcNumber.UNKNOWN) {
            log.info("EC Number not known. Enter EC number and try again");
            return pdbIds;
        }
        String USER_AGENT = "Mozilla/5.0";

        String baseURL = "https://search.rcsb.org/rcsbsearch/v1/query?json=";
        String jsonConverted = "{\"query\":{\"type\":\"terminal\",\"service\":\"text\",\"parameters\":{\"attribute\":\"rcsb_polymer_entity." +
                "rcsb_ec_lineage.id\",\"negation\":false,\"operator\":\"exact_match\",\"value\":\""+ecNumber+
                "\"},\"node_id\":0},\"return_type\":\"entry\",\"request_" +
                "options\":{\"pager\":{\"start\":0,\"rows\":1000},\"scoring_strategy\":\"combined\",\"sort\":[{\"sort" +
                "_by\":\"score\",\"direction\":\"desc\"}]},\"request_info\":{\"src\":\"ui\"}}";
        try {
            jsonConverted =  URLEncoder.encode(jsonConverted, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            log.error(ex.getMessage());
        }

        String GET_URL= baseURL + jsonConverted;
        URL obj = new URL(GET_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = httpURLConnection.getResponseCode();
        log.info("GET Response Code :: " + responseCode);
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
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try {
            log.info("Parsing response into json object");
            jsonObject = ((JSONObject)parser.parse(response.toString()));
            log.info("Json Object " + jsonObject.toString());
            JSONArray results = (JSONArray) jsonObject.get("result_set");
            log.info("Result Set: ");
            log.info(results.toString());
            Iterator<String> iterator = results.iterator();
            while (iterator.hasNext()) {
                Object pdbJson = iterator.next();
                JSONObject pdbId = (JSONObject) pdbJson;
                String identifier = (String)pdbId.get("identifier");
                pdbIds.add(identifier);
            }
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        return pdbIds;

    }

}