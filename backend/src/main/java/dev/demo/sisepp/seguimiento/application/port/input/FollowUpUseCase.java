package dev.demo.sisepp.seguimiento.application.port.input;

import dev.demo.sisepp.seguimiento.application.dto.CreateFollowUpRequest;
import dev.demo.sisepp.seguimiento.application.dto.FollowUpResponse;
import dev.demo.sisepp.seguimiento.application.dto.UpdateFollowUpRequest;

import java.util.List;
import java.util.UUID;

public interface FollowUpUseCase {
    FollowUpResponse create(CreateFollowUpRequest request, String requesterEmail);
    FollowUpResponse update(UUID id, UpdateFollowUpRequest request, String requesterEmail);
    List<FollowUpResponse> listByTask(UUID taskId);
    List<FollowUpResponse> listByPeriod(int year, int month);
    void delete(UUID id, String requesterEmail);
}
