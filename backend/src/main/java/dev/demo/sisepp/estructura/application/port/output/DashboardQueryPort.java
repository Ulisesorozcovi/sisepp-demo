package dev.demo.sisepp.estructura.application.port.output;

import dev.demo.sisepp.estructura.application.dto.DashboardResponse;
import dev.demo.sisepp.usuarios.domain.model.UserRole;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DashboardQueryPort {

    DashboardResponse.Totals loadTotals(int vigencia, UUID userId, UserRole role);

    List<DashboardResponse.LineProgress> loadLines(int vigencia, UUID userId, UserRole role);

    List<DashboardResponse.ProgramProgress> loadPrograms(int vigencia, UUID userId, UserRole role);

    BigDecimal loadAvanceTareas(int vigencia, UUID userId, UserRole role);

    List<DashboardResponse.TaskAlert> loadVencidas(int vigencia, UUID userId, UserRole role);

    List<DashboardResponse.TaskAlert> loadPorVencer(int vigencia, UUID userId, UserRole role);

    List<DashboardResponse.TaskAlert> loadEnRiesgo(int vigencia, UUID userId, UserRole role);
}
