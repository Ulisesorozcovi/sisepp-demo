package dev.demo.sisepp.planificacion.infrastructure.output.persistence;

import dev.demo.sisepp.planificacion.domain.model.InstrumentStatus;
import dev.demo.sisepp.planificacion.domain.model.InstrumentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "planning_instruments",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_instrument",
           columnNames = {"type", "start_year", "end_year"}
       ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanningInstrumentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private InstrumentType type;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "end_year", nullable = false)
    private Integer endYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PlanningInstrumentJpaEntity parent;

    @Column(name = "approved_date")
    private LocalDate approvedDate;

    @Column(name = "approval_ref", length = 200)
    private String approvalRef;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InstrumentStatus status = InstrumentStatus.ACTIVO;

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
