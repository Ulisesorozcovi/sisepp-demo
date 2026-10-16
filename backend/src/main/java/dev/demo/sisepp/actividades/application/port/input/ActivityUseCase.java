package dev.demo.sisepp.actividades.application.port.input;

import dev.demo.sisepp.actividades.application.dto.ActivityResponse;
import dev.demo.sisepp.actividades.application.dto.CreateActivityRequest;
import dev.demo.sisepp.actividades.application.dto.UpdateActivityRequest;

import java.util.List;
import java.util.UUID;

public interface ActivityUseCase {
    List<ActivityResponse> list(UUID projectId);
    ActivityResponse getById(UUID id);
    ActivityResponse create(CreateActivityRequest request);
    ActivityResponse update(UUID id, UpdateActivityRequest request);
}
