package dev.demo.sisepp.financiero.infrastructure.input.rest;

import dev.demo.sisepp.financiero.application.dto.CreateFinancialExecutionRequest;
import dev.demo.sisepp.financiero.application.dto.FinancialExecutionResponse;
import dev.demo.sisepp.financiero.application.dto.UpdateFinancialExecutionRequest;
import dev.demo.sisepp.financiero.application.port.input.FinancialExecutionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financial-executions")
@RequiredArgsConstructor
public class FinancialExecutionController {

    private final FinancialExecutionUseCase service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','DIRECTOR','COORDINADOR')")
    public ResponseEntity<List<FinancialExecutionResponse>> list(
        @RequestParam(required = false) UUID projectId,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month) {
        if (projectId != null) {
            return ResponseEntity.ok(service.listByProject(projectId));
        }
        if (year != null && month != null) {
            return ResponseEntity.ok(service.listByPeriod(year, month));
        }
        return ResponseEntity.ok(service.listByProject(projectId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','DIRECTOR')")
    public ResponseEntity<FinancialExecutionResponse> create(
        @Valid @RequestBody CreateFinancialExecutionRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','DIRECTOR')")
    public ResponseEntity<FinancialExecutionResponse> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateFinancialExecutionRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','DIRECTOR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
