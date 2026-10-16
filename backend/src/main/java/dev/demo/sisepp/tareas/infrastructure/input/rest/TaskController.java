package dev.demo.sisepp.tareas.infrastructure.input.rest;

import dev.demo.sisepp.tareas.domain.model.TaskStatus;
import dev.demo.sisepp.tareas.application.dto.CreateTaskRequest;
import dev.demo.sisepp.tareas.application.dto.RescheduleTaskRequest;
import dev.demo.sisepp.tareas.application.dto.TaskResponse;
import dev.demo.sisepp.tareas.application.dto.TaskWithProgressResponse;
import dev.demo.sisepp.tareas.application.port.input.TaskUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskUseCase taskService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskResponse>> list(
        @AuthenticationPrincipal String email,
        @RequestParam(required = false) UUID projectId,
        @RequestParam(required = false) UUID activityId,
        @RequestParam(required = false) TaskStatus status,
        @RequestParam(required = false) String responsible,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(taskService.list(email, projectId, activityId, status, responsible, from, to));
    }

    @GetMapping("/with-progress")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskWithProgressResponse>> listWithProgress(
        @RequestParam(required = false) UUID activityId,
        @RequestParam(required = false) UUID projectId) {
        return ResponseEntity.ok(taskService.listWithProgress(activityId, projectId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','COORDINADOR')")
    public ResponseEntity<TaskResponse> create(
        @AuthenticationPrincipal String email, @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(201).body(taskService.create(request, email));
    }

    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('STAFF','COORDINADOR')")
    public ResponseEntity<TaskResponse> reschedule(
        @AuthenticationPrincipal String email, @PathVariable UUID id, @Valid @RequestBody RescheduleTaskRequest request) {
        return ResponseEntity.ok(taskService.reschedule(id, request, email));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
