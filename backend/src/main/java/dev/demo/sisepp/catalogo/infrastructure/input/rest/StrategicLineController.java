package dev.demo.sisepp.catalogo.infrastructure.input.rest;

import dev.demo.sisepp.catalogo.application.dto.CreateStrategicLineRequest;
import dev.demo.sisepp.catalogo.application.dto.StrategicLineResponse;
import dev.demo.sisepp.catalogo.application.dto.UpdateStrategicLineRequest;
import dev.demo.sisepp.catalogo.application.port.input.StrategicLineUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/structure/strategic-lines")
@RequiredArgsConstructor
public class StrategicLineController {

    private final StrategicLineUseCase service;

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<List<StrategicLineResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<StrategicLineResponse> create(@Valid @RequestBody CreateStrategicLineRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<StrategicLineResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateStrategicLineRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
