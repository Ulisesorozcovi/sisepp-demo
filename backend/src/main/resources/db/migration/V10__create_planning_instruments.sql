-- =============================================================
-- V10: Instrumentos de planificación: PGAR, PAC, POAI
-- Tabla autorreferenciada para jerarquía: PAC→PGAR, POAI→PAC
-- =============================================================

CREATE TABLE planning_instruments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type            VARCHAR(10) NOT NULL CHECK (type IN ('PGAR', 'PAC', 'POAI')),
    name            VARCHAR(200) NOT NULL,
    start_year      INT NOT NULL,
    end_year        INT NOT NULL,
    parent_id       UUID REFERENCES planning_instruments(id) ON DELETE RESTRICT,
    approved_date   DATE,
    approval_ref    VARCHAR(200),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
                        CHECK (status IN ('ACTIVO', 'CERRADO', 'EN_FORMULACION')),
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_year_range CHECK (end_year >= start_year),
    CONSTRAINT uq_instrument UNIQUE (type, start_year, end_year)
);

CREATE INDEX idx_planning_instruments_type ON planning_instruments(type);
CREATE INDEX idx_planning_instruments_parent ON planning_instruments(parent_id);
CREATE INDEX idx_planning_instruments_status ON planning_instruments(status);
