package dev.demo.sisepp.estructura.application.service;

import dev.demo.sisepp.actividades.domain.model.Activity;
import dev.demo.sisepp.actividades.domain.model.ActivityAnnualBudget;
import dev.demo.sisepp.actividades.domain.model.ActivityAnnualGoal;
import dev.demo.sisepp.actividades.domain.port.output.ActivityAnnualBudgetRepositoryPort;
import dev.demo.sisepp.actividades.domain.port.output.ActivityAnnualGoalRepositoryPort;
import dev.demo.sisepp.actividades.domain.port.output.ActivityRepositoryPort;
import dev.demo.sisepp.catalogo.domain.model.*;
import dev.demo.sisepp.catalogo.domain.port.output.*;
import dev.demo.sisepp.estructura.application.dto.PacMatrixResponse;
import dev.demo.sisepp.estructura.application.dto.PacMatrixResponse.*;
import dev.demo.sisepp.estructura.application.port.input.PacMatrixUseCase;
import dev.demo.sisepp.planificacion.domain.model.PlanningInstrument;
import dev.demo.sisepp.planificacion.domain.port.output.PlanningInstrumentRepositoryPort;
import dev.demo.sisepp.programas.domain.model.Program;
import dev.demo.sisepp.programas.domain.port.output.ProgramRepositoryPort;
import dev.demo.sisepp.proyectos.domain.model.Project;
import dev.demo.sisepp.proyectos.domain.port.output.ProjectRepositoryPort;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PacMatrixService implements PacMatrixUseCase {

    private final PlanningInstrumentRepositoryPort planningRepo;
    private final StrategicLineRepositoryPort strategicLineRepo;
    private final ProgramRepositoryPort programRepo;
    private final ProjectRepositoryPort projectRepo;
    private final ActivityRepositoryPort activityRepo;
    private final ActivityAnnualGoalRepositoryPort goalRepo;
    private final ActivityAnnualBudgetRepositoryPort budgetRepo;
    private final PublicInvestmentProgramRepositoryPort pipRepo;
    private final ThematicCategoryRepositoryPort thematicCategoryRepo;
    private final MeasurementUnitRepositoryPort measurementUnitRepo;

    @Override
    @Transactional(readOnly = true)
    public PacMatrixResponse generate(UUID pacId, Integer vigencia) {
        PlanningInstrument pac = planningRepo.findById(pacId)
            .orElseThrow(() -> new ResourceNotFoundException("PAC no encontrado"));

        List<Integer> years = IntStream.rangeClosed(pac.startYear(), pac.endYear()).boxed().toList();

        List<StrategicLine> lines = strategicLineRepo.findAll();
        List<Program> allPrograms = programRepo.findAll();
        List<Project> allProjects = projectRepo.findByVigenciaBetween(pac.startYear(), pac.endYear());
        List<UUID> projectIds = allProjects.stream().map(Project::id).toList();

        List<Activity> allActivities = activityRepo.findByProjectIdIn(projectIds);
        List<UUID> activityIds = allActivities.stream().map(Activity::id).toList();

        List<ActivityAnnualGoal> allGoals = goalRepo.findByActivityIds(activityIds);
        List<ActivityAnnualBudget> allBudgets = budgetRepo.findByActivityIds(activityIds);

        Map<UUID, List<Program>> programsByLine = allPrograms.stream()
            .collect(Collectors.groupingBy(Program::strategicLineId));
        Map<UUID, List<Project>> projectsByProgram = allProjects.stream()
            .collect(Collectors.groupingBy(Project::programId));
        Map<UUID, List<Activity>> activitiesByProject = allActivities.stream()
            .collect(Collectors.groupingBy(Activity::projectId));
        Map<UUID, List<ActivityAnnualGoal>> goalsByActivity = allGoals.stream()
            .collect(Collectors.groupingBy(ActivityAnnualGoal::activityId));
        Map<UUID, List<ActivityAnnualBudget>> budgetsByActivity = allBudgets.stream()
            .collect(Collectors.groupingBy(ActivityAnnualBudget::activityId));

        Map<UUID, ThematicCategory> catMap = thematicCategoryRepo.findActive().stream()
            .collect(Collectors.toMap(ThematicCategory::id, c -> c));
        Map<UUID, MeasurementUnit> muMap = measurementUnitRepo.findAll().stream()
            .collect(Collectors.toMap(MeasurementUnit::id, m -> m));
        Map<UUID, PublicInvestmentProgram> pipMap = pipRepo.findActive().stream()
            .collect(Collectors.toMap(PublicInvestmentProgram::id, p -> p));

        Map<Integer, BigDecimal> grandTotal = new LinkedHashMap<>();
        years.forEach(y -> grandTotal.put(y, BigDecimal.ZERO));

        List<StrategicLineReport> lineReports = lines.stream().map(line -> {
            List<ProgramReport> programReports = programsByLine
                .getOrDefault(line.id(), List.of()).stream().map(program -> {
                    List<ProjectReport> projectReports = projectsByProgram
                        .getOrDefault(program.id(), List.of()).stream()
                        .filter(p -> p.vigencia() >= pac.startYear() && p.vigencia() <= pac.endYear())
                        .map(project -> {
                            List<ActivityReport> activityReports = activitiesByProject
                                .getOrDefault(project.id(), List.of()).stream().map(activity -> {
                                    Map<Integer, BigDecimal> goals = buildYearMap(years, goalsByActivity.getOrDefault(activity.id(), List.of()), true);
                                    Map<Integer, BigDecimal> budgets = buildYearMap(years, budgetsByActivity.getOrDefault(activity.id(), List.of()), false);

                                    ThematicCategory cat = activity.thematicCategoryId() != null
                                        ? catMap.get(activity.thematicCategoryId()) : null;
                                    String category = cat != null ? cat.name() : null;
                                    MeasurementUnit mu = activity.measurementUnitId() != null
                                        ? muMap.get(activity.measurementUnitId()) : null;
                                    String muName = mu != null ? mu.name() : activity.measurementUnit();

                                    return new ActivityReport(
                                        activity.id(), activity.name(), activity.weight(),
                                        activity.indicatorDescription(), activity.indicatorType(),
                                        category, muName, goals, budgets
                                    );
                                }).toList();

                            Map<Integer, BigDecimal> subtotalBudgets = new LinkedHashMap<>();
                            years.forEach(y -> subtotalBudgets.put(y, BigDecimal.ZERO));
                            activityReports.forEach(ar -> ar.budgets().forEach((y, v) -> {
                                if (!"total".equals(String.valueOf(y))) {
                                    subtotalBudgets.merge(y, v, BigDecimal::add);
                                    grandTotal.merge(y, v, BigDecimal::add);
                                }
                            }));
                            BigDecimal projectBudgetTotal = subtotalBudgets.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                            subtotalBudgets.put(0, projectBudgetTotal);

                            PublicInvestmentProgram pip = project.pipId() != null
                                ? pipMap.get(project.pipId()) : null;
                            String pipName = pip != null ? pip.code() + " " + pip.name() : null;

                            return new ProjectReport(
                                project.id(), project.name(), project.weight(),
                                pipName, project.objective(), project.objectiveWeight(),
                                activityReports, subtotalBudgets
                            );
                        }).toList();
                    return new ProgramReport(program.id(), program.code(), program.name(), program.weight(), projectReports);
                }).filter(pr -> !pr.projects().isEmpty()).toList();
            return new StrategicLineReport(line.id(), line.name(), null, programReports);
        }).filter(lr -> !lr.programs().isEmpty()).toList();

        BigDecimal totalAll = grandTotal.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        grandTotal.put(0, totalAll);

        PacInfo pacInfo = new PacInfo(pac.name(), pac.startYear(), pac.endYear(), pac.approvalRef());
        return new PacMatrixResponse(pacInfo, years, lineReports, grandTotal);
    }

    @SuppressWarnings("unchecked")
    private <T> Map<Integer, BigDecimal> buildYearMap(List<Integer> years, List<T> entries, boolean isGoal) {
        Map<Integer, BigDecimal> map = new LinkedHashMap<>();
        years.forEach(y -> map.put(y, BigDecimal.ZERO));
        BigDecimal total = BigDecimal.ZERO;
        for (T entry : entries) {
            int year;
            BigDecimal value;
            if (isGoal) {
                ActivityAnnualGoal g = (ActivityAnnualGoal) entry;
                year = g.year();
                value = g.goalValue() != null ? g.goalValue() : BigDecimal.ZERO;
            } else {
                ActivityAnnualBudget b = (ActivityAnnualBudget) entry;
                year = b.year();
                value = b.budgetAmount() != null ? b.budgetAmount() : BigDecimal.ZERO;
            }
            map.put(year, value);
            total = total.add(value);
        }
        map.put(0, total);
        return map;
    }
}
