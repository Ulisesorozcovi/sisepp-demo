package dev.demo.sisepp.actividades.infrastructure.input.rest;

import dev.demo.sisepp.actividades.application.dto.ActivityResponse;
import dev.demo.sisepp.actividades.application.dto.CreateActivityRequest;
import dev.demo.sisepp.actividades.application.dto.UpdateActivityRequest;
import dev.demo.sisepp.actividades.application.port.input.ActivityUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/structure/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityUseCase service;

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<List<ActivityResponse>> list(@RequestParam(required = false) UUID projectId) {
        return ResponseEntity.ok(service.list(projectId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINADOR','STAFF')")
    public ResponseEntity<ActivityResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<ActivityResponse> create(@Valid @RequestBody CreateActivityRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','STAFF')")
    public ResponseEntity<ActivityResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateActivityRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
