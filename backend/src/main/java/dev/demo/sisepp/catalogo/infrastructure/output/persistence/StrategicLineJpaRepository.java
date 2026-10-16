package dev.demo.sisepp.catalogo.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StrategicLineJpaRepository extends JpaRepository<StrategicLineJpaEntity, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
