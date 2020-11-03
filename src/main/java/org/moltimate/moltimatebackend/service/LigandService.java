package org.moltimate.moltimatebackend.service;

import io.swagger.models.Response;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.rcsb.RCSBDescription;
import org.biojava.nbio.structure.rcsb.RCSBDescriptionFactory;
import org.biojava.nbio.structure.rcsb.RCSBLigand;
import org.biojava.nbio.structure.rcsb.RCSBLigandsFactory;
import org.biojava.nbio.structure.rcsb.RCSBPolymer;
import org.json.simple.JSONValue;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.*;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;


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

        //call method from the motifRepository to get the pdb ids of the motifs that
        //are in the ec class or unknown
        List<String> pdbIds = new ArrayList<>();
        try {
            pdbIds = getPdbIdsFromEcClass(ecNumber);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("Found PDB Ids {}", pdbIds);
        //remove unknown ec class pdb ids

        log.info("Retrieving Ligands associated with PDB IDs {}", pdbIds);

        List<RCSBLigand> matchingLigands = RCSBLigandsFactory.getFromPdbIds(pdbIds).stream()
            .flatMap(ligands -> ligands.getLigands().stream())
            .collect(Collectors.groupingBy(l -> l.getName()))
            .values()
            .stream()
            .flatMap(group -> group.stream().limit(1))
            .collect(Collectors.toList());

        return matchingLigands;
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
            InputStream fileStream = fileLocation.openStream();
            byte[] file = new byte[fileStream.available()];

            fileStream.read(file);

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
    public String getEcNumber(Structure structure) throws InvalidPdbIdException {
        RCSBDescription description = RCSBDescriptionFactory.get(structure.getPDBCode());
        if (description == null) {
            throw new InvalidPdbIdException(structure.getPDBCode());
        }
        for (RCSBPolymer polymer : description.getPolymers()) {
            if (polymer.getEnzClass() != null) {
                return polymer.getEnzClass();
            }
        }
        return EcNumber.UNKNOWN;
    }

    /**
     * call the Protein Data bank's Search api to get the PDB Ids from the input EC Class
     * @param ecNumber
     * @return List of pdbIds in the EC class
     */
    public List<String> getPdbIdsFromEcClass(String ecNumber) throws IOException {
        String USER_AGENT = "Mozilla/5.0";

        String baseURL = "https://search.rcsb.org/rcsbsearch/v1/query?json=";
        String jsonConverted = "{\"query\":{\"type\":\"terminal\",\"service\":\"text\",\"parameters\":{\"attribute\":\"rcsb_polymer_entity.rcsb_ec_lineage.id\",\"negation\":false,\"operator\":\"exact_match\",\"value\":\""+ecNumber+"\"},\"node_id\":0},\"return_type\":\"entry\",\"request_options\":{\"pager\":{\"start\":0,\"rows\":1000},\"scoring_strategy\":\"combined\",\"sort\":[{\"sort_by\":\"score\",\"direction\":\"desc\"}]},\"request_info\":{\"src\":\"ui\"}}";
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

            // print result
            log.info(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = ((JSONObject)parser.parse(response.toString()));
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        log.info(jsonObject.toJSONString());



        List<String> pdbIds = new ArrayList<String>();

        return pdbIds;

    }

}