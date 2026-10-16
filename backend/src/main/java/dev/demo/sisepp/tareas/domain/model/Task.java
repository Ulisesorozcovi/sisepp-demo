package dev.demo.sisepp.tareas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record Task(
    UUID id,
    String name,
    UUID activityId,
    BigDecimal physicalGoalValue,
    String physicalGoalUnit,
    LocalDate dueDate,
    String responsibleName,
    UUID responsibleUserId,
    String taskType,
    BigDecimal weight,
    String observation,
    LocalDate startDate,
    TaskStatus status,
    /** Momento en que el avance acumulado alcanzó la meta. NULL mientras siga abierta. */
    LocalDateTime completedAt,
    /** Causa del retraso según el coordinador. El sistema detecta el patrón; esto explica el porqué. */
    String riskNote,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /** Una tarea sigue abierta mientras no se haya completado ni cerrado a mano. */
    public boolean isOpen() {
        return status != TaskStatus.COMPLETADA
            && status != TaskStatus.CANCELADA
            && status != TaskStatus.NO_APLICA;
    }

    /** Venció si sigue abierta y su fecha límite ya pasó. Se evalúa contra el día de hoy. */
    public boolean isOverdue() {
        return isOpen() && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    /** Alcanzó la meta después de su fecha límite. */
    public boolean isCompletedLate() {
        return status == TaskStatus.COMPLETADA
            && completedAt != null
            && dueDate != null
            && completedAt.toLocalDate().isAfter(dueDate);
    }
}
