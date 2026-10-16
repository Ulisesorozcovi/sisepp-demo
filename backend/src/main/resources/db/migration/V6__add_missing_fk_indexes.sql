-- =============================================================
-- V6: Índices faltantes en foreign keys existentes
-- Mejora rendimiento de consultas por coordinador,
-- por responsable, y por indicador ODS
-- =============================================================

-- Buscar proyectos por coordinador (usado en ProjectRepository.findByCoordinators_Id)
CREATE INDEX idx_project_coordinators_user ON project_coordinators(user_id);

-- Buscar actividades por indicador ODS
CREATE INDEX idx_activity_sdg_indicator ON activity_sdg_indicators(sdg_indicator_id);

-- Buscar tareas por responsable (filtro frecuente)
CREATE INDEX idx_tasks_responsible ON tasks(responsible_user_id);

-- Índices para nuevos campos de búsqueda frecuente (agregados en V5)
CREATE INDEX idx_programs_code ON programs(code);
CREATE INDEX idx_projects_code ON projects(code);
CREATE INDEX idx_projects_subdivision ON projects(subdivision);
CREATE INDEX idx_activities_code ON activities(code);
CREATE INDEX idx_tasks_start_date ON tasks(start_date);
