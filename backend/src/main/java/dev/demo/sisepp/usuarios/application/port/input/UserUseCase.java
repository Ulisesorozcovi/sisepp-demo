package dev.demo.sisepp.usuarios.application.port.input;

import dev.demo.sisepp.usuarios.application.dto.CreateUserRequest;
import dev.demo.sisepp.usuarios.application.dto.UpdateUserRequest;
import dev.demo.sisepp.usuarios.application.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserUseCase {
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> listUsers(String role, String status, String search);
    UserResponse updateUser(UUID id, UpdateUserRequest request, String callerEmail);
    UserResponse inactivateUser(UUID id);
    UserResponse reactivateUser(UUID id);
}
