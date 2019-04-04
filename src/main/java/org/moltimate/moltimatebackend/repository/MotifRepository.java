package org.moltimate.moltimatebackend.repository;

import org.moltimate.moltimatebackend.model.Motif;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface MotifRepository extends JpaRepository<Motif, String> {

    Motif findByPdbId(String pdbId);

    Page<Motif> findAll(Pageable pageable);

    Page<Motif> findByEcNumberStartingWith(String ecNumber, Pageable pageable);

    Page<Motif> findByEcNumberEqualsOrEcNumberStartingWith(String unknown, String ecNumber, Pageable pageable);
}
