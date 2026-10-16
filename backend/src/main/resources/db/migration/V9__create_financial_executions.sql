-- =============================================================
-- V9: Ejecución presupuestal por proyecto y período
-- Registro de presupuesto, compromisos, obligaciones y pagos
-- Origen: hojas "Financiero" del Sheets (~879 registros por corte)
-- Nota: uncommitted_budget es calculable (approved - commitments)
-- =============================================================

CREATE TABLE financial_executions (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id       UUID NOT NULL REFERENCES projects(id) ON DELETE RESTRICT,
    period_month     INT NOT NULL CHECK (period_month BETWEEN 1 AND 12),
    period_year      INT NOT NULL,
    approved_budget  NUMERIC(18,2) DEFAULT 0,
    commitments      NUMERIC(18,2) DEFAULT 0,
    obligations      NUMERIC(18,2) DEFAULT 0,
    payments         NUMERIC(18,2) DEFAULT 0,
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_financial_project_period UNIQUE (project_id, period_year, period_month)
);

CREATE INDEX idx_financial_project ON financial_executions(project_id);
CREATE INDEX idx_financial_period ON financial_executions(period_year, period_month);
