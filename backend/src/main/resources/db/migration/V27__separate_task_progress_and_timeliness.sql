-- ============================================================================
-- V27: Separar el estado de avance de la condición de puntualidad
-- ============================================================================
--
-- Problema que resuelve
-- ---------------------
-- La columna `status` mezclaba dos dimensiones independientes:
--   1. El avance del trabajo: PROGRAMADA -> EN_EJECUCION -> COMPLETADA
--   2. La puntualidad: a tiempo o con retraso
--
-- Al obligar a elegir un solo valor, una tarea RETRASADA que alcanzaba su meta
-- pasaba a COMPLETADA y se perdía el dato de que se cumplió fuera de plazo.
-- Para una entidad que reporta al Ministerio esa diferencia es información:
-- no es lo mismo cumplir la meta en marzo que cumplirla en noviembre.
--
-- Cómo queda
-- ----------
-- `status` conserva únicamente el ciclo de avance. La puntualidad deja de
-- almacenarse y se deriva:
--   * vencida                 -> due_date < CURRENT_DATE y la tarea sigue abierta
--   * completada con retraso  -> completed_at > due_date
--   * en riesgo               -> el avance va por debajo de lo esperado según
--                                el tiempo transcurrido (se calcula en consulta)
--
-- Derivarlo en vez de guardarlo evita necesitar un proceso programado que
-- recorra las tareas marcándolas: si ese proceso fallara, las alertas mentirían.
--
-- `risk_note` recoge lo que ninguna fórmula puede deducir: la causa que el
-- coordinador conoce (un contrato sin renovar, un predio en litigio).
-- ============================================================================

-- 1. Momento en que la tarea alcanzó su meta. NULL mientras siga abierta.
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP NULL;

COMMENT ON COLUMN tasks.completed_at IS
    'Fecha y hora en que el avance acumulado alcanzó la meta física. Comparada '
    'con due_date indica si la tarea se completó a tiempo o con retraso.';

-- 2. Nota de riesgo del coordinador: la causa, no la detección.
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS risk_note VARCHAR(500) NULL;

COMMENT ON COLUMN tasks.risk_note IS
    'Observación del coordinador sobre por qué una tarea va retrasada. '
    'Complementa la señal automática, que detecta el patrón pero no la causa.';

CREATE INDEX IF NOT EXISTS idx_tasks_completed_at ON tasks(completed_at);

-- ---------------------------------------------------------------------------
-- 3. Normalizar las tareas ya marcadas como COMPLETADA
-- ---------------------------------------------------------------------------
-- No existe registro histórico de cuándo se completaron. Se usa la fecha del
-- último avance reportado como mejor aproximación disponible, y si no hay
-- ninguno, la fecha de vencimiento (se asume cumplimiento en plazo, que es el
-- supuesto conservador: no inventa retrasos que nadie registró).
-- Se usa el cierre del último mes REPORTADO, no la fecha en que se digitó el
-- registro: un avance de junio puede haberse cargado en octubre, y lo que
-- importa para la puntualidad es cuándo se ejecutó el trabajo.
UPDATE tasks t
SET completed_at = COALESCE(
        (SELECT MAKE_DATE(f.reported_year, f.reported_month, 1)
                + INTERVAL '1 month' - INTERVAL '1 day'
         FROM follow_ups f
         WHERE f.task_id = t.id
         ORDER BY f.reported_year DESC, f.reported_month DESC
         LIMIT 1),
        t.due_date::timestamp
    )
WHERE t.status = 'COMPLETADA'
  AND t.completed_at IS NULL;

-- ---------------------------------------------------------------------------
-- 4. Reclasificar las tareas que estaban en RETRASADA
-- ---------------------------------------------------------------------------
-- RETRASADA deja de ser un estado de avance. Cada tarea pasa al estado que le
-- corresponde según lo que realmente reportó, y el motivo del retraso se
-- conserva en risk_note para no perder la información.
UPDATE tasks t
SET risk_note = COALESCE(
        NULLIF(LEFT(t.observation, 500), ''),
        'Reclasificada desde el estado RETRASADA al separar avance y puntualidad.'
    )
WHERE t.status = 'RETRASADA'
  AND t.risk_note IS NULL;

UPDATE tasks t
SET status = CASE
        WHEN COALESCE(
                (SELECT SUM(f.physical_progress) FROM follow_ups f WHERE f.task_id = t.id), 0
             ) >= t.physical_goal_value THEN 'COMPLETADA'
        WHEN COALESCE(
                (SELECT SUM(f.physical_progress) FROM follow_ups f WHERE f.task_id = t.id), 0
             ) > 0 THEN 'EN_EJECUCION'
        ELSE 'PROGRAMADA'
    END
WHERE t.status = 'RETRASADA';

-- Las que acabaron en COMPLETADA por la reclasificación necesitan su fecha.
UPDATE tasks t
SET completed_at = COALESCE(
        (SELECT MAKE_DATE(f.reported_year, f.reported_month, 1)
                + INTERVAL '1 month' - INTERVAL '1 day'
         FROM follow_ups f
         WHERE f.task_id = t.id
         ORDER BY f.reported_year DESC, f.reported_month DESC
         LIMIT 1),
        now()
    )
WHERE t.status = 'COMPLETADA'
  AND t.completed_at IS NULL;

-- ---------------------------------------------------------------------------
-- 5. RETRASADA se conserva en el CHECK por compatibilidad
-- ---------------------------------------------------------------------------
-- El enum de Java aún lo declara y hay instalaciones con datos históricos.
-- Ningún código vuelve a asignarlo: las transiciones automáticas solo producen
-- PROGRAMADA, EN_EJECUCION y COMPLETADA, y CANCELADA o NO_APLICA se fijan a mano.
