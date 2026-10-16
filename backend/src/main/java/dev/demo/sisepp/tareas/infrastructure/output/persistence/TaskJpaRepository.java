package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {
    List<TaskJpaEntity> findByActivityId(UUID activityId);
    List<TaskJpaEntity> findByActivity_IdIn(List<UUID> activityIds);
    List<TaskJpaEntity> findByActivity_Project_CoordinatorEntries_UserId(UUID userId);
    List<TaskJpaEntity> findByResponsibleUser_Id(UUID userId);
    List<TaskJpaEntity> findByIdIn(List<UUID> ids);
}
