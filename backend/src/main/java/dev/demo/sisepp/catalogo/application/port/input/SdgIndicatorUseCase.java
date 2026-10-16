package dev.demo.sisepp.catalogo.application.port.input;

import dev.demo.sisepp.catalogo.application.dto.SdgIndicatorResponse;

import java.util.List;

public interface SdgIndicatorUseCase {
    List<SdgIndicatorResponse> list();
}
