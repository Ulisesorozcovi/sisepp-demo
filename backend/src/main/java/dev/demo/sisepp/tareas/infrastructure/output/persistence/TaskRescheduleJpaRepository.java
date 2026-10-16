package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRescheduleJpaRepository extends JpaRepository<TaskRescheduleJpaEntity, UUID> {
    List<TaskRescheduleJpaEntity> findByTaskIdOrderByChangedAtAsc(UUID taskId);
}
