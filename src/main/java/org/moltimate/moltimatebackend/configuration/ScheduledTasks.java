package org.moltimate.moltimatebackend.configuration;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.service.ActiveSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A common location to store any scheduled tasks.
 */
@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private ActiveSiteService activeSiteService;

    /**
     * Update active site database entries from the Catalytic Site Atlas everyday at 1:00 am.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateActiveSiteTable() {
        log.info("Updating current active site information from the Catalytic Site Atlas");
        activeSiteService.updateActiveSiteTable();
    }
}
