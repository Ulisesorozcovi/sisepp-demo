package dev.demo.sisepp.tareas.infrastructure.output.persistence;

import dev.demo.sisepp.actividades.infrastructure.output.persistence.ActivityJpaEntity;
import dev.demo.sisepp.tareas.domain.model.TaskStatus;
import dev.demo.sisepp.usuarios.infrastructure.output.persistence.UserJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private ActivityJpaEntity activity;

    @Column(name = "physical_goal_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal physicalGoalValue;

    @Column(name = "physical_goal_unit", nullable = false, length = 50)
    private String physicalGoalUnit;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "responsible_name", nullable = false, length = 150)
    private String responsibleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_user_id")
    private UserJpaEntity responsibleUser;

    @Column(name = "task_type", length = 100)
    private String taskType;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TaskStatus status = TaskStatus.PROGRAMADA;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "risk_note", length = 500)
    private String riskNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
