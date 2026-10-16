package dev.demo.sisepp.financiero.application.port.input;

import dev.demo.sisepp.financiero.application.dto.CreateFinancialExecutionRequest;
import dev.demo.sisepp.financiero.application.dto.FinancialExecutionResponse;
import dev.demo.sisepp.financiero.application.dto.UpdateFinancialExecutionRequest;

import java.util.List;
import java.util.UUID;

public interface FinancialExecutionUseCase {
    FinancialExecutionResponse create(CreateFinancialExecutionRequest request);
    FinancialExecutionResponse update(UUID id, UpdateFinancialExecutionRequest request);
    List<FinancialExecutionResponse> listByProject(UUID projectId);
    List<FinancialExecutionResponse> listByPeriod(int year, int month);
    void delete(UUID id);
}
