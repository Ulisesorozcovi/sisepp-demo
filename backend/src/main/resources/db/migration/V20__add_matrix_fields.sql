-- Proyectos: objetivo y PIP
ALTER TABLE projects
    ADD COLUMN objective TEXT,
    ADD COLUMN objective_weight NUMERIC(5,2),
    ADD COLUMN pip_id UUID REFERENCES public_investment_programs(id) ON DELETE SET NULL;

CREATE INDEX idx_projects_pip ON projects(pip_id);

-- Actividades: indicador, categoría temática, tipo de indicador
ALTER TABLE activities
    ADD COLUMN indicator_description TEXT,
    ADD COLUMN indicator_type VARCHAR(30)
        CHECK (indicator_type IN ('IMG', 'COMPLEMENTARIO', 'PROPIO')),
    ADD COLUMN thematic_category_id UUID REFERENCES thematic_categories(id) ON DELETE SET NULL,
    ADD COLUMN management_indicator_id UUID REFERENCES management_indicators(id) ON DELETE SET NULL,
    ADD COLUMN measurement_unit_id UUID REFERENCES measurement_units(id) ON DELETE SET NULL;

CREATE INDEX idx_activities_thematic ON activities(thematic_category_id);
CREATE INDEX idx_activities_mgmt_indicator ON activities(management_indicator_id);
CREATE INDEX idx_activities_mu ON activities(measurement_unit_id);
