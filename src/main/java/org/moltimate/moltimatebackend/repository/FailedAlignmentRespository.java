package org.moltimate.moltimatebackend.repository;

import org.moltimate.moltimatebackend.dto.alignment.FailedAlignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedAlignmentRespository extends JpaRepository<FailedAlignment, Long> {
}
