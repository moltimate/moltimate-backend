package org.moltimate.moltimatebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.service.ActiveSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Active Site REST API
 */
@RestController
@RequestMapping(value = "/activesite")
@Slf4j
public class ActiveSiteController {

    @Autowired
    private ActiveSiteService activeSiteService;

    /**
     * Save a new ActiveSite to the database.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ActiveSite createActiveSite(@Valid @RequestBody ActiveSite activeSite) {
        return activeSiteService.createActiveSite(activeSite);
    }
}
