package dev.demo.sisepp.estructura.infrastructure.output.persistence;

import dev.demo.sisepp.estructura.application.dto.DashboardResponse;
import dev.demo.sisepp.estructura.application.port.output.DashboardQueryPort;
import dev.demo.sisepp.usuarios.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DashboardJdbcAdapter implements DashboardQueryPort {

    private final NamedParameterJdbcTemplate jdbc;

    private static final String ABIERTAS = "('PROGRAMADA','EN_EJECUCION','RETRASADA')";

    private static final String TIEMPO =
        "CASE WHEN t.start_date IS NULL OR t.due_date <= t.start_date THEN NULL "
        + "ELSE LEAST(GREATEST((CURRENT_DATE - t.start_date)::numeric "
        + "/ (t.due_date - t.start_date), 0), 1) END";

    private static final String AVANCE =
        "LEAST(COALESCE(f.reportado, 0) / NULLIF(t.physical_goal_value, 0), 1)";

    private static final String SENAL =
        "CASE"
        + " WHEN t.due_date < CURRENT_DATE THEN 'VENCIDA'"
        + " WHEN t.physical_goal_value = 1 THEN 'SIN_JUICIO'"
        + " WHEN (" + TIEMPO + ") IS NULL OR (" + TIEMPO + ") < 0.30 THEN 'SIN_JUICIO'"
        + " WHEN (" + AVANCE + ") / NULLIF((" + TIEMPO + "), 0) < 0.70 THEN 'EN_RIESGO'"
        + " WHEN (" + AVANCE + ") / NULLIF((" + TIEMPO + "), 0) < 0.90 THEN 'ATENCION'"
        + " ELSE 'EN_RUTA' END";

    private record Scope(String filtroProyecto, String filtroTarea) {
        static Scope forRole(UserRole role) {
            return switch (role) {
                case DIRECTOR, STAFF -> new Scope("", "");
                case COORDINADOR -> new Scope(
                    " AND p.id IN (SELECT project_id FROM project_coordinators WHERE user_id = :userId)", "");
                case TECNICO -> new Scope(
                    "", " AND t.responsible_user_id = :userId");
            };
        }
    }

    private enum Filtro {
        VENCIDAS("t.due_date < CURRENT_DATE"),
        POR_VENCER("t.due_date >= CURRENT_DATE AND t.due_date <= CURRENT_DATE + 30"),
        EN_RIESGO("t.due_date >= CURRENT_DATE AND (" + SENAL + ") = 'EN_RIESGO'");

        final String condicion;
        Filtro(String condicion) { this.condicion = condicion; }
    }

    private MapSqlParameterSource params(int vigencia, UUID userId) {
        return new MapSqlParameterSource()
            .addValue("vigencia", vigencia)
            .addValue("userId", userId);
    }

    // ------------------------------------------------------------------

    @Override
    public DashboardResponse.Totals loadTotals(int vigencia, UUID userId, UserRole role) {
        Scope scope = Scope.forRole(role);
        String sql = """
            SELECT
                COUNT(DISTINCT sl.id) AS lineas,
                COUNT(DISTINCT pr.id) AS programas,
                COUNT(DISTINCT p.id)  AS proyectos,
                COUNT(DISTINCT a.id)  AS actividades,
                COUNT(t.id) AS tareas,
                COUNT(*) FILTER (WHERE t.status = 'COMPLETADA')   AS completadas,
                COUNT(*) FILTER (WHERE t.status = 'EN_EJECUCION') AS en_ejecucion,
                COUNT(*) FILTER (WHERE t.status = 'PROGRAMADA')   AS programadas,
                COUNT(*) FILTER (WHERE t.status = 'RETRASADA')    AS retrasadas,
                COUNT(*) FILTER (WHERE t.status IN %s
                                   AND t.due_date < CURRENT_DATE) AS vencidas,
                COUNT(*) FILTER (WHERE t.status IN %s
                                   AND t.due_date >= CURRENT_DATE
                                   AND t.due_date <= CURRENT_DATE + 30) AS por_vencer,
                COUNT(*) FILTER (WHERE t.status IN %s
                                   AND t.due_date >= CURRENT_DATE
                                   AND (%s) = 'EN_RIESGO') AS en_riesgo,
                COUNT(*) FILTER (WHERE t.status = 'COMPLETADA'
                                   AND t.completed_at IS NOT NULL
                                   AND t.completed_at::date > t.due_date) AS completadas_con_retraso
            FROM tasks t
            JOIN activities a ON a.id = t.activity_id
            JOIN projects   p ON p.id = a.project_id
            JOIN programs   pr ON pr.id = p.program_id
            JOIN strategic_lines sl ON sl.id = pr.strategic_line_id
            LEFT JOIN LATERAL (
                SELECT SUM(physical_progress) AS reportado
                FROM follow_ups WHERE task_id = t.id
            ) f ON TRUE
            WHERE p.vigencia = :vigencia %s %s
            """.formatted(ABIERTAS, ABIERTAS, ABIERTAS, SENAL,
                          scope.filtroProyecto(), scope.filtroTarea());

        return jdbc.queryForObject(sql, params(vigencia, userId), (rs, i) -> new DashboardResponse.Totals(
            rs.getLong("lineas"), rs.getLong("programas"), rs.getLong("proyectos"),
            rs.getLong("actividades"), rs.getLong("tareas"), rs.getLong("completadas"),
            rs.getLong("en_ejecucion"), rs.getLong("programadas"), rs.getLong("retrasadas"),
            rs.getLong("vencidas"), rs.getLong("por_vencer"), rs.getLong("en_riesgo"),
            rs.getLong("completadas_con_retraso")
        ));
    }

    // ------------------------------------------------------------------

    private String cteAvance(Scope scope) {
        return """
            WITH avance_tarea AS (
                SELECT a.project_id,
                       LEAST(COALESCE(f.reportado, 0) / NULLIF(t.physical_goal_value, 0), 1.0)
                         * (COALESCE(NULLIF(t.weight, 0), 0) / 100.0)
                         * (COALESCE(NULLIF(a.weight, 0), 0) / 100.0) AS aporte
                FROM tasks t
                JOIN activities a ON a.id = t.activity_id
                JOIN projects   p ON p.id = a.project_id
                LEFT JOIN LATERAL (
                    SELECT SUM(physical_progress) AS reportado
                    FROM follow_ups WHERE task_id = t.id
                ) f ON TRUE
                WHERE p.vigencia = :vigencia
                  AND t.status NOT IN ('CANCELADA', 'NO_APLICA')
                  %s %s
            ),
            avance_proyecto AS (
                SELECT p.id, p.program_id,
                       COALESCE(NULLIF(p.weight, 0), 0) / 100.0 AS w,
                       COALESCE(SUM(at.aporte), 0) AS avance
                FROM projects p
                LEFT JOIN avance_tarea at ON at.project_id = p.id
                WHERE p.vigencia = :vigencia AND p.status = 'ACTIVO' %s
                GROUP BY p.id, p.program_id, p.weight
            ),
            avance_programa AS (
                SELECT pr.id, pr.strategic_line_id,
                       COALESCE(NULLIF(pr.weight, 0), 0) / 100.0 AS w,
                       COALESCE(SUM(ap.w * ap.avance), 0) AS avance
                FROM programs pr
                JOIN avance_proyecto ap ON ap.program_id = pr.id
                WHERE pr.status = 'ACTIVO'
                GROUP BY pr.id, pr.strategic_line_id, pr.weight
            )
            """.formatted(scope.filtroProyecto(), scope.filtroTarea(), scope.filtroProyecto());
    }

    @Override
    public List<DashboardResponse.LineProgress> loadLines(int vigencia, UUID userId, UserRole role) {
        Scope scope = Scope.forRole(role);
        String sql = cteAvance(scope) + """
            SELECT sl.id, sl.name,
                   ROUND(COALESCE(SUM(apr.w * apr.avance), 0) * 100, 1) AS pct
            FROM strategic_lines sl
            JOIN avance_programa apr ON apr.strategic_line_id = sl.id
            WHERE sl.status = 'ACTIVO'
            GROUP BY sl.id, sl.name
            ORDER BY sl.name
            """;

        return jdbc.query(sql, params(vigencia, userId), (rs, i) -> new DashboardResponse.LineProgress(
            rs.getObject("id", UUID.class), rs.getString("name"), rs.getBigDecimal("pct")
        ));
    }

    @Override
    public List<DashboardResponse.ProgramProgress> loadPrograms(int vigencia, UUID userId, UserRole role) {
        Scope scope = Scope.forRole(role);
        String sql = cteAvance(scope) + """
            SELECT pr.id, pr.code, pr.name, sl.name AS linea, pr.weight,
                   ROUND(COALESCE(SUM(ap.w * ap.avance), 0) * 100, 1) AS pct
            FROM programs pr
            JOIN strategic_lines sl ON sl.id = pr.strategic_line_id
            JOIN avance_proyecto ap ON ap.program_id = pr.id
            WHERE pr.status = 'ACTIVO'
            GROUP BY pr.id, pr.code, pr.name, sl.name, pr.weight
            ORDER BY pr.code
            """;

        return jdbc.query(sql, params(vigencia, userId), (rs, i) -> new DashboardResponse.ProgramProgress(
            rs.getObject("id", UUID.class), rs.getString("code"), rs.getString("name"),
            rs.getString("linea"), rs.getBigDecimal("pct"), rs.getBigDecimal("weight")
        ));
    }

    @Override
    public BigDecimal loadAvanceTareas(int vigencia, UUID userId, UserRole role) {
        Scope scope = Scope.forRole(role);
        String sql = """
            SELECT ROUND(COALESCE(AVG(
                       LEAST(COALESCE(f.reportado, 0) / NULLIF(t.physical_goal_value, 0), 1.0)
                   ), 0) * 100, 1) AS pct
            FROM tasks t
            JOIN activities a ON a.id = t.activity_id
            JOIN projects   p ON p.id = a.project_id
            LEFT JOIN LATERAL (
                SELECT SUM(physical_progress) AS reportado
                FROM follow_ups WHERE task_id = t.id
            ) f ON TRUE
            WHERE p.vigencia = :vigencia
              AND t.status NOT IN ('CANCELADA', 'NO_APLICA')
              %s
            """.formatted(scope.filtroTarea());

        BigDecimal pct = jdbc.queryForObject(sql, params(vigencia, userId), BigDecimal.class);
        return pct != null ? pct : BigDecimal.ZERO;
    }

    @Override
    public List<DashboardResponse.TaskAlert> loadVencidas(int vigencia, UUID userId, UserRole role) {
        return loadAlerts(vigencia, userId, role, Filtro.VENCIDAS);
    }

    @Override
    public List<DashboardResponse.TaskAlert> loadPorVencer(int vigencia, UUID userId, UserRole role) {
        return loadAlerts(vigencia, userId, role, Filtro.POR_VENCER);
    }

    @Override
    public List<DashboardResponse.TaskAlert> loadEnRiesgo(int vigencia, UUID userId, UserRole role) {
        return loadAlerts(vigencia, userId, role, Filtro.EN_RIESGO);
    }

    private List<DashboardResponse.TaskAlert> loadAlerts(int vigencia, UUID userId, UserRole role, Filtro filtro) {
        Scope scope = Scope.forRole(role);
        String sql = """
            SELECT t.id, t.name, t.physical_goal_value, t.physical_goal_unit,
                   t.start_date, t.due_date, t.responsible_name, t.status,
                   t.observation, t.risk_note,
                   a.name AS actividad,
                   p.id AS proyecto_id, p.code AS proyecto_codigo, p.name AS proyecto,
                   COALESCE(f.reportado, 0) AS reportado,
                   (CURRENT_DATE - t.due_date) AS dias,
                   ROUND((%s)::numeric, 3) AS tiempo,
                   CASE WHEN (%s) IS NULL OR (%s) = 0 THEN NULL
                        ELSE ROUND(((%s) / (%s))::numeric, 2) END AS ritmo,
                   (%s) AS senal
            FROM tasks t
            JOIN activities a ON a.id = t.activity_id
            JOIN projects   p ON p.id = a.project_id
            LEFT JOIN LATERAL (
                SELECT SUM(physical_progress) AS reportado
                FROM follow_ups WHERE task_id = t.id
            ) f ON TRUE
            WHERE p.vigencia = :vigencia
              AND t.status IN %s
              AND %s
              %s %s
            ORDER BY t.due_date
            LIMIT 100
            """.formatted(
                TIEMPO,
                TIEMPO, TIEMPO, AVANCE, TIEMPO,
                SENAL,
                ABIERTAS, filtro.condicion, scope.filtroProyecto(), scope.filtroTarea());

        return jdbc.query(sql, params(vigencia, userId), (rs, i) -> {
            Date inicio = rs.getDate("start_date");
            Date vence = rs.getDate("due_date");

            BigDecimal meta = rs.getBigDecimal("physical_goal_value");
            BigDecimal reportado = rs.getBigDecimal("reportado");
            BigDecimal cumplimiento = BigDecimal.ZERO;
            if (meta != null && meta.signum() != 0 && reportado != null) {
                cumplimiento = reportado
                    .multiply(BigDecimal.valueOf(100))
                    .divide(meta, 1, RoundingMode.HALF_UP);
                if (cumplimiento.compareTo(BigDecimal.valueOf(100)) > 0) {
                    cumplimiento = BigDecimal.valueOf(100);
                }
            }

            return new DashboardResponse.TaskAlert(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("actividad"),
                rs.getString("proyecto_codigo"),
                rs.getString("proyecto"),
                rs.getObject("proyecto_id", UUID.class),
                rs.getString("responsible_name"),
                meta,
                rs.getString("physical_goal_unit"),
                reportado,
                cumplimiento,
                inicio != null ? inicio.toLocalDate() : null,
                vence != null ? vence.toLocalDate() : null,
                rs.getLong("dias"),
                rs.getString("status"),
                rs.getString("observation"),
                rs.getBigDecimal("tiempo"),
                rs.getBigDecimal("ritmo"),
                rs.getString("senal"),
                rs.getString("risk_note")
            );
        });
    }
}
