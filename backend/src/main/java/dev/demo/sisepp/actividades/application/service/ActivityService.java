package dev.demo.sisepp.actividades.application.service;

import dev.demo.sisepp.actividades.application.dto.ActivityResponse;
import dev.demo.sisepp.actividades.application.dto.AnnualBudgetEntry;
import dev.demo.sisepp.actividades.application.dto.AnnualGoalEntry;
import dev.demo.sisepp.actividades.application.dto.CreateActivityRequest;
import dev.demo.sisepp.actividades.application.dto.UpdateActivityRequest;
import dev.demo.sisepp.actividades.domain.model.Activity;
import dev.demo.sisepp.actividades.domain.model.ActivityAnnualBudget;
import dev.demo.sisepp.actividades.domain.model.ActivityAnnualGoal;
import dev.demo.sisepp.actividades.application.port.input.ActivityUseCase;
import dev.demo.sisepp.actividades.domain.port.output.ActivityAnnualBudgetRepositoryPort;
import dev.demo.sisepp.actividades.domain.port.output.ActivityAnnualGoalRepositoryPort;
import dev.demo.sisepp.actividades.domain.port.output.ActivityRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import dev.demo.sisepp.shared.exception.WeightExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService implements ActivityUseCase {

    private final ActivityRepositoryPort repository;
    private final ActivityAnnualGoalRepositoryPort goalRepository;
    private final ActivityAnnualBudgetRepositoryPort budgetRepository;

    @Override
    @Transactional
    public ActivityResponse create(CreateActivityRequest request) {
        validateWeight(request.projectId(), request.weight(), null);
        Activity activity = new Activity(
            null, request.name(), request.projectId(), request.code(),
            request.measurementUnit(), request.goal(), request.weight(),
            request.indicatorDescription(), request.indicatorType(),
            request.thematicCategoryId(), request.managementIndicatorId(), request.measurementUnitId(),
            RecordStatus.ACTIVO, new HashSet<>(request.sdgIndicatorIds()), null, null
        );
        return ActivityResponse.fromDomain(repository.save(activity));
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResponse getById(UUID id) {
        Activity activity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
        return ActivityResponse.fromDomain(
            activity,
            goalRepository.findByActivityId(id),
            budgetRepository.findByActivityId(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityResponse> list(UUID projectId) {
        List<Activity> activities = projectId != null
            ? repository.findByProjectId(projectId)
            : repository.findAll();
        return activities.stream().map(a -> ActivityResponse.fromDomain(
            a,
            goalRepository.findByActivityId(a.id()),
            budgetRepository.findByActivityId(a.id())
        )).toList();
    }

    @Override
    @Transactional
    public ActivityResponse update(UUID id, UpdateActivityRequest request) {
        Activity activity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
        validateWeight(activity.projectId(), request.weight(), id);
        Activity updated = new Activity(
            activity.id(), request.name(), activity.projectId(), request.code(),
            request.measurementUnit(), request.goal(), request.weight(),
            request.indicatorDescription(), request.indicatorType(),
            request.thematicCategoryId(), request.managementIndicatorId(), request.measurementUnitId(),
            activity.status(), new HashSet<>(request.sdgIndicatorIds()),
            activity.createdAt(), activity.updatedAt()
        );
        Activity saved = repository.save(updated);

        if (request.annualGoals() != null) {
            goalRepository.deleteByActivityId(id);
            for (AnnualGoalEntry entry : request.annualGoals()) {
                goalRepository.save(new ActivityAnnualGoal(
                    null, id, entry.year(), entry.goalValue(), null, null
                ));
            }
        }

        if (request.annualBudgets() != null) {
            budgetRepository.deleteByActivityId(id);
            for (AnnualBudgetEntry entry : request.annualBudgets()) {
                budgetRepository.save(new ActivityAnnualBudget(
                    null, id, entry.year(), entry.budgetAmount(), null, null
                ));
            }
        }

        return ActivityResponse.fromDomain(
            saved,
            goalRepository.findByActivityId(id),
            budgetRepository.findByActivityId(id)
        );
    }

    private static final BigDecimal MAX_WEIGHT = new BigDecimal("100");

    private void validateWeight(UUID projectId, BigDecimal newWeight, UUID excludeId) {
        if (newWeight == null || projectId == null) return;
        BigDecimal currentSum = repository.sumWeightsByProjectId(projectId, excludeId);
        BigDecimal total = currentSum.add(newWeight);
        if (total.compareTo(MAX_WEIGHT) > 0) {
            BigDecimal available = MAX_WEIGHT.subtract(currentSum).max(BigDecimal.ZERO);
            throw new WeightExceededException(
                "La ponderación total de las actividades sería " + total +
                "%, excediendo el máximo de 100%. Peso disponible: " + available + "%"
            );
        }
    }
}
