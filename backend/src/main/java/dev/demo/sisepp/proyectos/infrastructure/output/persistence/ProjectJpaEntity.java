package dev.demo.sisepp.proyectos.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.infrastructure.output.persistence.PublicInvestmentProgramJpaEntity;
import dev.demo.sisepp.planificacion.infrastructure.output.persistence.PlanningInstrumentJpaEntity;
import dev.demo.sisepp.programas.infrastructure.output.persistence.ProgramJpaEntity;
import dev.demo.sisepp.shared.RecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private ProgramJpaEntity program;

    @Column(nullable = false)
    private Integer vigencia;

    @Column(length = 30)
    private String code;

    @Column(length = 100)
    private String subdivision;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(columnDefinition = "TEXT")
    private String objective;

    @Column(name = "objective_weight", precision = 5, scale = 2)
    private BigDecimal objectiveWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pip_id")
    private PublicInvestmentProgramJpaEntity pip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pac_id")
    private PlanningInstrumentJpaEntity pac;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RecordStatus status = RecordStatus.ACTIVO;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProjectCoordinatorJpaEntity> coordinatorEntries = new ArrayList<>();

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

    public boolean hasCoordinator(UUID userId) {
        return coordinatorEntries.stream().anyMatch(c -> c.getUserId().equals(userId));
    }
}
