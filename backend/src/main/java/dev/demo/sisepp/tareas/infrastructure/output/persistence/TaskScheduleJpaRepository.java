package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskScheduleJpaRepository extends JpaRepository<TaskScheduleJpaEntity, UUID> {

    List<TaskScheduleJpaEntity> findByTaskId(UUID taskId);

    List<TaskScheduleJpaEntity> findByActivityId(UUID activityId);
}
