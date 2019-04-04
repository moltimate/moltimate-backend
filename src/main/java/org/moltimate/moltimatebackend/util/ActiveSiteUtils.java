package org.moltimate.moltimatebackend.util;

import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.parser.ActiveSiteParser;
import org.moltimate.moltimatebackend.parser.CsaActiveSiteParser;
import org.moltimate.moltimatebackend.parser.PromolActiveSiteParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActiveSiteUtils {

    // Duplicate active sites are handled by favoring the active site from the parser defined earlier in this list.
    private static final List<ActiveSiteParser> ORDERED_PARSERS = Arrays.asList(
            new CsaActiveSiteParser(),
            new PromolActiveSiteParser()
    );

    /**
     * Returns a list of protein active sites from known sources of truth.
     */
    public static List<ActiveSite> getActiveSites() {
        return dedupeActiveSites(ORDERED_PARSERS.stream()
                                         .map(ActiveSiteParser::parseMotifs)
                                         .collect(Collectors.toList()));
    }

    /**
     * Takes an ordered list of active site lists, and dedupes them by PDB ID. The lists are processed in the order
     * they are passed, so the first list gets priority if there are duplicate PDB IDs.
     *
     * @param activeSiteLists Ordered list of active site lists
     * @return List of distinct active sites from all of the provided active site lists
     */
    private static List<ActiveSite> dedupeActiveSites(List<List<ActiveSite>> activeSiteLists) {
        Map<String, Boolean> pdbIdSeen = new HashMap<>();
        List<ActiveSite> distinctActiveSites = new ArrayList<>();

        activeSiteLists.forEach(activeSites -> {
            activeSites.forEach(activeSite -> {
                if (activeSite.getResidues()
                        .size() < 3) {
                    return; // ignore active sites with fewer than 3 residues
                }

                if (!pdbIdSeen.containsKey(activeSite.getPdbId()) && !"".equals(activeSite.getPdbId())) {
                    distinctActiveSites.add(activeSite);
                    pdbIdSeen.put(activeSite.getPdbId(), true);
                }
            });
        });

        return distinctActiveSites;
    }
}
