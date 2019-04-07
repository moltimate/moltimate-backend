package org.moltimate.moltimatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MakeMotifRequest {

    String pdbId;
    String ecNumber;
    List<Residue> activeSiteResidues;
    MultipartFile structureFile;
}
