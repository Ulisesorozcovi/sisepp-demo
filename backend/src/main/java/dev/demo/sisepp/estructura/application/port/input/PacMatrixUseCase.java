package dev.demo.sisepp.estructura.application.port.input;

import dev.demo.sisepp.estructura.application.dto.PacMatrixResponse;

import java.util.UUID;

public interface PacMatrixUseCase {
    PacMatrixResponse generate(UUID pacId, Integer vigencia);
}
