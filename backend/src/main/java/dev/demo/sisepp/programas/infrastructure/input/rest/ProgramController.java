package dev.demo.sisepp.programas.infrastructure.input.rest;

import dev.demo.sisepp.programas.application.dto.CreateProgramRequest;
import dev.demo.sisepp.programas.application.dto.ProgramResponse;
import dev.demo.sisepp.programas.application.dto.UpdateProgramRequest;
import dev.demo.sisepp.programas.application.port.input.ProgramUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/structure/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramUseCase service;

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<List<ProgramResponse>> list(@RequestParam(required = false) UUID strategicLineId) {
        return ResponseEntity.ok(service.list(strategicLineId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<ProgramResponse> create(@Valid @RequestBody CreateProgramRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<ProgramResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateProgramRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
