package dev.demo.sisepp.financiero.application.service;

import dev.demo.sisepp.financiero.application.dto.CreateFinancialExecutionRequest;
import dev.demo.sisepp.financiero.application.dto.FinancialExecutionResponse;
import dev.demo.sisepp.financiero.application.dto.UpdateFinancialExecutionRequest;
import dev.demo.sisepp.financiero.domain.model.FinancialExecution;
import dev.demo.sisepp.financiero.application.port.input.FinancialExecutionUseCase;
import dev.demo.sisepp.financiero.domain.port.output.FinancialExecutionRepositoryPort;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialExecutionService implements FinancialExecutionUseCase {

    private final FinancialExecutionRepositoryPort repository;

    @Override
    @Transactional
    public FinancialExecutionResponse create(CreateFinancialExecutionRequest request) {
        FinancialExecution execution = new FinancialExecution(
            null, request.projectId(), request.periodMonth(), request.periodYear(),
            request.approvedBudget(),
            request.commitments() != null ? request.commitments() : BigDecimal.ZERO,
            request.obligations() != null ? request.obligations() : BigDecimal.ZERO,
            request.payments() != null ? request.payments() : BigDecimal.ZERO,
            null, null
        );
        return FinancialExecutionResponse.fromDomain(repository.save(execution));
    }

    @Override
    @Transactional
    public FinancialExecutionResponse update(UUID id, UpdateFinancialExecutionRequest request) {
        FinancialExecution existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ejecución financiera no encontrada"));

        FinancialExecution updated = new FinancialExecution(
            existing.id(), existing.projectId(), existing.periodMonth(), existing.periodYear(),
            request.approvedBudget() != null ? request.approvedBudget() : existing.approvedBudget(),
            request.commitments() != null ? request.commitments() : existing.commitments(),
            request.obligations() != null ? request.obligations() : existing.obligations(),
            request.payments() != null ? request.payments() : existing.payments(),
            existing.createdAt(), existing.updatedAt()
        );
        return FinancialExecutionResponse.fromDomain(repository.save(updated));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialExecutionResponse> listByProject(UUID projectId) {
        return repository.findByProjectId(projectId).stream()
            .map(FinancialExecutionResponse::fromDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialExecutionResponse> listByPeriod(int year, int month) {
        return repository.findByPeriodYearAndPeriodMonth(year, month).stream()
            .map(FinancialExecutionResponse::fromDomain)
            .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Ejecución financiera no encontrada");
        }
        repository.deleteById(id);
    }
}
