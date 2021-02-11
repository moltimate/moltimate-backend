package org.moltimate.moltimatebackend.repository;

import org.moltimate.moltimatebackend.dto.alignment.SuccessfulAlignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuccessfulAlignmentRepository extends JpaRepository<SuccessfulAlignment, Long> {
}
