package dev.demo.sisepp.seguimiento.infrastructure.output.persistence;

import dev.demo.sisepp.seguimiento.domain.model.FollowUp;
import dev.demo.sisepp.seguimiento.domain.port.output.FollowUpRepositoryPort;
import dev.demo.sisepp.tareas.infrastructure.output.persistence.TaskJpaRepository;
import dev.demo.sisepp.usuarios.infrastructure.output.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FollowUpRepositoryAdapter implements FollowUpRepositoryPort {

    private final FollowUpJpaRepository jpaRepository;
    private final TaskJpaRepository taskJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public FollowUp save(FollowUp followUp) {
        FollowUpJpaEntity entity;
        if (followUp.id() != null) {
            entity = jpaRepository.findById(followUp.id())
                .orElseGet(() -> FollowUpMapper.toJpa(followUp));
            entity.setReportedMonth(followUp.reportedMonth());
            entity.setReportedYear(followUp.reportedYear());
            entity.setPhysicalProgress(followUp.physicalProgress());
            entity.setEvidenceUrl(followUp.evidenceUrl());
            entity.setAttachmentUrl(followUp.attachmentUrl());
            entity.setStatus(followUp.status());
            entity.setObservations(followUp.observations());
        } else {
            entity = FollowUpMapper.toJpa(followUp);
        }
        if (followUp.taskId() != null) {
            taskJpaRepository.findById(followUp.taskId()).ifPresent(entity::setTask);
        }
        if (followUp.reportedByUserId() != null) {
            userJpaRepository.findById(followUp.reportedByUserId()).ifPresent(entity::setReportedBy);
        }
        return FollowUpMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<FollowUp> findById(UUID id) {
        return jpaRepository.findById(id).map(FollowUpMapper::toDomain);
    }

    @Override
    public List<FollowUp> findByTaskId(UUID taskId) {
        return jpaRepository.findByTaskId(taskId).stream()
            .map(FollowUpMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<FollowUp> findByReportedYearAndReportedMonth(Integer year, Integer month) {
        return jpaRepository.findByReportedYearAndReportedMonth(year, month).stream()
            .map(FollowUpMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<FollowUp> findByReportedByUserId(UUID userId) {
        return jpaRepository.findByReportedBy_Id(userId).stream()
            .map(FollowUpMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<FollowUp> findByTaskIdIn(List<UUID> taskIds) {
        if (taskIds.isEmpty()) return List.of();
        return jpaRepository.findByTask_IdIn(taskIds).stream()
            .map(FollowUpMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTaskIdAndPeriod(UUID taskId, int year, int month) {
        return jpaRepository.existsByTask_IdAndReportedYearAndReportedMonth(taskId, year, month);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
