package dev.demo.sisepp.bulk.application.service;

import dev.demo.sisepp.actividades.domain.model.Activity;
import dev.demo.sisepp.actividades.domain.model.ActivityAnnualBudget;
import dev.demo.sisepp.actividades.domain.model.ActivityAnnualGoal;
import dev.demo.sisepp.actividades.domain.port.output.ActivityAnnualBudgetRepositoryPort;
import dev.demo.sisepp.actividades.domain.port.output.ActivityAnnualGoalRepositoryPort;
import dev.demo.sisepp.actividades.domain.port.output.ActivityRepositoryPort;
import dev.demo.sisepp.bulk.application.dto.*;
import dev.demo.sisepp.bulk.application.port.input.BulkLoadPacUseCase;
import dev.demo.sisepp.catalogo.domain.model.MeasurementUnit;
import dev.demo.sisepp.catalogo.domain.model.StrategicLine;
import dev.demo.sisepp.catalogo.domain.port.output.MeasurementUnitRepositoryPort;
import dev.demo.sisepp.catalogo.domain.port.output.StrategicLineRepositoryPort;
import dev.demo.sisepp.planificacion.domain.model.InstrumentType;
import dev.demo.sisepp.planificacion.domain.model.PlanningInstrument;
import dev.demo.sisepp.planificacion.domain.port.output.PlanningInstrumentRepositoryPort;
import dev.demo.sisepp.programas.domain.model.Program;
import dev.demo.sisepp.programas.domain.port.output.ProgramRepositoryPort;
import dev.demo.sisepp.proyectos.domain.model.Project;
import dev.demo.sisepp.proyectos.domain.port.output.ProjectRepositoryPort;
import dev.demo.sisepp.shared.RecordStatus;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BulkLoadPacService implements BulkLoadPacUseCase {

    private final PlanningInstrumentRepositoryPort planningRepo;
    private final StrategicLineRepositoryPort strategicLineRepo;
    private final ProgramRepositoryPort programRepo;
    private final ProjectRepositoryPort projectRepo;
    private final ActivityRepositoryPort activityRepo;
    private final ActivityAnnualGoalRepositoryPort goalRepo;
    private final ActivityAnnualBudgetRepositoryPort budgetRepo;
    private final MeasurementUnitRepositoryPort measurementUnitRepo;

    @Override
    @Transactional
    public BulkLoadSummary execute(BulkPacRequest request) {
        PlanningInstrument pac = planningRepo.findById(request.pacId())
            .orElseThrow(() -> new ResourceNotFoundException("PAC no encontrado"));
        if (pac.type() != InstrumentType.PAC) {
            throw new IllegalArgumentException("El instrumento seleccionado no es de tipo PAC");
        }

        validate(request);

        Map<String, MeasurementUnit> unitsByName = measurementUnitRepo.findActive().stream()
            .collect(Collectors.toMap(u -> u.name().toLowerCase(), u -> u, (a, b) -> a));

        Map<String, StrategicLine> linesByName = strategicLineRepo.findAll().stream()
            .collect(Collectors.toMap(l -> l.name().toLowerCase().trim(), l -> l, (a, b) -> a));

        Map<String, Program> programsByCode = programRepo.findAll().stream()
            .filter(p -> p.code() != null)
            .collect(Collectors.toMap(p -> p.code().trim(), p -> p, (a, b) -> a));

        Map<String, Project> projectsByCode = projectRepo.findByPacId(request.pacId()).stream()
            .filter(p -> p.code() != null)
            .collect(Collectors.toMap(p -> p.code().trim(), p -> p, (a, b) -> a));

        int linesCreated = 0, linesReused = 0;
        int progsCreated = 0, progsReused = 0;
        int projsCreated = 0, projsUpdated = 0;
        int actsCreated = 0, actsUpdated = 0;
        int goalsCreated = 0, budgetsCreated = 0;

        for (BulkLineRequest lineReq : request.strategicLines()) {
            String lineKey = lineReq.name().toLowerCase().trim();
            StrategicLine line = linesByName.get(lineKey);
            if (line == null) {
                line = strategicLineRepo.save(new StrategicLine(
                    null, lineReq.name().trim(), null, null, RecordStatus.ACTIVO, null, null
                ));
                linesByName.put(lineKey, line);
                linesCreated++;
            } else {
                linesReused++;
            }

            if (lineReq.programs() == null) continue;

            for (BulkProgramRequest progReq : lineReq.programs()) {
                String progKey = progReq.code().trim();
                Program program = programsByCode.get(progKey);
                if (program == null) {
                    program = programRepo.save(new Program(
                        null, progReq.name().trim(), progReq.code().trim(),
                        progReq.weight(), null, line.id(), request.pacId(),
                        RecordStatus.ACTIVO, null, null
                    ));
                    programsByCode.put(progKey, program);
                    progsCreated++;
                } else {
                    progsReused++;
                }

                if (progReq.projects() == null) continue;

                for (BulkProjectRequest projReq : progReq.projects()) {
                    String projKey = projReq.code().trim();
                    Project existing = projectsByCode.get(projKey);
                    Project project;
                    if (existing == null) {
                        project = projectRepo.save(new Project(
                            null, projReq.name().trim(), program.id(),
                            projReq.vigencia(), projReq.code().trim(), projReq.subdivision(),
                            projReq.weight(), projReq.objective(), projReq.objectiveWeight(),
                            null, request.pacId(),
                            pac.startYear(), pac.endYear(),
                            RecordStatus.ACTIVO, Set.of(), null, null
                        ));
                        projectsByCode.put(projKey, project);
                        projsCreated++;
                    } else {
                        project = projectRepo.save(new Project(
                            existing.id(), projReq.name().trim(), program.id(),
                            projReq.vigencia(), projReq.code().trim(), projReq.subdivision(),
                            projReq.weight(), projReq.objective(), projReq.objectiveWeight(),
                            existing.pipId(), existing.pacId(),
                            existing.startYear(), existing.endYear(),
                            existing.status(), existing.coordinatorIds(),
                            existing.createdAt(), existing.updatedAt()
                        ));
                        projectsByCode.put(projKey, project);
                        projsUpdated++;
                    }

                    if (projReq.activities() == null) continue;

                    List<Activity> existingActivities = activityRepo.findByProjectId(project.id());
                    Map<String, Activity> actsByCode = existingActivities.stream()
                        .filter(a -> a.code() != null)
                        .collect(Collectors.toMap(a -> a.code().trim(), a -> a, (a, b) -> a));

                    for (BulkActivityRequest actReq : projReq.activities()) {
                        UUID muId = null;
                        String muName = actReq.measurementUnit();
                        if (muName != null && !muName.isBlank()) {
                            MeasurementUnit mu = unitsByName.get(muName.toLowerCase().trim());
                            if (mu != null) {
                                muId = mu.id();
                            }
                        }

                        String actKey = actReq.code().trim();
                        Activity existingAct = actsByCode.get(actKey);
                        Activity activity;
                        if (existingAct == null) {
                            activity = activityRepo.save(new Activity(
                                null, actReq.name().trim(), project.id(),
                                actReq.code().trim(), muName, null,
                                actReq.weight(), actReq.indicatorDescription(),
                                actReq.indicatorType(), null, null, muId,
                                RecordStatus.ACTIVO,
                                actReq.sdgIndicatorIds() != null ? actReq.sdgIndicatorIds() : Set.of(),
                                null, null
                            ));
                            actsCreated++;
                        } else {
                            activity = activityRepo.save(new Activity(
                                existingAct.id(), actReq.name().trim(), project.id(),
                                actReq.code().trim(), muName, existingAct.goal(),
                                actReq.weight(), actReq.indicatorDescription(),
                                actReq.indicatorType(),
                                existingAct.thematicCategoryId(),
                                existingAct.managementIndicatorId(), muId,
                                existingAct.status(),
                                actReq.sdgIndicatorIds() != null ? actReq.sdgIndicatorIds() : existingAct.sdgIndicatorIds(),
                                existingAct.createdAt(), existingAct.updatedAt()
                            ));
                            actsUpdated++;
                        }

                        if (actReq.annualGoals() != null) {
                            for (AnnualValueRequest g : actReq.annualGoals()) {
                                Optional<ActivityAnnualGoal> existingGoal =
                                    goalRepo.findByActivityIdAndYear(activity.id(), g.year());
                                if (existingGoal.isEmpty()) {
                                    goalRepo.save(new ActivityAnnualGoal(
                                        null, activity.id(), g.year(), g.value(), null, null
                                    ));
                                    goalsCreated++;
                                } else {
                                    goalRepo.save(new ActivityAnnualGoal(
                                        existingGoal.get().id(), activity.id(), g.year(), g.value(),
                                        existingGoal.get().createdAt(), existingGoal.get().updatedAt()
                                    ));
                                }
                            }
                        }

                        if (actReq.annualBudgets() != null) {
                            for (AnnualValueRequest b : actReq.annualBudgets()) {
                                Optional<ActivityAnnualBudget> existingBudget =
                                    budgetRepo.findByActivityIdAndYear(activity.id(), b.year());
                                if (existingBudget.isEmpty()) {
                                    budgetRepo.save(new ActivityAnnualBudget(
                                        null, activity.id(), b.year(), b.value(), null, null
                                    ));
                                    budgetsCreated++;
                                } else {
                                    budgetRepo.save(new ActivityAnnualBudget(
                                        existingBudget.get().id(), activity.id(), b.year(), b.value(),
                                        existingBudget.get().createdAt(), existingBudget.get().updatedAt()
                                    ));
                                }
                            }
                        }
                    }
                }
            }
        }

        return new BulkLoadSummary(true, new BulkLoadSummary.Summary(
            linesCreated, linesReused, progsCreated, progsReused,
            projsCreated, projsUpdated, actsCreated, actsUpdated,
            goalsCreated, budgetsCreated
        ));
    }

    private void validate(BulkPacRequest request) {
        List<String> errors = new ArrayList<>();
        Set<String> programCodes = new HashSet<>();
        Set<String> projectCodes = new HashSet<>();
        Set<String> activityCodes = new HashSet<>();

        for (BulkLineRequest line : request.strategicLines()) {
            if (line.programs() == null) continue;
            for (BulkProgramRequest prog : line.programs()) {
                if (!programCodes.add(prog.code().trim())) {
                    errors.add("Código de programa duplicado: " + prog.code());
                }

                if (prog.projects() == null) continue;

                BigDecimal projWeightSum = BigDecimal.ZERO;
                for (BulkProjectRequest proj : prog.projects()) {
                    if (!projectCodes.add(proj.code().trim())) {
                        errors.add("Código de proyecto duplicado: " + proj.code());
                    }
                    if (proj.weight() != null) {
                        projWeightSum = projWeightSum.add(proj.weight());
                    }

                    if (proj.activities() == null) continue;

                    BigDecimal actWeightSum = BigDecimal.ZERO;
                    for (BulkActivityRequest act : proj.activities()) {
                        if (!activityCodes.add(act.code().trim())) {
                            errors.add("Código de actividad duplicado: " + act.code());
                        }
                        if (act.weight() != null) {
                            actWeightSum = actWeightSum.add(act.weight());
                        }
                    }
                    if (actWeightSum.compareTo(BigDecimal.ONE) > 0) {
                        errors.add("Ponderación de actividades del proyecto " + proj.code()
                            + " excede 1.00: " + actWeightSum);
                    }
                }
                if (projWeightSum.compareTo(BigDecimal.ONE) > 0) {
                    errors.add("Ponderación de proyectos del programa " + prog.code()
                        + " excede 1.00: " + projWeightSum);
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Errores de validación: " + String.join("; ", errors));
        }
    }
}
