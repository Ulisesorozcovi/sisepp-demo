-- =============================================================
-- V11: Vincula líneas estratégicas al PGAR, programas y proyectos al PAC
-- =============================================================

-- Líneas estratégicas pertenecen a un PGAR
ALTER TABLE strategic_lines
    ADD COLUMN pgar_id UUID REFERENCES planning_instruments(id) ON DELETE RESTRICT;

CREATE INDEX idx_strategic_lines_pgar ON strategic_lines(pgar_id);

-- Programas pertenecen a un PAC
ALTER TABLE programs
    ADD COLUMN pac_id UUID REFERENCES planning_instruments(id) ON DELETE RESTRICT;

CREATE INDEX idx_programs_pac ON programs(pac_id);

-- Proyectos: referencia al PAC y rango de años en que aplica
ALTER TABLE projects
    ADD COLUMN pac_id UUID REFERENCES planning_instruments(id) ON DELETE RESTRICT,
    ADD COLUMN start_year INT,
    ADD COLUMN end_year INT;

CREATE INDEX idx_projects_pac ON projects(pac_id);
