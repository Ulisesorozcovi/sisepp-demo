package dev.demo.sisepp.tareas.application.dto;

import dev.demo.sisepp.tareas.domain.model.Task;
import dev.demo.sisepp.tareas.domain.model.TaskStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
    UUID id,
    String name,
    UUID activityId,
    BigDecimal physicalGoalValue,
    String physicalGoalUnit,
    LocalDate dueDate,
    LocalDate startDate,
    String responsibleName,
    UUID responsibleUserId,
    String taskType,
    BigDecimal weight,
    String observation,
    TaskStatus status,
    LocalDateTime completedAt,
    String riskNote,
    /** Derivado: sigue abierta y su fecha ya pasó. */
    boolean overdue,
    /** Derivado: alcanzó la meta después de la fecha límite. */
    boolean completedLate,
    LocalDateTime createdAt
) {
    public static TaskResponse fromDomain(Task task) {
        return new TaskResponse(
            task.id(),
            task.name(),
            task.activityId(),
            task.physicalGoalValue(),
            task.physicalGoalUnit(),
            task.dueDate(),
            task.startDate(),
            task.responsibleName(),
            task.responsibleUserId(),
            task.taskType(),
            task.weight(),
            task.observation(),
            task.status(),
            task.completedAt(),
            task.riskNote(),
            task.isOverdue(),
            task.isCompletedLate(),
            task.createdAt()
        );
    }
}
