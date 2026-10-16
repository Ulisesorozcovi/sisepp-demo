-- =============================================================
-- V7: Programación de tareas por período
-- Permite programar una misma tarea base para diferentes períodos
-- con metas parciales y responsables específicos
-- Origen: hoja "Programación tareas" del Sheets (~906 registros)
-- =============================================================

CREATE TABLE task_schedules (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id         UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    activity_id     UUID NOT NULL REFERENCES activities(id) ON DELETE RESTRICT,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    scheduled_goal  NUMERIC(12,2),
    responsible     VARCHAR(150),
    observation     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_schedule_dates CHECK (end_date >= start_date)
);

CREATE INDEX idx_task_schedules_task ON task_schedules(task_id);
CREATE INDEX idx_task_schedules_activity ON task_schedules(activity_id);
CREATE INDEX idx_task_schedules_dates ON task_schedules(start_date, end_date);
