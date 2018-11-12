package org.moltimate.moltimatebackend.repository;

import org.moltimate.moltimatebackend.model.ActiveSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActiveSiteRepository extends JpaRepository<ActiveSite, String> {

    ActiveSite findByPdbId(String pdbId);

    ActiveSite findByMcsaId(String mcsaId);

    ActiveSite findByUniprotId(String uniprotId);

    ActiveSite findByCustomId(String customId);

    List<ActiveSite> findByEcNumberStartingWith(String ecNumber);
}
