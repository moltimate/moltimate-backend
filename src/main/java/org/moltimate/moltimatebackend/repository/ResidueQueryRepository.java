package org.moltimate.moltimatebackend.repository;

import org.moltimate.moltimatebackend.motif.ResidueQuerySet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidueQueryRepository extends JpaRepository<ResidueQuerySet, Long> {
}
