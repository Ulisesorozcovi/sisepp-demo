package dev.demo.sisepp.proyectos.infrastructure.output.persistence;

import dev.demo.sisepp.catalogo.infrastructure.output.persistence.PublicInvestmentProgramJpaRepository;
import dev.demo.sisepp.programas.infrastructure.output.persistence.ProgramJpaRepository;
import dev.demo.sisepp.proyectos.domain.model.Project;
import dev.demo.sisepp.proyectos.domain.port.output.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {

    private final ProjectJpaRepository jpaRepository;
    private final ProgramJpaRepository programJpaRepository;
    private final PublicInvestmentProgramJpaRepository pipJpaRepository;
    private final ProjectCoordinatorJpaRepository coordinatorJpaRepository;

    @Override
    public Project save(Project project) {
        ProjectJpaEntity entity;
        if (project.id() != null) {
            entity = jpaRepository.findById(project.id()).orElseGet(() -> ProjectMapper.toJpa(project));
            entity.setName(project.name());
            entity.setVigencia(project.vigencia());
            entity.setCode(project.code());
            entity.setSubdivision(project.subdivision());
            entity.setWeight(project.weight());
            entity.setObjective(project.objective());
            entity.setObjectiveWeight(project.objectiveWeight());
            entity.setStartYear(project.startYear());
            entity.setEndYear(project.endYear());
            entity.setStatus(project.status());
        } else {
            entity = ProjectMapper.toJpa(project);
        }
        if (project.programId() != null) {
            programJpaRepository.findById(project.programId()).ifPresent(entity::setProgram);
        }
        if (project.pipId() != null) {
            pipJpaRepository.findById(project.pipId()).ifPresent(entity::setPip);
        } else {
            entity.setPip(null);
        }
        return ProjectMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaRepository.findById(id).map(ProjectMapper::toDomain);
    }

    @Override
    public List<Project> findByProgramId(UUID programId) {
        return jpaRepository.findByProgramId(programId).stream()
            .map(ProjectMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Project> findByVigencia(Integer vigencia) {
        return jpaRepository.findByVigencia(vigencia).stream()
            .map(ProjectMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Project> findByCoordinatorId(UUID userId) {
        return jpaRepository.findByCoordinatorEntries_UserId(userId).stream()
            .map(ProjectMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Project> findAll() {
        return jpaRepository.findAll().stream()
            .map(ProjectMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addCoordinator(UUID projectId, UUID userId) {
        if (coordinatorJpaRepository.findByProjectIdAndUserId(projectId, userId).isEmpty()) {
            coordinatorJpaRepository.save(
                ProjectCoordinatorJpaEntity.builder()
                    .projectId(projectId)
                    .userId(userId)
                    .build()
            );
        }
    }

    @Override
    @Transactional
    public void removeCoordinator(UUID projectId, UUID userId) {
        coordinatorJpaRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public BigDecimal sumWeightsByProgramId(UUID programId, UUID excludeId) {
        return jpaRepository.sumWeightsByProgramId(programId, excludeId);
    }

    @Override
    public List<Project> findByPacId(UUID pacId) {
        return jpaRepository.findByPacId(pacId).stream()
            .map(ProjectMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Project> findByVigenciaBetween(int startYear, int endYear) {
        return jpaRepository.findByVigenciaBetween(startYear, endYear).stream()
            .map(ProjectMapper::toDomain)
            .collect(Collectors.toList());
    }
}
