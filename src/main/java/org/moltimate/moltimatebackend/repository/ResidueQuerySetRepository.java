package org.moltimate.moltimatebackend.repository;

import org.moltimate.moltimatebackend.model.ResidueQuerySet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface ResidueQuerySetRepository extends JpaRepository<ResidueQuerySet, Long> {
}
