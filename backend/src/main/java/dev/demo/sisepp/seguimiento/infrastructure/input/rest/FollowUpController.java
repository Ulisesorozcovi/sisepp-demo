package dev.demo.sisepp.seguimiento.infrastructure.input.rest;

import dev.demo.sisepp.seguimiento.application.dto.CreateFollowUpRequest;
import dev.demo.sisepp.seguimiento.application.dto.FollowUpResponse;
import dev.demo.sisepp.seguimiento.application.dto.UpdateFollowUpRequest;
import dev.demo.sisepp.seguimiento.application.port.input.FollowUpUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/follow-ups")
@RequiredArgsConstructor
public class FollowUpController {

    private final FollowUpUseCase service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FollowUpResponse>> list(
        @RequestParam(required = false) UUID taskId,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month) {
        if (taskId != null) {
            return ResponseEntity.ok(service.listByTask(taskId));
        }
        if (year != null && month != null) {
            return ResponseEntity.ok(service.listByPeriod(year, month));
        }
        return ResponseEntity.ok(service.listByTask(taskId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','COORDINADOR','TECNICO')")
    public ResponseEntity<FollowUpResponse> create(
        @AuthenticationPrincipal String email,
        @Valid @RequestBody CreateFollowUpRequest request) {
        return ResponseEntity.status(201).body(service.create(request, email));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','COORDINADOR','TECNICO')")
    public ResponseEntity<FollowUpResponse> update(
        @AuthenticationPrincipal String email,
        @PathVariable UUID id,
        @Valid @RequestBody UpdateFollowUpRequest request) {
        return ResponseEntity.ok(service.update(id, request, email));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','COORDINADOR','TECNICO')")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal String email, @PathVariable UUID id) {
        service.delete(id, email);
        return ResponseEntity.noContent().build();
    }
}
