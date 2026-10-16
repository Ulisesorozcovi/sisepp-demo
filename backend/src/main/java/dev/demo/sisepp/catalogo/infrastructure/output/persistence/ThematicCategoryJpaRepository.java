package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import dev.demo.sisepp.shared.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ThematicCategoryJpaRepository extends JpaRepository<ThematicCategoryJpaEntity, UUID> {
    List<ThematicCategoryJpaEntity> findByStatus(RecordStatus status);
}
