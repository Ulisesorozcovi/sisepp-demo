package dev.demo.sisepp.estructura;

import dev.demo.sisepp.IntegrationTestBase;
import dev.demo.sisepp.estructura.application.dto.DashboardResponse;
import dev.demo.sisepp.estructura.application.port.input.DashboardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Alcance de los datos según el rol de quien consulta.
 *
 * <p>Esto no es una comodidad de presentación sino una regla de acceso: un
 * coordinador responde por los proyectos que le asignaron y no debe ver la
 * cartera de otro, ni siquiera agregada. Por eso estas pruebas verifican que
 * el recorte ocurre en la consulta y no en la interfaz.
 */
@DisplayName("Alcance del resumen según el rol")
class DashboardScopeIT extends IntegrationTestBase {

    @Autowired JdbcTemplate jdbc;
    @Autowired DashboardUseCase dashboardService;

    private static final UUID PAC = UUID.fromString("a0000001-0000-0000-0000-000000000002");
    private static final UUID PGAR = UUID.fromString("a0000001-0000-0000-0000-000000000001");

    private static final String DIRECTOR = "director@example.com";
    private static final String COORDINADOR = "coordinador@example.com";
    private static final String TECNICO = "tecnico@example.com";

    private int vigencia;
    private UUID proyectoDelCoordinador;
    private UUID proyectoAjeno;
    private UUID tareaPropiaDelTecnico;

    @BeforeEach
    void prepararDosCarteras() {
        vigencia = LocalDate.now().getYear();

        UUID coordinadorId = jdbc.queryForObject(
            "SELECT id FROM users WHERE email = ?", UUID.class, COORDINADOR);
        UUID tecnicoId = jdbc.queryForObject(
            "SELECT id FROM users WHERE email = ?", UUID.class, TECNICO);

        // Cartera del coordinador: la ve.
        proyectoDelCoordinador = crearRama("PROPIO");
        jdbc.update("INSERT INTO project_coordinators (project_id, user_id) VALUES (?, ?)",
            proyectoDelCoordinador, coordinadorId);

        // Cartera de otro: no debe verla.
        proyectoAjeno = crearRama("AJENO");

        UUID actPropia = actividadDe(proyectoDelCoordinador);
        UUID actAjena = actividadDe(proyectoAjeno);

        tareaPropiaDelTecnico = crearTarea(actPropia, "Tarea del técnico", tecnicoId);
        crearTarea(actPropia, "Tarea de otro responsable", null);
        crearTarea(actAjena, "Tarea de cartera ajena", null);
    }

    private UUID crearRama(String etiqueta) {
        UUID lineaId = UUID.randomUUID();
        UUID programaId = UUID.randomUUID();
        UUID proyectoId = UUID.randomUUID();
        String sufijo = etiqueta + "-" + lineaId.toString().substring(0, 8);

        jdbc.update("INSERT INTO strategic_lines (id, name, pgar_id, status) VALUES (?, ?, ?, 'ACTIVO')",
            lineaId, "Línea " + sufijo, PGAR);
        jdbc.update("""
            INSERT INTO programs (id, code, name, strategic_line_id, pac_id, weight, status)
            VALUES (?, ?, ?, ?, ?, 100, 'ACTIVO')
            """, programaId, "PRG-" + sufijo, "Programa " + sufijo, lineaId, PAC);
        jdbc.update("""
            INSERT INTO projects (id, code, name, program_id, pac_id, vigencia, weight, status)
            VALUES (?, ?, ?, ?, ?, ?, 100, 'ACTIVO')
            """, proyectoId, "PRY-" + sufijo, "Proyecto " + sufijo, programaId, PAC, vigencia);
        return proyectoId;
    }

    private UUID actividadDe(UUID proyectoId) {
        UUID id = UUID.randomUUID();
        jdbc.update("""
            INSERT INTO activities (id, code, name, project_id, measurement_unit, goal, weight, status)
            VALUES (?, ?, ?, ?, 'Hectáreas', 100, 100, 'ACTIVO')
            """, id, "ACT-" + id.toString().substring(0, 8), "Actividad de prueba", proyectoId);
        return id;
    }

    private UUID crearTarea(UUID activityId, String nombre, UUID responsableId) {
        UUID id = UUID.randomUUID();
        jdbc.update("""
            INSERT INTO tasks (id, activity_id, name, physical_goal_value, physical_goal_unit,
                               start_date, due_date, responsible_name, responsible_user_id,
                               weight, status)
            VALUES (?, ?, ?, 100, 'Hectáreas', ?, ?, 'Responsable', ?, 100, 'PROGRAMADA')
            """, id, activityId, nombre,
            LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(2), responsableId);
        return id;
    }

    // ------------------------------------------------------------------

    @Test
    @DisplayName("el director ve las dos carteras")
    void directorVeTodo() {
        DashboardResponse resumen = dashboardService.generate(vigencia, DIRECTOR);

        assertThat(resumen.ambito()).isEqualTo("GLOBAL");
        assertThat(resumen.rol()).isEqualTo("DIRECTOR");
        assertThat(resumen.totales().proyectos()).isGreaterThanOrEqualTo(2);
        assertThat(resumen.totales().tareas()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("el coordinador no ve proyectos que no tiene asignados")
    void coordinadorSoloVeSuCartera() {
        DashboardResponse resumen = dashboardService.generate(vigencia, COORDINADOR);

        assertThat(resumen.ambito()).isEqualTo("PROYECTOS");

        // La comprobación de fondo: el proyecto ajeno no aparece por ningún lado.
        Long proyectosVisibles = jdbc.queryForObject("""
            SELECT COUNT(*) FROM projects p
            WHERE p.vigencia = ?
              AND p.id IN (SELECT project_id FROM project_coordinators
                           WHERE user_id = (SELECT id FROM users WHERE email = ?))
            """, Long.class, vigencia, COORDINADOR);

        assertThat(resumen.totales().proyectos()).isEqualTo(proyectosVisibles);

        assertThat(resumen.tareasVencidas()).noneMatch(t -> t.proyectoId().equals(proyectoAjeno));
        assertThat(resumen.tareasPorVencer()).noneMatch(t -> t.proyectoId().equals(proyectoAjeno));
        assertThat(resumen.tareasEnRiesgo()).noneMatch(t -> t.proyectoId().equals(proyectoAjeno));
    }

    @Test
    @DisplayName("el técnico solo ve las tareas de las que es responsable")
    void tecnicoSoloVeSusTareas() {
        DashboardResponse resumen = dashboardService.generate(vigencia, TECNICO);

        assertThat(resumen.ambito()).isEqualTo("TAREAS");

        Long tareasAsignadas = jdbc.queryForObject("""
            SELECT COUNT(*) FROM tasks t
            JOIN activities a ON a.id = t.activity_id
            JOIN projects   p ON p.id = a.project_id
            WHERE p.vigencia = ?
              AND t.responsible_user_id = (SELECT id FROM users WHERE email = ?)
            """, Long.class, vigencia, TECNICO);

        assertThat(resumen.totales().tareas()).isEqualTo(tareasAsignadas);

        // El árbol estratégico no le aporta nada a quien ejecuta: va vacío.
        assertThat(resumen.lineas()).isEmpty();
        assertThat(resumen.programas()).isEmpty();
    }

    @Test
    @DisplayName("el avance global nunca se sale del rango 0 a 100")
    void elAvanceEstaAcotado() {
        for (String email : new String[]{DIRECTOR, COORDINADOR, TECNICO}) {
            DashboardResponse resumen = dashboardService.generate(vigencia, email);
            assertThat(resumen.avanceFisicoGlobal())
                .as("avance para %s", email)
                .isBetween(BigDecimal.ZERO, new BigDecimal("100"));
        }
    }

    @Test
    @DisplayName("una tarea vencida aparece en el resumen de su responsable")
    void laTareaVencidaLlegaAlResponsable() {
        jdbc.update("UPDATE tasks SET due_date = ? WHERE id = ?",
            LocalDate.now().minusDays(5), tareaPropiaDelTecnico);

        DashboardResponse resumen = dashboardService.generate(vigencia, TECNICO);

        assertThat(resumen.totales().vencidas()).isGreaterThanOrEqualTo(1);
        assertThat(resumen.tareasVencidas())
            .anyMatch(t -> t.id().equals(tareaPropiaDelTecnico));
    }
}
