package dev.demo.sisepp.catalogo.application.port.input;

import dev.demo.sisepp.catalogo.application.dto.CreateStrategicLineRequest;
import dev.demo.sisepp.catalogo.application.dto.StrategicLineResponse;
import dev.demo.sisepp.catalogo.application.dto.UpdateStrategicLineRequest;

import java.util.List;
import java.util.UUID;

public interface StrategicLineUseCase {
    List<StrategicLineResponse> list();
    StrategicLineResponse create(CreateStrategicLineRequest request);
    StrategicLineResponse update(UUID id, UpdateStrategicLineRequest request);
}
