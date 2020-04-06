package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.rcsb.RCSBDescription;
import org.biojava.nbio.structure.rcsb.RCSBDescriptionFactory;
import org.biojava.nbio.structure.rcsb.RCSBLigand;
import org.biojava.nbio.structure.rcsb.RCSBLigandsFactory;
import org.biojava.nbio.structure.rcsb.RCSBPolymer;
import org.moltimate.moltimatebackend.constant.EcNumber;
import org.moltimate.moltimatebackend.exception.InvalidFileException;
import org.moltimate.moltimatebackend.exception.InvalidPdbIdException;
import org.moltimate.moltimatebackend.repository.MotifRepository;
import org.moltimate.moltimatebackend.util.DockingUtils;
import org.moltimate.moltimatebackend.util.DockingUtils.InMemoryMultipartFile;
import org.moltimate.moltimatebackend.validation.EcNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<RCSBLigand> getByEcNumber(String ecNumber) {
        if (ecNumber == null || EcNumber.UNKNOWN.equals(ecNumber)) {
            return Collections.emptyList();
        }
        EcNumberValidator.validate(ecNumber);
        List<String> pdbIds = motifRepository
            .findByEcNumberEqualsOrEcNumberStartingWith(
                EcNumber.UNKNOWN, ecNumber, PageRequest.of(0, 320))
            .map(motif -> motif.getPdbId()).getContent();
        
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

}