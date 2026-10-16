package dev.demo.sisepp.programas.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.infrastructure.output.persistence.StrategicLineJpaEntity;
import dev.demo.sisepp.planificacion.infrastructure.output.persistence.PlanningInstrumentJpaEntity;
import dev.demo.sisepp.shared.RecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "programs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"strategic_line_id", "name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 20)
    private String code;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategic_line_id", nullable = false)
    private StrategicLineJpaEntity strategicLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pac_id")
    private PlanningInstrumentJpaEntity pac;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RecordStatus status = RecordStatus.ACTIVO;

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
