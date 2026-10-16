package dev.demo.sisepp.planificacion.infrastructure.input.rest;

import dev.demo.sisepp.planificacion.application.dto.CreatePlanningInstrumentRequest;
import dev.demo.sisepp.planificacion.application.dto.PlanningInstrumentResponse;
import dev.demo.sisepp.planificacion.application.dto.UpdatePlanningInstrumentRequest;
import dev.demo.sisepp.planificacion.domain.model.InstrumentStatus;
import dev.demo.sisepp.planificacion.domain.model.InstrumentType;
import dev.demo.sisepp.planificacion.application.port.input.PlanningInstrumentUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/planning-instruments")
@RequiredArgsConstructor
public class PlanningInstrumentController {

    private final PlanningInstrumentUseCase service;

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<List<PlanningInstrumentResponse>> list(
        @RequestParam(required = false) InstrumentType type,
        @RequestParam(required = false) InstrumentStatus status) {
        return ResponseEntity.ok(service.list(type, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<PlanningInstrumentResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<PlanningInstrumentResponse> create(@Valid @RequestBody CreatePlanningInstrumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<PlanningInstrumentResponse> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdatePlanningInstrumentRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
