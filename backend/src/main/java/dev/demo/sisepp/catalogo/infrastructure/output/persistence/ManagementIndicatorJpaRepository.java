package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.shared.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ManagementIndicatorJpaRepository extends JpaRepository<ManagementIndicatorJpaEntity, UUID> {
    List<ManagementIndicatorJpaEntity> findByStatus(RecordStatus status);
}
