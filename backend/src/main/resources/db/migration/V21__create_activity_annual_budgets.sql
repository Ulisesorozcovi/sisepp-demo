CREATE TABLE activity_annual_budgets (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    activity_id     UUID NOT NULL REFERENCES activities(id) ON DELETE CASCADE,
    year            INT NOT NULL,
    budget_amount   NUMERIC(18,2) NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_activity_year_budget UNIQUE (activity_id, year)
);

CREATE INDEX idx_activity_budgets_activity ON activity_annual_budgets(activity_id);
CREATE INDEX idx_activity_budgets_year ON activity_annual_budgets(year);
