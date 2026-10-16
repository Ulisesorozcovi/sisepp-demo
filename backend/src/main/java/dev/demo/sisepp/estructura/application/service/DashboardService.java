package dev.demo.sisepp.estructura.application.service;

import dev.demo.sisepp.estructura.application.dto.DashboardResponse;
import dev.demo.sisepp.estructura.application.port.input.DashboardUseCase;
import dev.demo.sisepp.estructura.application.port.output.DashboardQueryPort;
import dev.demo.sisepp.shared.exception.ResourceNotFoundException;
import dev.demo.sisepp.usuarios.domain.model.User;
import dev.demo.sisepp.usuarios.domain.model.UserRole;
import dev.demo.sisepp.usuarios.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardUseCase {

    private final DashboardQueryPort dashboardQuery;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse generate(Integer vigencia, String email) {
        int anio = (vigencia != null) ? vigencia : Year.now().getValue();

        User usuario = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        UserRole role = usuario.role();
        UUID userId = usuario.id();
        boolean esTecnico = role == UserRole.TECNICO;

        DashboardResponse.Totals totales = dashboardQuery.loadTotals(anio, userId, role);
        List<DashboardResponse.TaskAlert> vencidas = dashboardQuery.loadVencidas(anio, userId, role);
        List<DashboardResponse.TaskAlert> porVencer = dashboardQuery.loadPorVencer(anio, userId, role);
        List<DashboardResponse.TaskAlert> enRiesgo = dashboardQuery.loadEnRiesgo(anio, userId, role);

        List<DashboardResponse.LineProgress> lineas =
            esTecnico ? List.of() : dashboardQuery.loadLines(anio, userId, role);
        List<DashboardResponse.ProgramProgress> programas =
            esTecnico ? List.of() : dashboardQuery.loadPrograms(anio, userId, role);

        BigDecimal global = esTecnico
            ? dashboardQuery.loadAvanceTareas(anio, userId, role)
            : promedio(lineas);

        String ambito = switch (role) {
            case DIRECTOR, STAFF -> "GLOBAL";
            case COORDINADOR -> "PROYECTOS";
            case TECNICO -> "TAREAS";
        };

        return new DashboardResponse(
            anio, role.name(), ambito,
            global, totales, lineas, programas, vencidas, porVencer, enRiesgo
        );
    }

    private BigDecimal promedio(List<DashboardResponse.LineProgress> lineas) {
        if (lineas.isEmpty()) return BigDecimal.ZERO;
        return lineas.stream()
            .map(DashboardResponse.LineProgress::avance)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(lineas.size()), 1, RoundingMode.HALF_UP);
    }
}
