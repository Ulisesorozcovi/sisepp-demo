package dev.demo.sisepp.usuarios.infrastructure.input.rest;

import dev.demo.sisepp.usuarios.application.dto.CreateUserRequest;
import dev.demo.sisepp.usuarios.application.dto.UpdateUserRequest;
import dev.demo.sisepp.usuarios.application.dto.UserResponse;
import dev.demo.sisepp.usuarios.application.port.input.UserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','DIRECTOR')")
    public ResponseEntity<List<UserResponse>> list(
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String search) {
        return ResponseEntity.ok(userService.listUsers(role, status, search));
    }

    @GetMapping("/assignable")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponse>> listAssignable() {
        return ResponseEntity.ok(userService.listUsers("TECNICO", "ACTIVO", null));
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(201).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<UserResponse> update(
        @AuthenticationPrincipal String email,
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request, email));
    }

    @PatchMapping("/{id}/inactivate")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<UserResponse> inactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.inactivateUser(id));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<UserResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.reactivateUser(id));
    }
}
