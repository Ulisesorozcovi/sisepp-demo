package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import dev.demo.sisepp.tareas.domain.model.TaskReschedule;

public final class TaskRescheduleMapper {

    private TaskRescheduleMapper() {}

    public static TaskReschedule toDomain(TaskRescheduleJpaEntity entity) {
        return new TaskReschedule(
            entity.getId(),
            entity.getTask() != null ? entity.getTask().getId() : null,
            entity.getPreviousDueDate(),
            entity.getNewDueDate(),
            entity.getJustification(),
            entity.getChangedByUser() != null ? entity.getChangedByUser().getId() : null,
            entity.getChangedAt()
        );
    }

    public static TaskRescheduleJpaEntity toJpa(TaskReschedule domain) {
        return TaskRescheduleJpaEntity.builder()
            .id(domain.id())
            .previousDueDate(domain.previousDueDate())
            .newDueDate(domain.newDueDate())
            .justification(domain.justification())
            .build();
    }
}
