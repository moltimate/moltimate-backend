package org.moltimate.moltimatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MakeMotifRequest {

    String pdbId;
    String ecNumber;
    List<String> activeSiteResidues;
    MultipartFile structureFile;

    public List<Residue> parseResidueEntries() {
        List<Residue> residueList = new ArrayList<>();
        for (String residueEntry : this.activeSiteResidues) {
            for (String residueAttr : residueEntry.split(",")) {
                String[] res = residueAttr.split(" ");
                Residue residue = Residue.builder()
                        .residueName(res[0])
                        .residueChainName(res[1])
                        .residueId(res[2])
                        .build();
                residueList.add(residue);
            }
        }
        residueList.sort(Comparator.comparingInt(r -> Integer.parseInt(r.getResidueId())));
        return residueList;
    }
}
