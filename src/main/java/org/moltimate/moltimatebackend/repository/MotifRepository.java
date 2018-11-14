package org.moltimate.moltimatebackend.repository;

import org.moltimate.moltimatebackend.motif.Motif;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotifRepository extends JpaRepository<Motif, String> {

    Motif findByPdbId(String pdbId);

}
