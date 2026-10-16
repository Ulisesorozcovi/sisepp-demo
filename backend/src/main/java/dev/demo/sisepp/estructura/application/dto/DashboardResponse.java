package dev.demo.sisepp.estructura.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Resumen ejecutivo del avance físico del PAC para una vigencia.
 *
 * El avance se consolida de forma ponderada siguiendo la jerarquía
 * tarea -> actividad -> proyecto -> programa -> línea estratégica.
 * El global es el promedio de las líneas, porque las líneas estratégicas
 * no tienen peso propio en el modelo de datos.
 */
public record DashboardResponse(
    Integer vigencia,
    /** Rol de quien consulta: DIRECTOR, STAFF, COORDINADOR o TECNICO. */
    String rol,
    /** Alcance de los datos: GLOBAL, PROYECTOS (los asignados) o TAREAS (las propias). */
    String ambito,
    BigDecimal avanceFisicoGlobal,
    Totals totales,
    List<LineProgress> lineas,
    List<ProgramProgress> programas,
    List<TaskAlert> tareasVencidas,
    List<TaskAlert> tareasPorVencer,
    /** Aún no vencen, pero su avance va por debajo de lo esperado para el tiempo transcurrido. */
    List<TaskAlert> tareasEnRiesgo
) {

    public record Totals(
        long lineas,
        long programas,
        long proyectos,
        long actividades,
        long tareas,
        long completadas,
        long enEjecucion,
        long programadas,
        long retrasadas,
        long vencidas,
        long porVencer,
        long enRiesgo,
        long completadasConRetraso
    ) {}

    public record LineProgress(
        UUID id,
        String nombre,
        BigDecimal avance
    ) {}

    public record ProgramProgress(
        UUID id,
        String codigo,
        String nombre,
        String linea,
        BigDecimal avance,
        BigDecimal peso
    ) {}

    /**
     * Tarea que requiere atención, con el detalle suficiente para mostrarla
     * y reprogramarla sin tener que volver a consultar el backend.
     *
     * @param dias días transcurridos desde el vencimiento (positivo) o que
     *             faltan para vencer (negativo)
     */
    public record TaskAlert(
        UUID id,
        String tarea,
        String actividad,
        String proyectoCodigo,
        String proyecto,
        UUID proyectoId,
        String responsable,
        BigDecimal meta,
        String unidad,
        BigDecimal reportado,
        BigDecimal cumplimiento,
        LocalDate fechaInicio,
        LocalDate fechaVencimiento,
        long dias,
        String estado,
        String observacion,
        /** Fracción del plazo ya consumida, de 0 a 1. */
        BigDecimal tiempoTranscurrido,
        /**
         * Avance real dividido entre el esperado por el tiempo transcurrido.
         * Menor que 1 significa que va por detrás. Null cuando la tarea apenas
         * arranca y todavía no hay base para juzgarla.
         */
        BigDecimal ritmo,
        /** VENCIDA, EN_RIESGO, ATENCION, EN_RUTA o SIN_JUICIO. */
        String senal,
        /** Causa del retraso según el coordinador. */
        String notaRiesgo
    ) {}
}
