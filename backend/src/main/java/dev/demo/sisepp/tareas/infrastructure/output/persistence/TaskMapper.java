package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import dev.demo.sisepp.tareas.domain.model.Task;

public final class TaskMapper {

    private TaskMapper() {}

    public static Task toDomain(TaskJpaEntity entity) {
        return new Task(
            entity.getId(),
            entity.getName(),
            entity.getActivity() != null ? entity.getActivity().getId() : null,
            entity.getPhysicalGoalValue(),
            entity.getPhysicalGoalUnit(),
            entity.getDueDate(),
            entity.getResponsibleName(),
            entity.getResponsibleUser() != null ? entity.getResponsibleUser().getId() : null,
            entity.getTaskType(),
            entity.getWeight(),
            entity.getObservation(),
            entity.getStartDate(),
            entity.getStatus(),
            entity.getCompletedAt(),
            entity.getRiskNote(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static TaskJpaEntity toJpa(Task domain) {
        return TaskJpaEntity.builder()
            .id(domain.id())
            .name(domain.name())
            .physicalGoalValue(domain.physicalGoalValue())
            .physicalGoalUnit(domain.physicalGoalUnit())
            .dueDate(domain.dueDate())
            .responsibleName(domain.responsibleName())
            .taskType(domain.taskType())
            .weight(domain.weight())
            .observation(domain.observation())
            .startDate(domain.startDate())
            .status(domain.status())
            .completedAt(domain.completedAt())
            .riskNote(domain.riskNote())
            .build();
    }
}
