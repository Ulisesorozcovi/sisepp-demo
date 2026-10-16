-- =============================================================
-- V8: Seguimiento mensual de avance por tarea
-- Registro de avance físico, evidencias y estado por mes
-- Origen: hoja "Seguimiento" del Sheets (~969 registros)
-- =============================================================

CREATE TABLE follow_ups (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id           UUID NOT NULL REFERENCES tasks(id) ON DELETE RESTRICT,
    reported_month    INT NOT NULL CHECK (reported_month BETWEEN 1 AND 12),
    reported_year     INT NOT NULL,
    physical_progress NUMERIC(12,2),
    evidence_url      TEXT,
    status            VARCHAR(30) NOT NULL DEFAULT 'EN_PROCESO'
        CHECK (status IN ('EN_PROCESO', 'CUMPLIDA', 'REZAGADA', 'NO_APLICA')),
    observations      TEXT,
    reported_by       UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT now(),
    updated_at        TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_followup_task_month UNIQUE (task_id, reported_year, reported_month)
);

CREATE INDEX idx_follow_ups_task ON follow_ups(task_id);
CREATE INDEX idx_follow_ups_period ON follow_ups(reported_year, reported_month);
CREATE INDEX idx_follow_ups_status ON follow_ups(status);
CREATE INDEX idx_follow_ups_reported_by ON follow_ups(reported_by);
