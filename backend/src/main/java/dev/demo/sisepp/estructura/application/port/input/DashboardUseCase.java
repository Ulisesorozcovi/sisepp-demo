package dev.demo.sisepp.estructura.application.port.input;

import dev.demo.sisepp.estructura.application.dto.DashboardResponse;

public interface DashboardUseCase {
    DashboardResponse generate(Integer vigencia, String email);
}
