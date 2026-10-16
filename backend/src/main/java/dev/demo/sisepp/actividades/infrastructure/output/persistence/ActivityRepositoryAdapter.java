package dev.demo.sisepp.actividades.infrastructure.output.persistence;

import dev.demo.sisepp.actividades.domain.model.Activity;
import dev.demo.sisepp.actividades.domain.port.output.ActivityRepositoryPort;
import dev.demo.sisepp.catalogo.infrastructure.output.persistence.*;
import dev.demo.sisepp.proyectos.infrastructure.output.persistence.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityRepositoryAdapter implements ActivityRepositoryPort {

    private final ActivityJpaRepository jpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final SdgIndicatorJpaRepository sdgIndicatorJpaRepository;
    private final ThematicCategoryJpaRepository thematicCategoryJpaRepository;
    private final ManagementIndicatorJpaRepository managementIndicatorJpaRepository;
    private final MeasurementUnitJpaRepository measurementUnitJpaRepository;

    @Override
    public Activity save(Activity activity) {
        ActivityJpaEntity entity;
        if (activity.id() != null) {
            entity = jpaRepository.findById(activity.id()).orElseGet(() -> ActivityMapper.toJpa(activity));
            entity.setName(activity.name());
            entity.setCode(activity.code());
            entity.setMeasurementUnit(activity.measurementUnit());
            entity.setGoal(activity.goal());
            entity.setWeight(activity.weight());
            entity.setIndicatorDescription(activity.indicatorDescription());
            entity.setIndicatorType(activity.indicatorType());
            entity.setStatus(activity.status());
        } else {
            entity = ActivityMapper.toJpa(activity);
        }
        if (activity.projectId() != null) {
            projectJpaRepository.findById(activity.projectId()).ifPresent(entity::setProject);
        }
        if (activity.thematicCategoryId() != null) {
            thematicCategoryJpaRepository.findById(activity.thematicCategoryId()).ifPresent(entity::setThematicCategory);
        } else {
            entity.setThematicCategory(null);
        }
        if (activity.managementIndicatorId() != null) {
            managementIndicatorJpaRepository.findById(activity.managementIndicatorId()).ifPresent(entity::setManagementIndicator);
        } else {
            entity.setManagementIndicator(null);
        }
        if (activity.measurementUnitId() != null) {
            measurementUnitJpaRepository.findById(activity.measurementUnitId()).ifPresent(entity::setMeasurementUnitRef);
        } else {
            entity.setMeasurementUnitRef(null);
        }
        if (activity.sdgIndicatorIds() != null) {
            Set<SdgIndicatorJpaEntity> indicators = activity.sdgIndicatorIds().stream()
                .map(sdgIndicatorJpaRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            entity.setSdgIndicators(indicators);
        }
        return ActivityMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Activity> findById(UUID id) {
        return jpaRepository.findById(id).map(ActivityMapper::toDomain);
    }

    @Override
    public List<Activity> findByProjectId(UUID projectId) {
        return jpaRepository.findByProjectId(projectId).stream()
            .map(ActivityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Activity> findByProjectIdIn(List<UUID> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) return List.of();
        return jpaRepository.findByProject_IdIn(projectIds).stream()
            .map(ActivityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Activity> findAll() {
        return jpaRepository.findAll().stream()
            .map(ActivityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumWeightsByProjectId(UUID projectId, UUID excludeId) {
        return jpaRepository.sumWeightsByProjectId(projectId, excludeId);
    }
}
