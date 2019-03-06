package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FileService {

    @Autowired
    private ProteinService proteinService;

    public Resource getAsPdb(String pdbId) {
        return new ByteArrayResource(proteinService.queryPdb(pdbId)
                                             .toPDB()
                                             .getBytes());
    }

    public Resource getAsMmcif(String pdbId) {
        return new ByteArrayResource(proteinService.queryPdb(pdbId)
                                             .toMMCIF()
                                             .getBytes());
    }

    public Resource getAsMotif(String pdbId, List<Residue> activeSiteResidues) {
        return null;
    }
}
