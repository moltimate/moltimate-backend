package com.moltimate.moltimatebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdbQuery {

    private List<String> pdbIds;

    public PdbQuery(String pdbId) {
        pdbIds = Collections.singletonList(pdbId);
    }
}
