package org.moltimate.moltimatebackend.repository;

import org.moltimate.moltimatebackend.model.Motif;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotifRepository extends JpaRepository<Motif, String> {

    Motif findByPdbId(String pdbId);

    List<Motif> findByEcNumberStartingWith(String ecNumber);
}
