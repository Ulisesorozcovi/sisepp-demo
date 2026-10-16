package dev.demo.sisepp.seguimiento.infrastructure.output.persistence;

import dev.demo.sisepp.seguimiento.domain.model.FollowUpStatus;
import dev.demo.sisepp.tareas.infrastructure.output.persistence.TaskJpaEntity;
import dev.demo.sisepp.usuarios.infrastructure.output.persistence.UserJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "follow_ups", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"task_id", "reported_year", "reported_month"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowUpJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskJpaEntity task;

    @Column(name = "reported_month", nullable = false)
    private Integer reportedMonth;

    @Column(name = "reported_year", nullable = false)
    private Integer reportedYear;

    @Column(name = "physical_progress", precision = 12, scale = 2)
    private BigDecimal physicalProgress;

    @Column(name = "evidence_url", columnDefinition = "TEXT")
    private String evidenceUrl;

    @Column(name = "attachment_url", columnDefinition = "TEXT")
    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private FollowUpStatus status = FollowUpStatus.EN_PROCESO;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private UserJpaEntity reportedBy;

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
