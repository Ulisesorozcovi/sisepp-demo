package dev.demo.sisepp.seguimiento.domain.port.output;

import dev.demo.sisepp.seguimiento.domain.model.FollowUp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowUpRepositoryPort {
    FollowUp save(FollowUp followUp);
    Optional<FollowUp> findById(UUID id);
    List<FollowUp> findByTaskId(UUID taskId);
    List<FollowUp> findByReportedYearAndReportedMonth(Integer year, Integer month);
    List<FollowUp> findByReportedByUserId(UUID userId);
    List<FollowUp> findByTaskIdIn(List<UUID> taskIds);
    boolean existsByTaskIdAndPeriod(UUID taskId, int year, int month);
    void deleteById(UUID id);
}
