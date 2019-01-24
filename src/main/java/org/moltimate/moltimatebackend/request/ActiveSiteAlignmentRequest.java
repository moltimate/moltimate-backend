package org.moltimate.moltimatebackend.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ActiveSiteAlignmentRequest represents the PDB ids of the proteins whose active sites will be compared
 * against a set of motifs in the provided ecNumber.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSiteAlignmentRequest {

    private List<String> pdbIds;
    private List<String> options;
    private List<String> filters;
    private List<MultipartFile> files; // TODO: Rename to customMotifs
    private String ecNumber; // TODO: Make this into a filter
}
