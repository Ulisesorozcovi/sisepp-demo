package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import dev.demo.sisepp.tareas.domain.model.TaskSchedule;

public final class TaskScheduleMapper {

    private TaskScheduleMapper() {}

    public static TaskSchedule toDomain(TaskScheduleJpaEntity entity) {
        return new TaskSchedule(
            entity.getId(),
            entity.getTask() != null ? entity.getTask().getId() : null,
            entity.getActivity() != null ? entity.getActivity().getId() : null,
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getScheduledGoal(),
            entity.getResponsible(),
            entity.getObservation(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static TaskScheduleJpaEntity toJpa(TaskSchedule domain) {
        return TaskScheduleJpaEntity.builder()
            .id(domain.id())
            .startDate(domain.startDate())
            .endDate(domain.endDate())
            .scheduledGoal(domain.scheduledGoal())
            .responsible(domain.responsible())
            .observation(domain.observation())
            .build();
    }
}
