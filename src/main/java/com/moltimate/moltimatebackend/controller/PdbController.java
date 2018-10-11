package com.moltimate.moltimatebackend.controller;

import com.moltimate.moltimatebackend.model.PdbQuery;
import com.moltimate.moltimatebackend.model.PdbResponse;
import com.moltimate.moltimatebackend.service.PdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/query")
public class PdbController {

    @Autowired
    private PdbService pdbService;

    @PostMapping()
    @ResponseBody
    public PdbResponse query(@RequestBody PdbQuery pdbQuery) {
        return pdbService.query(pdbQuery);
    }

    @GetMapping(value = "/{pdbId}")
    @ResponseBody
    public PdbResponse query(@PathVariable(value = "pdbId") String pdbId) {
        return pdbService.query(new PdbQuery(pdbId));
    }
}
