package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.shared.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MeasurementUnitJpaRepository extends JpaRepository<MeasurementUnitJpaEntity, UUID> {
    List<MeasurementUnitJpaEntity> findByStatus(RecordStatus status);
}
