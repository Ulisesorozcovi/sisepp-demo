package dev.demo.sisepp.tareas;

import dev.demo.sisepp.IntegrationTestBase;
import dev.demo.sisepp.seguimiento.application.dto.CreateFollowUpRequest;
import dev.demo.sisepp.seguimiento.application.dto.FollowUpResponse;
import dev.demo.sisepp.seguimiento.application.dto.UpdateFollowUpRequest;
import dev.demo.sisepp.seguimiento.domain.model.FollowUpStatus;
import dev.demo.sisepp.seguimiento.application.port.input.FollowUpUseCase;
import dev.demo.sisepp.shared.exception.DuplicateNameException;
import dev.demo.sisepp.tareas.application.dto.CreateTaskRequest;
import dev.demo.sisepp.tareas.application.dto.RescheduleTaskRequest;
import dev.demo.sisepp.tareas.application.dto.TaskResponse;
import dev.demo.sisepp.tareas.domain.model.TaskStatus;
import dev.demo.sisepp.tareas.application.port.input.TaskUseCase;
import dev.demo.sisepp.tareas.domain.port.output.TaskRescheduleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Ciclo de vida de una tarea y su relación con el avance reportado.
 *
 * <p>Cubre la regla que antes no existía: el estado de la tarea lo decide el
 * avance acumulado, no una edición manual. Hasta la V27 las tareas nacían
 * PROGRAMADA y nada volvía a moverlas.
 */
@DisplayName("Ciclo de vida de la tarea")
class TaskLifecycleIT extends IntegrationTestBase {

    @Autowired JdbcTemplate jdbc;
    @Autowired TaskUseCase taskService;
    @Autowired FollowUpUseCase followUpService;
    @Autowired TaskRescheduleRepositoryPort rescheduleRepository;

    /** PAC sembrado por la migración V13. */
    private static final UUID PAC = UUID.fromString("a0000001-0000-0000-0000-000000000002");
    private static final UUID PGAR = UUID.fromString("a0000001-0000-0000-0000-000000000001");

    private static final String COORDINADOR = "coordinador@example.com";

    private UUID activityId;
    private UUID tecnicoId;

    @BeforeEach
    void prepararEstructura() {
        // Cada prueba arranca con su propia rama del árbol, para que una no
        // dependa de lo que haya dejado otra.
        UUID lineaId = UUID.randomUUID();
        UUID programaId = UUID.randomUUID();
        UUID proyectoId = UUID.randomUUID();
        activityId = UUID.randomUUID();

        String sufijo = lineaId.toString().substring(0, 8);

        jdbc.update("""
            INSERT INTO strategic_lines (id, name, pgar_id, status)
            VALUES (?, ?, ?, 'ACTIVO')
            """, lineaId, "Línea de prueba " + sufijo, PGAR);

        jdbc.update("""
            INSERT INTO programs (id, code, name, strategic_line_id, pac_id, weight, status)
            VALUES (?, ?, ?, ?, ?, 100, 'ACTIVO')
            """, programaId, "PRG-" + sufijo, "Programa de prueba " + sufijo, lineaId, PAC);

        jdbc.update("""
            INSERT INTO projects (id, code, name, program_id, pac_id, vigencia, weight, status)
            VALUES (?, ?, ?, ?, ?, ?, 100, 'ACTIVO')
            """, proyectoId, "PRY-" + sufijo, "Proyecto de prueba " + sufijo,
            programaId, PAC, LocalDate.now().getYear());

        jdbc.update("""
            INSERT INTO activities (id, code, name, project_id, measurement_unit, goal, weight, status)
            VALUES (?, ?, ?, ?, 'Hectáreas', 100, 100, 'ACTIVO')
            """, activityId, "ACT-" + sufijo, "Actividad de prueba " + sufijo, proyectoId);

        tecnicoId = jdbc.queryForObject(
            "SELECT id FROM users WHERE email = 'tecnico@example.com'", UUID.class);

        // El coordinador debe tener el proyecto asignado o el sistema le niega
        // crear tareas en él: es la regla de alcance del mapa de procesos.
        jdbc.update("""
            INSERT INTO project_coordinators (project_id, user_id)
            SELECT ?, id FROM users WHERE email = ?
            ON CONFLICT DO NOTHING
            """, proyectoId, COORDINADOR);
    }

    private TaskResponse crearTarea(BigDecimal meta) {
        return taskService.create(new CreateTaskRequest(
            activityId,
            "Siembra en predios priorizados",
            meta,
            "Hectáreas",
            LocalDate.now().plusMonths(3),
            "Técnico Prueba",
            tecnicoId,
            "Operativa",
            new BigDecimal("100.00"),
            null,
            LocalDate.now().minusMonths(1)
        ), COORDINADOR);
    }

    private FollowUpResponse reportar(UUID taskId, int mes, BigDecimal avance) {
        return followUpService.create(new CreateFollowUpRequest(
            taskId, mes, LocalDate.now().getYear(), avance,
            "https://evidencias.test/" + mes, null, FollowUpStatus.EN_PROCESO, "Avance del mes"
        ), COORDINADOR);
    }

    private TaskStatus estadoDe(UUID taskId) {
        return TaskStatus.valueOf(
            jdbc.queryForObject("SELECT status FROM tasks WHERE id = ?", String.class, taskId));
    }

    // ------------------------------------------------------------------

    @Test
    @DisplayName("una tarea recién creada queda PROGRAMADA y sin fecha de completado")
    void tareaNaceProgramada() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));

        assertThat(tarea.status()).isEqualTo(TaskStatus.PROGRAMADA);
        assertThat(tarea.completedAt()).isNull();
        assertThat(tarea.completedLate()).isFalse();
    }

    @Test
    @DisplayName("el primer avance mueve la tarea a EN_EJECUCION")
    void primerAvanceArrancaLaTarea() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));

        reportar(tarea.id(), 1, new BigDecimal("30.00"));

        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.EN_EJECUCION);
        assertThat(jdbc.queryForObject(
            "SELECT completed_at FROM tasks WHERE id = ?", Object.class, tarea.id())).isNull();
    }

    @Test
    @DisplayName("alcanzar la meta completa la tarea y sella la fecha")
    void alcanzarLaMetaCompletaLaTarea() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));

        reportar(tarea.id(), 1, new BigDecimal("40.00"));
        reportar(tarea.id(), 2, new BigDecimal("60.00"));

        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.COMPLETADA);
        assertThat(jdbc.queryForObject(
            "SELECT completed_at FROM tasks WHERE id = ?", Object.class, tarea.id())).isNotNull();
    }

    @Test
    @DisplayName("superar la meta también completa la tarea, sin pasar del 100%")
    void superarLaMetaCompletaIgual() {
        TaskResponse tarea = crearTarea(new BigDecimal("50.00"));

        reportar(tarea.id(), 1, new BigDecimal("80.00"));

        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.COMPLETADA);
    }

    @Test
    @DisplayName("corregir un avance a la baja reabre la tarea y borra la fecha de completado")
    void corregirAvanceALaBajaReabreLaTarea() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));
        FollowUpResponse reporte = reportar(tarea.id(), 1, new BigDecimal("100.00"));
        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.COMPLETADA);

        // El técnico se equivocó al digitar: eran 40, no 100.
        followUpService.update(reporte.id(), new UpdateFollowUpRequest(
            new BigDecimal("40.00"), null, null, null, null), COORDINADOR);

        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.EN_EJECUCION);
        assertThat(jdbc.queryForObject(
            "SELECT completed_at FROM tasks WHERE id = ?", Object.class, tarea.id())).isNull();
    }

    @Test
    @DisplayName("borrar el único avance devuelve la tarea a PROGRAMADA")
    void borrarElAvanceDevuelveAProgramada() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));
        FollowUpResponse reporte = reportar(tarea.id(), 1, new BigDecimal("30.00"));
        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.EN_EJECUCION);

        followUpService.delete(reporte.id(), COORDINADOR);

        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.PROGRAMADA);
    }

    @Test
    @DisplayName("una tarea completada después de su fecha queda marcada como tardía")
    void completarFueraDePlazoMarcaRetraso() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));

        // Se fuerza una fecha límite ya vencida: el escenario que el sistema
        // debe poder distinguir de un cumplimiento en plazo.
        jdbc.update("UPDATE tasks SET due_date = ? WHERE id = ?",
            LocalDate.now().minusDays(10), tarea.id());

        reportar(tarea.id(), 1, new BigDecimal("100.00"));

        Boolean tardia = jdbc.queryForObject("""
            SELECT completed_at::date > due_date FROM tasks WHERE id = ?
            """, Boolean.class, tarea.id());

        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.COMPLETADA);
        assertThat(tardia).isTrue();
    }

    @Test
    @DisplayName("una tarea cancelada a mano no la reabre el avance reportado")
    void elRecalculoNoReabreTareasCerradasAMano() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));
        jdbc.update("UPDATE tasks SET status = 'CANCELADA' WHERE id = ?", tarea.id());

        reportar(tarea.id(), 1, new BigDecimal("50.00"));

        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.CANCELADA);
    }

    // ------------------------------------------------------------------

    @Test
    @DisplayName("no se admiten dos reportes de la misma tarea en el mismo mes")
    void rechazaReporteDuplicadoDelMismoMes() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));
        reportar(tarea.id(), 3, new BigDecimal("10.00"));

        assertThatThrownBy(() -> reportar(tarea.id(), 3, new BigDecimal("15.00")))
            .isInstanceOf(DuplicateNameException.class);
    }

    @Test
    @DisplayName("el mismo mes de años distintos sí son reportes separados")
    void permiteElMismoMesEnAniosDistintos() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));
        reportar(tarea.id(), 3, new BigDecimal("10.00"));

        FollowUpResponse otroAnio = followUpService.create(new CreateFollowUpRequest(
            tarea.id(), 3, LocalDate.now().getYear() - 1, new BigDecimal("5.00"),
            null, null, FollowUpStatus.EN_PROCESO, null), COORDINADOR);

        assertThat(otroAnio.id()).isNotNull();
        assertThat(followUpService.listByTask(tarea.id())).hasSize(2);
    }

    // ------------------------------------------------------------------

    @Test
    @DisplayName("reprogramar mueve la fecha y deja constancia de quién y por qué")
    void reprogramarDejaRastro() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));
        LocalDate fechaOriginal = tarea.dueDate();
        LocalDate nuevaFecha = fechaOriginal.plusMonths(2);

        taskService.reschedule(tarea.id(), new RescheduleTaskRequest(
            nuevaFecha,
            "Las jornadas se aplazaron por mareas altas atípicas en la bahía."
        ), COORDINADOR);

        LocalDate fechaGuardada = jdbc.queryForObject(
            "SELECT due_date FROM tasks WHERE id = ?", LocalDate.class, tarea.id());
        assertThat(fechaGuardada).isEqualTo(nuevaFecha);

        assertThat(rescheduleRepository.findByTaskIdOrderByChangedAtAsc(tarea.id()))
            .singleElement()
            .satisfies(r -> {
                assertThat(r.previousDueDate()).isEqualTo(fechaOriginal);
                assertThat(r.newDueDate()).isEqualTo(nuevaFecha);
                assertThat(r.justification()).contains("mareas altas");
                assertThat(r.changedByUserId()).isNotNull();
            });
    }

    @Test
    @DisplayName("reprogramar no altera el avance ya reportado ni el estado")
    void reprogramarNoTocaElAvance() {
        TaskResponse tarea = crearTarea(new BigDecimal("100.00"));
        reportar(tarea.id(), 1, new BigDecimal("30.00"));

        taskService.reschedule(tarea.id(), new RescheduleTaskRequest(
            LocalDate.now().plusMonths(6),
            "Se extiende el plazo por demora en la contratación del operador."
        ), COORDINADOR);

        assertThat(estadoDe(tarea.id())).isEqualTo(TaskStatus.EN_EJECUCION);
        assertThat(followUpService.listByTask(tarea.id())).hasSize(1);
    }
}
