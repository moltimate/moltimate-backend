package com.moltimate.moltimatebackend.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryRequest {

    private List<String> pdbIds;
    private String ecNumber;
    private List<String> options;
    private List<String> filters;
}
