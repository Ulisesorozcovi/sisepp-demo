package dev.demo.sisepp.proyectos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectCoordinatorJpaRepository
        extends JpaRepository<ProjectCoordinatorJpaEntity, ProjectCoordinatorId> {

    List<ProjectCoordinatorJpaEntity> findByProjectId(UUID projectId);

    List<ProjectCoordinatorJpaEntity> findByUserId(UUID userId);

    Optional<ProjectCoordinatorJpaEntity> findByProjectIdAndUserId(UUID projectId, UUID userId);

    void deleteByProjectIdAndUserId(UUID projectId, UUID userId);
}
