-- =============================================================
-- V12: Metas anuales por actividad
-- Distribución de la meta total del PAC por año
-- Ej: actividad con meta PAC total = 8, distribuida: 2024=2, 2025=2, 2026=2, 2027=2
-- =============================================================

CREATE TABLE activity_annual_goals (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    activity_id     UUID NOT NULL REFERENCES activities(id) ON DELETE CASCADE,
    year            INT NOT NULL,
    goal_value      NUMERIC(12,2) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_activity_year_goal UNIQUE (activity_id, year)
);

CREATE INDEX idx_activity_goals_activity ON activity_annual_goals(activity_id);
CREATE INDEX idx_activity_goals_year ON activity_annual_goals(year);
