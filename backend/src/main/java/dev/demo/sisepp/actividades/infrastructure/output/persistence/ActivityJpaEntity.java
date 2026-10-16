package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.infrastructure.output.persistence.ManagementIndicatorJpaEntity;
import dev.demo.sisepp.catalogo.infrastructure.output.persistence.MeasurementUnitJpaEntity;
import dev.demo.sisepp.catalogo.infrastructure.output.persistence.SdgIndicatorJpaEntity;
import dev.demo.sisepp.catalogo.infrastructure.output.persistence.ThematicCategoryJpaEntity;
import dev.demo.sisepp.proyectos.infrastructure.output.persistence.ProjectJpaEntity;
import dev.demo.sisepp.shared.RecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectJpaEntity project;

    @Column(length = 30)
    private String code;

    @Column(name = "measurement_unit", length = 50)
    private String measurementUnit;

    @Column(precision = 12, scale = 2)
    private BigDecimal goal;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "indicator_description", columnDefinition = "TEXT")
    private String indicatorDescription;

    @Column(name = "indicator_type", length = 30)
    private String indicatorType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thematic_category_id")
    private ThematicCategoryJpaEntity thematicCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_indicator_id")
    private ManagementIndicatorJpaEntity managementIndicator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_unit_id")
    private MeasurementUnitJpaEntity measurementUnitRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RecordStatus status = RecordStatus.ACTIVO;

    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ActivityAnnualGoalJpaEntity> annualGoals = new ArrayList<>();

    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ActivityAnnualBudgetJpaEntity> annualBudgets = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "activity_sdg_indicators",
        joinColumns = @JoinColumn(name = "activity_id"),
        inverseJoinColumns = @JoinColumn(name = "sdg_indicator_id")
    )
    @Builder.Default
    private Set<SdgIndicatorJpaEntity> sdgIndicators = new HashSet<>();

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
