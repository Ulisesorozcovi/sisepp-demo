-- =============================================================
-- V5: Campos de negocio faltantes según modelo de producción
-- Agrega: códigos PAC, ponderaciones, subdirección, unidades,
--         metas, tipos, observaciones, fechas de inicio
-- =============================================================

-- 1. strategic_lines: agregar descripción
ALTER TABLE strategic_lines
    ADD COLUMN description TEXT;

-- 2. programs: agregar código PAC, peso y descripción
ALTER TABLE programs
    ADD COLUMN code VARCHAR(20),
    ADD COLUMN weight NUMERIC(5,2),
    ADD COLUMN description TEXT;

-- 3. projects: agregar código PAC, subdirección y peso
ALTER TABLE projects
    ADD COLUMN code VARCHAR(30),
    ADD COLUMN subdivision VARCHAR(100),
    ADD COLUMN weight NUMERIC(5,2);

-- 4. project_coordinators: agregar rol en proyecto
ALTER TABLE project_coordinators
    ADD COLUMN role_in_project VARCHAR(30) DEFAULT 'Coordinador'
        CHECK (role_in_project IN ('Líder', 'Directivo', 'Administrador', 'Coordinador'));

-- 5. activities: agregar código PAC, unidad de medida, meta y peso
ALTER TABLE activities
    ADD COLUMN code VARCHAR(30),
    ADD COLUMN measurement_unit VARCHAR(50),
    ADD COLUMN goal NUMERIC(12,2),
    ADD COLUMN weight NUMERIC(5,2);

-- 6. sdg_indicators: agregar descripción
ALTER TABLE sdg_indicators
    ADD COLUMN description TEXT;

-- 7. tasks: agregar tipo, peso, observaciones y fecha inicio
ALTER TABLE tasks
    ADD COLUMN task_type VARCHAR(100),
    ADD COLUMN weight NUMERIC(5,2),
    ADD COLUMN observation TEXT,
    ADD COLUMN start_date DATE;

-- 8. users.role: ampliar CHECK para incluir ADMINISTRADOR
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users
    ADD CONSTRAINT users_role_check
        CHECK (role IN ('DIRECTOR', 'COORDINADOR', 'ADMINISTRADOR'));

-- 9. tasks.status: ampliar CHECK para incluir NO_APLICA y CANCELADA
ALTER TABLE tasks DROP CONSTRAINT IF EXISTS tasks_status_check;
ALTER TABLE tasks
    ADD CONSTRAINT tasks_status_check
        CHECK (status IN ('PROGRAMADA', 'EN_EJECUCION', 'RETRASADA', 'COMPLETADA', 'NO_APLICA', 'CANCELADA'));
