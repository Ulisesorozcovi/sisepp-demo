package dev.demo.sisepp.estructura.application.service;

import dev.demo.sisepp.actividades.domain.model.Activity;
import dev.demo.sisepp.actividades.domain.port.output.ActivityRepositoryPort;
import dev.demo.sisepp.catalogo.application.dto.SdgIndicatorResponse;
import dev.demo.sisepp.catalogo.domain.model.SdgIndicator;
import dev.demo.sisepp.catalogo.domain.port.output.SdgIndicatorRepositoryPort;
import dev.demo.sisepp.estructura.application.dto.StructureTreeResponse.*;
import dev.demo.sisepp.estructura.application.dto.WeightSummaryResponse;
import dev.demo.sisepp.estructura.application.dto.WeightSummaryResponse.WeightItemResponse;
import dev.demo.sisepp.estructura.application.port.input.StructureUseCase;
import dev.demo.sisepp.catalogo.domain.model.StrategicLine;
import dev.demo.sisepp.catalogo.domain.port.output.StrategicLineRepositoryPort;
import dev.demo.sisepp.programas.domain.model.Program;
import dev.demo.sisepp.programas.domain.port.output.ProgramRepositoryPort;
import dev.demo.sisepp.proyectos.domain.model.Project;
import dev.demo.sisepp.proyectos.domain.port.output.ProjectRepositoryPort;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StructureService implements StructureUseCase {

    private final StrategicLineRepositoryPort strategicLineRepository;
    private final ProgramRepositoryPort programRepository;
    private final ProjectRepositoryPort projectRepository;
    private final ActivityRepositoryPort activityRepository;
    private final SdgIndicatorRepositoryPort sdgIndicatorRepository;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StrategicLineNode> buildTree(Integer vigencia) {
        List<StrategicLine> lines = strategicLineRepository.findAll();
        List<Program> allPrograms = programRepository.findAll();
        List<Project> allProjects = vigencia != null
            ? projectRepository.findByVigencia(vigencia)
            : projectRepository.findAll();
        List<UUID> projectIds = allProjects.stream().map(Project::id).toList();

        List<Activity> allActivities = activityRepository.findByProjectIdIn(projectIds);

        List<UUID> coordinatorIds = allProjects.stream()
            .flatMap(p -> p.coordinatorIds().stream())
            .distinct().toList();
        Map<UUID, String> userNames = userRepository.findByIdIn(coordinatorIds).stream()
            .collect(Collectors.toMap(User::id, User::fullName));

        Map<Integer, SdgIndicator> sdgMap = sdgIndicatorRepository.findAll().stream()
            .collect(Collectors.toMap(SdgIndicator::id, i -> i));

        Map<UUID, List<Program>> programsByLine = allPrograms.stream()
            .collect(Collectors.groupingBy(Program::strategicLineId));
        Map<UUID, List<Project>> projectsByProgram = allProjects.stream()
            .collect(Collectors.groupingBy(Project::programId));
        Map<UUID, List<Activity>> activitiesByProject = allActivities.stream()
            .collect(Collectors.groupingBy(Activity::projectId));

        return lines.stream().map(line -> {
            List<ProgramNode> programs = programsByLine
                .getOrDefault(line.id(), List.of()).stream().map(program -> {
                    List<ProjectNode> projects = projectsByProgram
                        .getOrDefault(program.id(), List.of()).stream().map(project -> {
                            List<ActivityNode> activities = activitiesByProject
                                .getOrDefault(project.id(), List.of()).stream().map(activity -> {
                                    List<SdgIndicatorResponse> indicators = activity.sdgIndicatorIds().stream()
                                        .map(sdgMap::get)
                                        .filter(Objects::nonNull)
                                        .map(SdgIndicatorResponse::fromDomain)
                                        .toList();
                                    return new ActivityNode(activity.id(), activity.name(), activity.code(),
                                        activity.measurementUnit(), activity.goal(), activity.weight(),
                                        activity.status(), indicators);
                                }).toList();

                            List<String> coordinatorNameList = project.coordinatorIds().stream()
                                .map(id -> userNames.getOrDefault(id, "Desconocido"))
                                .toList();

                            return new ProjectNode(
                                project.id(), project.name(), project.code(), project.subdivision(),
                                project.weight(), project.vigencia(), project.status(),
                                coordinatorNameList, activities);
                        }).toList();
                    return new ProgramNode(program.id(), program.name(), program.code(), program.weight(),
                        program.description(), program.status(), projects);
                }).toList();
            return new StrategicLineNode(line.id(), line.name(), line.description(), line.status(), programs);
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WeightSummaryResponse programWeightSummary(UUID programId) {
        Program program = programRepository.findById(programId)
            .orElseThrow(() -> new ResourceNotFoundException("Programa no encontrado"));
        List<Project> projects = projectRepository.findByProgramId(programId);
        BigDecimal totalWeight = projects.stream()
            .map(p -> p.weight() != null ? p.weight() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<WeightItemResponse> items = projects.stream()
            .map(p -> new WeightItemResponse(p.id(), p.name(), p.weight()))
            .toList();
        return WeightSummaryResponse.of(programId, program.name(), totalWeight, items);
    }

    @Override
    @Transactional(readOnly = true)
    public WeightSummaryResponse projectWeightSummary(UUID projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        List<Activity> activities = activityRepository.findByProjectId(projectId);
        BigDecimal totalWeight = activities.stream()
            .map(a -> a.weight() != null ? a.weight() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<WeightItemResponse> items = activities.stream()
            .map(a -> new WeightItemResponse(a.id(), a.name(), a.weight()))
            .toList();
        return WeightSummaryResponse.of(projectId, project.name(), totalWeight, items);
    }
}
