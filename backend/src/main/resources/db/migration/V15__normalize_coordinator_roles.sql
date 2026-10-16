-- V15: Normalizar role_in_project a uppercase sin tildes para mapeo con enum Java

-- Actualizar datos existentes
UPDATE project_coordinators SET role_in_project = 'LIDER' WHERE role_in_project = 'Líder';
UPDATE project_coordinators SET role_in_project = 'LIDER' WHERE role_in_project = 'Lider';
UPDATE project_coordinators SET role_in_project = 'DIRECTIVO' WHERE role_in_project = 'Directivo';
UPDATE project_coordinators SET role_in_project = 'ADMINISTRADOR' WHERE role_in_project = 'Administrador';
UPDATE project_coordinators SET role_in_project = 'COORDINADOR' WHERE role_in_project = 'Coordinador';

-- Reemplazar CHECK constraint
ALTER TABLE project_coordinators DROP CONSTRAINT IF EXISTS project_coordinators_role_in_project_check;
ALTER TABLE project_coordinators
    ADD CONSTRAINT project_coordinators_role_in_project_check
        CHECK (role_in_project IN ('LIDER', 'DIRECTIVO', 'ADMINISTRADOR', 'COORDINADOR'));

-- Cambiar default
ALTER TABLE project_coordinators
    ALTER COLUMN role_in_project SET DEFAULT 'COORDINADOR';
