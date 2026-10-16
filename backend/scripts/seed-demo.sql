-- ============================================================================
--  SISEPP - Datos de demostración del PAC 2024-2027
-- ============================================================================
--
--  NO es una migración de Flyway. Es un script opcional que se ejecuta a mano
--  para poblar un entorno de demostración o de pruebas. Los datos de demo no
--  deben vivir en las migraciones: viajarían a producción.
--
--  Ejecución:
--    psql -h localhost -U sisepp_user -d sisepp -v seed_password=<CAMBIAR> -f seed-demo.sql
--
--  En Windows (PowerShell):
--    $env:PGPASSWORD="<CAMBIAR>"
--    $env:PGCLIENTENCODING="UTF8"
--    & "C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -U sisepp_user -d sisepp -v seed_password=<CAMBIAR> -f scripts\seed-demo.sql
--
--  Es idempotente: borra sus propios datos y los vuelve a crear. Puede
--  ejecutarse las veces que haga falta para dejar la demo en su estado inicial.
--  No toca la estructura que creen los usuarios por la interfaz, solo lo suyo
--  (identificado por UUID con prefijo b00000).
--
--  Escenarios sembrados, a propósito distintos entre sí:
--    PRY-2026-01  Proyecto al día       -> avance alto, reportes completos
--    PRY-2026-02  Proyecto rezagado     -> tareas vencidas sin reportar
--    PRY-2026-03  Proyecto recién abierto -> programado, sin avance
--    PRY-2026-04..08  Proyectos normales -> avance parcial
--
--  Fecha de referencia del escenario: octubre de 2026, vigencia 2026.
-- ============================================================================

-- El archivo está guardado en UTF-8. En Windows la consola suele estar en
-- WIN1252 y psql interpretaría mal las tildes, así que se declara aquí.
\encoding UTF8

\set ON_ERROR_STOP on

BEGIN;

-- ---------------------------------------------------------------------------
-- 0. Limpieza de datos de demostración previos
-- ---------------------------------------------------------------------------
DELETE FROM follow_ups        WHERE task_id IN (SELECT id FROM tasks WHERE id::text LIKE 'b0000005%');
DELETE FROM task_reschedules  WHERE task_id IN (SELECT id FROM tasks WHERE id::text LIKE 'b0000005%');
DELETE FROM tasks             WHERE id::text LIKE 'b0000005%';
DELETE FROM activity_annual_goals    WHERE activity_id IN (SELECT id FROM activities WHERE id::text LIKE 'b0000004%');
DELETE FROM activity_annual_budgets  WHERE activity_id IN (SELECT id FROM activities WHERE id::text LIKE 'b0000004%');
DELETE FROM activity_sdg_indicators  WHERE activity_id IN (SELECT id FROM activities WHERE id::text LIKE 'b0000004%');
DELETE FROM activities        WHERE id::text LIKE 'b0000004%';
DELETE FROM financial_executions     WHERE project_id IN (SELECT id FROM projects WHERE id::text LIKE 'b0000003%');
DELETE FROM project_coordinators     WHERE project_id IN (SELECT id FROM projects WHERE id::text LIKE 'b0000003%');
DELETE FROM projects          WHERE id::text LIKE 'b0000003%';
DELETE FROM programs          WHERE id::text LIKE 'b0000002%';
DELETE FROM strategic_lines   WHERE id::text LIKE 'b0000001%';
-- Los usuarios demo se borran al final, cuando ya no quedan tareas que los referencien.
DELETE FROM password_reset_tokens WHERE user_id IN (SELECT id FROM users WHERE id::text LIKE 'c0000001%');
DELETE FROM users             WHERE id::text LIKE 'c0000001%';


-- ---------------------------------------------------------------------------
-- 1. Equipo de trabajo: coordinadores y técnicos
-- ---------------------------------------------------------------------------
-- Contraseña de todos: la que pases con -v seed_password=...
-- Los coordinadores responden por proyectos; los técnicos ejecutan y reportan.

INSERT INTO users (id, full_name, email, password_hash, role, status) VALUES
-- Coordinadores
('c0000001-0000-0000-0000-000000000001', 'Tomás Gómez Restrepo',
 'tomas.gomez@example.com',      crypt(:'seed_password', gen_salt('bf', 10)), 'COORDINADOR', 'ACTIVO'),
('c0000001-0000-0000-0000-000000000002', 'Laura Mendoza Pérez',
 'laura.mendoza@example.com',    crypt(:'seed_password', gen_salt('bf', 10)), 'COORDINADOR', 'ACTIVO'),
-- Técnicos
('c0000001-0000-0000-0000-000000000011', 'Andrés Cuesta Mosquera',
 'andres.cuesta@example.com',    crypt(:'seed_password', gen_salt('bf', 10)), 'TECNICO', 'ACTIVO'),
('c0000001-0000-0000-0000-000000000012', 'Diana Palacios Rentería',
 'diana.palacios@example.com',   crypt(:'seed_password', gen_salt('bf', 10)), 'TECNICO', 'ACTIVO'),
('c0000001-0000-0000-0000-000000000013', 'Jhon Córdoba Asprilla',
 'jhon.cordoba@example.com',     crypt(:'seed_password', gen_salt('bf', 10)), 'TECNICO', 'ACTIVO'),
('c0000001-0000-0000-0000-000000000014', 'Marcela Úsuga Higuita',
 'marcela.usuga@example.com',    crypt(:'seed_password', gen_salt('bf', 10)), 'TECNICO', 'ACTIVO'),
('c0000001-0000-0000-0000-000000000015', 'Sebastián Durango Vélez',
 'sebastian.durango@example.com',crypt(:'seed_password', gen_salt('bf', 10)), 'TECNICO', 'ACTIVO'),
('c0000001-0000-0000-0000-000000000016', 'Yuliana Mena Moreno',
 'yuliana.mena@example.com',     crypt(:'seed_password', gen_salt('bf', 10)), 'TECNICO', 'ACTIVO')
ON CONFLICT (email) DO UPDATE
SET full_name = EXCLUDED.full_name, role = EXCLUDED.role,
    status = EXCLUDED.status, password_hash = EXCLUDED.password_hash, updated_at = now();

-- ---------------------------------------------------------------------------
-- 2. Líneas estratégicas  (cuelgan del PGAR 2026-2037)
-- ---------------------------------------------------------------------------
INSERT INTO strategic_lines (id, name, description, pgar_id, status) VALUES
('b0000001-0000-0000-0000-000000000001',
 'Gestión integral del recurso hídrico',
 'Administración, protección y uso sostenible del agua superficial y subterránea en la jurisdicción.',
 'a0000001-0000-0000-0000-000000000001', 'ACTIVO'),
('b0000001-0000-0000-0000-000000000002',
 'Conservación y restauración de ecosistemas',
 'Recuperación de áreas degradadas y protección de la biodiversidad del golfo de Urabá.',
 'a0000001-0000-0000-0000-000000000001', 'ACTIVO'),
('b0000001-0000-0000-0000-000000000003',
 'Gestión ambiental territorial y cambio climático',
 'Ordenamiento ambiental, gestión del riesgo y cultura ambiental ciudadana.',
 'a0000001-0000-0000-0000-000000000001', 'ACTIVO');

-- ---------------------------------------------------------------------------
-- 3. Programas  (pesos suman 100 dentro de cada línea)
-- ---------------------------------------------------------------------------
INSERT INTO programs (id, code, name, description, strategic_line_id, pac_id, weight, status) VALUES
('b0000002-0000-0000-0000-000000000001', 'PRG-01',
 'Administración del recurso hídrico',
 'Concesiones, reglamentación de corrientes y monitoreo de la oferta hídrica.',
 'b0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000002', 55.00, 'ACTIVO'),
('b0000002-0000-0000-0000-000000000002', 'PRG-02',
 'Saneamiento y calidad del agua',
 'Vigilancia de vertimientos y mejoramiento de la calidad de las fuentes hídricas.',
 'b0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000002', 45.00, 'ACTIVO'),
('b0000002-0000-0000-0000-000000000003', 'PRG-03',
 'Restauración de áreas degradadas',
 'Reforestación, restauración de manglares y recuperación de suelos.',
 'b0000001-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000002', 60.00, 'ACTIVO'),
('b0000002-0000-0000-0000-000000000004', 'PRG-04',
 'Conservación de la biodiversidad',
 'Áreas protegidas, fauna silvestre y conectividad ecológica.',
 'b0000001-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000002', 40.00, 'ACTIVO'),
('b0000002-0000-0000-0000-000000000005', 'PRG-05',
 'Gestión del riesgo y cultura ambiental',
 'Prevención de desastres, educación ambiental y participación comunitaria.',
 'b0000001-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000002', 100.00, 'ACTIVO');

-- ---------------------------------------------------------------------------
-- 4. Proyectos  (vigencia 2026)
-- ---------------------------------------------------------------------------
INSERT INTO projects (id, code, name, program_id, pac_id, vigencia, start_year, end_year,
                      subdivision, objective, objective_weight, weight, status) VALUES

-- ESCENARIO 1: al día
('b0000003-0000-0000-0000-000000000001', 'PRY-2026-01',
 'Restauración de ecosistemas degradados en el golfo de Urabá',
 'b0000002-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000002',
 2026, 2024, 2027, 'Territorial Centro',
 'Recuperar la cobertura vegetal y los servicios ecosistémicos en áreas priorizadas de manglar y bosque seco tropical.',
 100.00, 60.00, 'ACTIVO'),

-- ESCENARIO 2: rezagado
('b0000003-0000-0000-0000-000000000002', 'PRY-2026-02',
 'Monitoreo de la calidad del agua en cuencas priorizadas',
 'b0000002-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000002',
 2026, 2024, 2027, 'Territorial Norte',
 'Mantener la red de monitoreo de calidad hídrica y generar información para la toma de decisiones.',
 100.00, 60.00, 'ACTIVO'),

-- ESCENARIO 3: recién abierto
('b0000003-0000-0000-0000-000000000003', 'PRY-2026-03',
 'Educación ambiental y participación comunitaria',
 'b0000002-0000-0000-0000-000000000005', 'a0000001-0000-0000-0000-000000000002',
 2026, 2024, 2027, 'Territorial Centro',
 'Fortalecer la cultura ambiental mediante procesos formativos con comunidades e instituciones educativas.',
 100.00, 30.00, 'ACTIVO'),

-- ESCENARIO 4: normales
('b0000003-0000-0000-0000-000000000004', 'PRY-2026-04',
 'Reglamentación y legalización del uso del agua',
 'b0000002-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000002',
 2026, 2024, 2027, 'Territorial Norte',
 'Otorgar y hacer seguimiento a concesiones de agua y permisos de vertimiento.',
 100.00, 100.00, 'ACTIVO'),
('b0000003-0000-0000-0000-000000000005', 'PRY-2026-05',
 'Conservación de áreas protegidas y fauna silvestre',
 'b0000002-0000-0000-0000-000000000004', 'a0000001-0000-0000-0000-000000000002',
 2026, 2024, 2027, 'Territorial Sur',
 'Administrar las áreas protegidas regionales y atender la fauna silvestre decomisada o entregada.',
 100.00, 100.00, 'ACTIVO'),
('b0000003-0000-0000-0000-000000000006', 'PRY-2026-06',
 'Recuperación de microcuencas abastecedoras',
 'b0000002-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000002',
 2026, 2024, 2027, 'Territorial Sur',
 'Proteger las microcuencas que abastecen acueductos municipales y veredales.',
 100.00, 40.00, 'ACTIVO'),
('b0000003-0000-0000-0000-000000000007', 'PRY-2026-07',
 'Vigilancia de vertimientos industriales y domésticos',
 'b0000002-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000002',
 2026, 2024, 2027, 'Territorial Centro',
 'Controlar las descargas de aguas residuales sobre fuentes hídricas de la jurisdicción.',
 100.00, 40.00, 'ACTIVO'),
('b0000003-0000-0000-0000-000000000008', 'PRY-2026-08',
 'Gestión del riesgo de desastres de origen natural',
 'b0000002-0000-0000-0000-000000000005', 'a0000001-0000-0000-0000-000000000002',
 2026, 2024, 2027, 'Territorial Norte',
 'Apoyar a los municipios en la identificación y reducción del riesgo por inundación y erosión costera.',
 100.00, 70.00, 'ACTIVO');

-- ---------------------------------------------------------------------------
-- 5. Asignación de coordinadores
-- ---------------------------------------------------------------------------
-- Sin esta asignación el coordinador no ve el proyecto (regla del mapa de procesos).
-- Cada coordinador responde por una cartera distinta. Así se comprueba que
-- un coordinador solo ve lo suyo, que es la regla del mapa de procesos.
INSERT INTO project_coordinators (project_id, user_id) VALUES
-- Tomás Gómez: restauración, educación ambiental y microcuencas
('b0000003-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001'),
('b0000003-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000001'),
('b0000003-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000001'),
-- Laura Mendoza: recurso hídrico y vertimientos
('b0000003-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000002'),
('b0000003-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000002'),
('b0000003-0000-0000-0000-000000000007', 'c0000001-0000-0000-0000-000000000002'),
-- Coordinador de prueba: biodiversidad y gestión del riesgo
('b0000003-0000-0000-0000-000000000005',
 (SELECT id FROM users WHERE email = 'coordinador@example.com')),
('b0000003-0000-0000-0000-000000000008',
 (SELECT id FROM users WHERE email = 'coordinador@example.com'))
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------------
-- 6. Actividades
-- ---------------------------------------------------------------------------
INSERT INTO activities (id, code, name, project_id, measurement_unit, goal, weight,
                        indicator_description, indicator_type, status) VALUES

-- --- PRY-01 (al día): 4 actividades ---
('b0000004-0000-0000-0000-000000000101', 'ACT-01-01',
 'Establecimiento de plantaciones forestales protectoras',
 'b0000003-0000-0000-0000-000000000001', 'Hectáreas', 120.00, 30.00,
 'Hectáreas establecidas en restauración activa', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000102', 'ACT-01-02',
 'Mantenimiento de áreas restauradas en vigencias anteriores',
 'b0000003-0000-0000-0000-000000000001', 'Hectáreas', 200.00, 25.00,
 'Hectáreas con mantenimiento efectivo', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000103', 'ACT-01-03',
 'Restauración de manglar en la bahía de Turbo',
 'b0000003-0000-0000-0000-000000000001', 'Hectáreas', 45.00, 30.00,
 'Hectáreas de manglar en proceso de restauración', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000104', 'ACT-01-04',
 'Acuerdos de conservación con familias campesinas',
 'b0000003-0000-0000-0000-000000000001', 'Acuerdos', 60.00, 15.00,
 'Acuerdos suscritos y en seguimiento', 'COMPLEMENTARIO', 'ACTIVO'),

-- --- PRY-02 (rezagado): 2 actividades ---
('b0000004-0000-0000-0000-000000000201', 'ACT-02-01',
 'Monitoreo fisicoquímico de corrientes superficiales',
 'b0000003-0000-0000-0000-000000000002', 'Monitoreos', 48.00, 60.00,
 'Campañas de monitoreo ejecutadas', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000202', 'ACT-02-02',
 'Elaboración de informes de calidad hídrica',
 'b0000003-0000-0000-0000-000000000002', 'Informes', 4.00, 40.00,
 'Informes trimestrales publicados', 'PROPIO', 'ACTIVO'),

-- --- PRY-03 (recién abierto): 2 actividades ---
('b0000004-0000-0000-0000-000000000301', 'ACT-03-01',
 'Talleres de educación ambiental en instituciones educativas',
 'b0000003-0000-0000-0000-000000000003', 'Capacitaciones', 36.00, 60.00,
 'Talleres realizados con cobertura verificada', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000302', 'ACT-03-02',
 'Conformación de grupos ecológicos comunitarios',
 'b0000003-0000-0000-0000-000000000003', 'Comités', 12.00, 40.00,
 'Grupos conformados y activos', 'COMPLEMENTARIO', 'ACTIVO'),

-- --- PRY-04..08 (normales): 1 o 2 actividades cada uno ---
('b0000004-0000-0000-0000-000000000401', 'ACT-04-01',
 'Trámite de concesiones de agua superficial',
 'b0000003-0000-0000-0000-000000000004', 'Actos Administrativos', 90.00, 60.00,
 'Actos administrativos expedidos', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000402', 'ACT-04-02',
 'Seguimiento a permisos de vertimiento vigentes',
 'b0000003-0000-0000-0000-000000000004', 'Visitas', 75.00, 40.00,
 'Visitas de seguimiento realizadas', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000501', 'ACT-05-01',
 'Administración de áreas protegidas regionales',
 'b0000003-0000-0000-0000-000000000005', 'Hectáreas', 15000.00, 55.00,
 'Hectáreas con plan de manejo en ejecución', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000502', 'ACT-05-02',
 'Atención de fauna silvestre decomisada o entregada',
 'b0000003-0000-0000-0000-000000000005', 'Individuos', 240.00, 45.00,
 'Individuos atendidos en el CAV', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000601', 'ACT-06-01',
 'Aislamiento y protección de microcuencas abastecedoras',
 'b0000003-0000-0000-0000-000000000006', 'Kilómetros', 18.00, 100.00,
 'Kilómetros de cerca viva establecidos', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000701', 'ACT-07-01',
 'Operativos de control a vertimientos',
 'b0000003-0000-0000-0000-000000000007', 'Visitas', 60.00, 100.00,
 'Operativos de control ejecutados', 'IMG', 'ACTIVO'),
('b0000004-0000-0000-0000-000000000801', 'ACT-08-01',
 'Asistencia técnica a municipios en gestión del riesgo',
 'b0000003-0000-0000-0000-000000000008', 'Acciones', 38.00, 100.00,
 'Municipios con asistencia técnica efectiva', 'IMG', 'ACTIVO');

-- Metas anuales 2024-2027: la de 2026 es la del campo goal; las otras se derivan.
INSERT INTO activity_annual_goals (activity_id, year, goal_value)
SELECT a.id, y.year,
       CASE y.year
           WHEN 2024 THEN ROUND(a.goal * 0.85, 2)
           WHEN 2025 THEN ROUND(a.goal * 0.95, 2)
           WHEN 2026 THEN a.goal
           WHEN 2027 THEN ROUND(a.goal * 1.05, 2)
       END
FROM activities a
CROSS JOIN (VALUES (2024),(2025),(2026),(2027)) AS y(year)
WHERE a.id::text LIKE 'b0000004%'
ON CONFLICT (activity_id, year) DO UPDATE SET goal_value = EXCLUDED.goal_value;

-- Presupuesto anual por actividad
INSERT INTO activity_annual_budgets (activity_id, year, budget_amount)
SELECT a.id, y.year, ROUND((a.goal * 1850000 * CASE y.year WHEN 2026 THEN 1.0 ELSE 0.9 END)::numeric, 2)
FROM activities a
CROSS JOIN (VALUES (2024),(2025),(2026),(2027)) AS y(year)
WHERE a.id::text LIKE 'b0000004%'
ON CONFLICT (activity_id, year) DO UPDATE SET budget_amount = EXCLUDED.budget_amount;

-- Asociación con indicadores ODS (6 agua, 13 clima, 15 vida terrestre)
INSERT INTO activity_sdg_indicators (activity_id, sdg_indicator_id)
SELECT a.id, s.id
FROM activities a
JOIN sdg_indicators s ON s.id IN (6, 13, 15)
WHERE a.id::text LIKE 'b0000004%'
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------------
-- 7. Tareas
-- ---------------------------------------------------------------------------
-- responsible_user_id apunta al TECNICO, que es quien reporta el avance.
INSERT INTO tasks (id, activity_id, name, physical_goal_value, physical_goal_unit,
                   start_date, due_date, responsible_name, responsible_user_id,
                   task_type, weight, status, observation) VALUES

-- ===== PRY-01 — AL DÍA — reportes completos de enero a septiembre =====
('b0000005-0000-0000-0000-000000000101', 'b0000004-0000-0000-0000-000000000101',
 'Producción de material vegetal en vivero', 40000.00, 'Unidades',
 '2026-01-15', '2026-06-30', 'Andrés Cuesta Mosquera',
 'c0000001-0000-0000-0000-000000000011',
 'Operativa', 30.00, 'COMPLETADA', 'Vivero de Apartadó. Meta superada en junio.'),
('b0000005-0000-0000-0000-000000000102', 'b0000004-0000-0000-0000-000000000101',
 'Preparación de terreno y trazado en predios priorizados', 120.00, 'Hectáreas',
 '2026-02-01', '2026-07-31', 'Andrés Cuesta Mosquera',
 'c0000001-0000-0000-0000-000000000011',
 'Operativa', 35.00, 'COMPLETADA', NULL),
('b0000005-0000-0000-0000-000000000103', 'b0000004-0000-0000-0000-000000000101',
 'Siembra y fertilización inicial', 120.00, 'Hectáreas',
 '2026-04-01', '2026-11-30', 'Andrés Cuesta Mosquera',
 'c0000001-0000-0000-0000-000000000011',
 'Operativa', 35.00, 'EN_EJECUCION', 'Avanza según cronograma de lluvias.'),

('b0000005-0000-0000-0000-000000000104', 'b0000004-0000-0000-0000-000000000102',
 'Control de malezas en áreas restauradas', 200.00, 'Hectáreas',
 '2026-01-20', '2026-10-31', 'Diana Palacios Rentería',
 'c0000001-0000-0000-0000-000000000012',
 'Operativa', 50.00, 'EN_EJECUCION', NULL),
('b0000005-0000-0000-0000-000000000105', 'b0000004-0000-0000-0000-000000000102',
 'Reposición de plántulas perdidas', 200.00, 'Hectáreas',
 '2026-03-01', '2026-11-30', 'Diana Palacios Rentería',
 'c0000001-0000-0000-0000-000000000012',
 'Operativa', 50.00, 'EN_EJECUCION', NULL),

('b0000005-0000-0000-0000-000000000106', 'b0000004-0000-0000-0000-000000000103',
 'Recolección de propágulos de mangle', 30000.00, 'Unidades',
 '2026-01-10', '2026-05-31', 'Andrés Cuesta Mosquera',
 'c0000001-0000-0000-0000-000000000011',
 'Operativa', 40.00, 'COMPLETADA', 'Jornadas con comunidad de Bahía El Uno.'),
('b0000005-0000-0000-0000-000000000107', 'b0000004-0000-0000-0000-000000000103',
 'Siembra de propágulos en áreas de manglar degradado', 45.00, 'Hectáreas',
 '2026-03-01', '2026-12-15', 'Andrés Cuesta Mosquera',
 'c0000001-0000-0000-0000-000000000011',
 'Operativa', 60.00, 'EN_EJECUCION', NULL),

('b0000005-0000-0000-0000-000000000108', 'b0000004-0000-0000-0000-000000000104',
 'Caracterización socioeconómica de familias candidatas', 60.00, 'Acuerdos',
 '2026-01-15', '2026-04-30', 'Tomás Gómez Restrepo',
 'c0000001-0000-0000-0000-000000000001',
 'Administrativa', 40.00, 'COMPLETADA', NULL),
('b0000005-0000-0000-0000-000000000109', 'b0000004-0000-0000-0000-000000000104',
 'Suscripción y seguimiento de acuerdos de conservación', 60.00, 'Acuerdos',
 '2026-05-01', '2026-11-30', 'Tomás Gómez Restrepo',
 'c0000001-0000-0000-0000-000000000001',
 'Administrativa', 60.00, 'EN_EJECUCION', NULL),

-- ===== PRY-02 — REZAGADO — vencidas sin reportar =====
('b0000005-0000-0000-0000-000000000201', 'b0000004-0000-0000-0000-000000000201',
 'Campañas de monitoreo del primer semestre', 24.00, 'Monitoreos',
 '2026-01-15', '2026-06-30', 'Jhon Córdoba Asprilla',
 'c0000001-0000-0000-0000-000000000013',
 'Operativa', 50.00, 'PROGRAMADA', 'Laboratorio sin contrato vigente desde marzo.'),
('b0000005-0000-0000-0000-000000000202', 'b0000004-0000-0000-0000-000000000201',
 'Campañas de monitoreo del segundo semestre', 24.00, 'Monitoreos',
 '2026-07-01', '2026-09-30', 'Jhon Córdoba Asprilla',
 'c0000001-0000-0000-0000-000000000013',
 'Operativa', 50.00, 'PROGRAMADA', NULL),
('b0000005-0000-0000-0000-000000000203', 'b0000004-0000-0000-0000-000000000202',
 'Informe de calidad hídrica del primer trimestre', 1.00, 'Informes',
 '2026-04-01', '2026-04-30', 'Laura Mendoza Pérez',
 'c0000001-0000-0000-0000-000000000002',
 'Administrativa', 25.00, 'PROGRAMADA', NULL),
('b0000005-0000-0000-0000-000000000204', 'b0000004-0000-0000-0000-000000000202',
 'Informe de calidad hídrica del segundo trimestre', 1.00, 'Informes',
 '2026-07-01', '2026-07-31', 'Laura Mendoza Pérez',
 'c0000001-0000-0000-0000-000000000002',
 'Administrativa', 25.00, 'PROGRAMADA', NULL),
('b0000005-0000-0000-0000-000000000205', 'b0000004-0000-0000-0000-000000000202',
 'Informe de calidad hídrica del tercer trimestre', 1.00, 'Informes',
 '2026-10-01', '2026-10-31', 'Laura Mendoza Pérez',
 'c0000001-0000-0000-0000-000000000002',
 'Administrativa', 25.00, 'PROGRAMADA', NULL),

-- ===== PRY-03 — RECIÉN ABIERTO — todo a futuro, sin avance =====
('b0000005-0000-0000-0000-000000000301', 'b0000004-0000-0000-0000-000000000301',
 'Talleres en instituciones educativas de Turbo y Necoclí', 18.00, 'Capacitaciones',
 '2026-10-15', '2026-12-10', 'Marcela Úsuga Higuita',
 'c0000001-0000-0000-0000-000000000014',
 'Operativa', 50.00, 'PROGRAMADA', 'Inicia tras la firma del convenio con la Secretaría de Educación.'),
('b0000005-0000-0000-0000-000000000302', 'b0000004-0000-0000-0000-000000000301',
 'Talleres en instituciones educativas de Apartadó y Carepa', 18.00, 'Capacitaciones',
 '2026-10-20', '2026-12-15', 'Marcela Úsuga Higuita',
 'c0000001-0000-0000-0000-000000000014',
 'Operativa', 50.00, 'PROGRAMADA', NULL),
('b0000005-0000-0000-0000-000000000303', 'b0000004-0000-0000-0000-000000000302',
 'Conformación de grupos ecológicos escolares', 12.00, 'Comités',
 '2026-11-01', '2026-12-20', 'Tomás Gómez Restrepo',
 'c0000001-0000-0000-0000-000000000001',
 'Administrativa', 100.00, 'PROGRAMADA', NULL),

-- ===== PRY-04..08 — NORMALES — avance parcial =====
('b0000005-0000-0000-0000-000000000401', 'b0000004-0000-0000-0000-000000000401',
 'Evaluación técnica de solicitudes de concesión', 90.00, 'Actos Administrativos',
 '2026-01-15', '2026-11-30', 'Sebastián Durango Vélez',
 'c0000001-0000-0000-0000-000000000015',
 'Administrativa', 100.00, 'EN_EJECUCION', NULL),
('b0000005-0000-0000-0000-000000000402', 'b0000004-0000-0000-0000-000000000402',
 'Visitas de seguimiento a permisos vigentes', 75.00, 'Visitas',
 '2026-02-01', '2026-11-30', 'Sebastián Durango Vélez',
 'c0000001-0000-0000-0000-000000000015',
 'Operativa', 100.00, 'EN_EJECUCION', NULL),
('b0000005-0000-0000-0000-000000000501', 'b0000004-0000-0000-0000-000000000501',
 'Ejecución del plan de manejo del DRMI Ensenada de Rionegro', 15000.00, 'Hectáreas',
 '2026-01-15', '2026-12-15', 'Yuliana Mena Moreno',
 'c0000001-0000-0000-0000-000000000016',
 'Operativa', 100.00, 'EN_EJECUCION', NULL),
('b0000005-0000-0000-0000-000000000502', 'b0000004-0000-0000-0000-000000000502',
 'Recepción, valoración y disposición de fauna en el CAV', 240.00, 'Individuos',
 '2026-01-02', '2026-12-20', 'Yuliana Mena Moreno',
 'c0000001-0000-0000-0000-000000000016',
 'Operativa', 100.00, 'EN_EJECUCION', NULL),
('b0000005-0000-0000-0000-000000000601', 'b0000004-0000-0000-0000-000000000601',
 'Establecimiento de cerca viva en microcuencas priorizadas', 18.00, 'Kilómetros',
 '2026-02-15', '2026-08-31', 'Diana Palacios Rentería',
 'c0000001-0000-0000-0000-000000000012',
 'Operativa', 100.00, 'RETRASADA', 'Retraso por invierno prolongado. Pendiente reprogramar.'),
('b0000005-0000-0000-0000-000000000701', 'b0000004-0000-0000-0000-000000000701',
 'Operativos de control a vertimientos industriales', 60.00, 'Visitas',
 '2026-01-20', '2026-11-30', 'Sebastián Durango Vélez',
 'c0000001-0000-0000-0000-000000000015',
 'Operativa', 100.00, 'EN_EJECUCION', NULL),
('b0000005-0000-0000-0000-000000000801', 'b0000004-0000-0000-0000-000000000801',
 'Asistencia técnica a municipios en gestión del riesgo', 38.00, 'Acciones',
 '2026-01-15', '2026-12-10', 'Marcela Úsuga Higuita',
 'c0000001-0000-0000-0000-000000000014',
 'Operativa', 100.00, 'EN_EJECUCION', NULL);

-- Una reprogramación registrada, para que la trazabilidad tenga qué mostrar
INSERT INTO task_reschedules (task_id, previous_due_date, new_due_date, justification, changed_by_user_id)
VALUES ('b0000005-0000-0000-0000-000000000107', '2026-10-31', '2026-12-15',
        'Las jornadas de siembra se aplazaron por mareas altas atípicas en la bahía durante septiembre. Se reprograma el cierre para diciembre conservando la meta física.',
        'c0000001-0000-0000-0000-000000000001');

-- ---------------------------------------------------------------------------
-- 8. Avance mensual reportado
-- ---------------------------------------------------------------------------
-- Cada fila es el avance DEL MES, no el acumulado (regla del mapa de procesos).
-- Se generan con series para evitar cientos de INSERT escritos a mano.

-- 8.1 PRY-01: reportes completos de enero a septiembre.
--     El avance se reparte entre los meses de ejecución de cada tarea.
INSERT INTO follow_ups (task_id, reported_month, reported_year, physical_progress,
                        evidence_url, status, observations, reported_by)
SELECT
    t.id,
    m.mes,
    2026,
    ROUND((t.physical_goal_value * p.pct / 100.0)::numeric, 2),
    'https://drive.example.com/evidencias/2026/' || SUBSTRING(t.id::text, 1, 8) || '-' || LPAD(m.mes::text, 2, '0'),
    CASE WHEN m.mes = p.mes_fin AND t.status = 'COMPLETADA' THEN 'CUMPLIDA' ELSE 'EN_PROCESO' END,
    'Avance reportado por el técnico de campo. Evidencia fotográfica y planilla de registro.',
    t.responsible_user_id
FROM tasks t
JOIN (VALUES
    -- tarea                                     mes_ini mes_fin
    ('b0000005-0000-0000-0000-000000000101'::uuid, 1, 6),
    ('b0000005-0000-0000-0000-000000000102'::uuid, 2, 7),
    ('b0000005-0000-0000-0000-000000000103'::uuid, 4, 9),
    ('b0000005-0000-0000-0000-000000000104'::uuid, 1, 9),
    ('b0000005-0000-0000-0000-000000000105'::uuid, 3, 9),
    ('b0000005-0000-0000-0000-000000000106'::uuid, 1, 5),
    ('b0000005-0000-0000-0000-000000000107'::uuid, 3, 9),
    ('b0000005-0000-0000-0000-000000000108'::uuid, 1, 4),
    ('b0000005-0000-0000-0000-000000000109'::uuid, 5, 9)
) AS r(tid, mes_ini, mes_fin) ON r.tid = t.id
CROSS JOIN LATERAL generate_series(r.mes_ini, r.mes_fin) AS m(mes)
CROSS JOIN LATERAL (
    SELECT r.mes_fin AS mes_fin,
           -- Las tareas COMPLETADA reparten el 100%; las EN_EJECUCION, el 70%.
           CASE WHEN t.status = 'COMPLETADA' THEN 100.0 ELSE 70.0 END
           / (r.mes_fin - r.mes_ini + 1) AS pct
) AS p
ON CONFLICT (task_id, reported_year, reported_month) DO NOTHING;

-- 8.2 PRY-04..08: avance parcial, de enero a septiembre.
INSERT INTO follow_ups (task_id, reported_month, reported_year, physical_progress,
                        evidence_url, status, observations, reported_by)
SELECT
    t.id,
    m.mes,
    2026,
    ROUND((t.physical_goal_value * r.pct_total / 100.0 / 9.0)::numeric, 2),
    'https://drive.example.com/evidencias/2026/' || SUBSTRING(t.id::text, 1, 8) || '-' || LPAD(m.mes::text, 2, '0'),
    CASE WHEN t.status = 'RETRASADA' AND m.mes >= 7 THEN 'REZAGADA' ELSE 'EN_PROCESO' END,
    CASE WHEN t.status = 'RETRASADA' AND m.mes >= 7
         THEN 'Ejecución por debajo de lo programado. Condiciones climáticas adversas.'
         ELSE 'Avance reportado dentro de lo programado para el mes.' END,
    t.responsible_user_id
FROM tasks t
JOIN (VALUES
    ('b0000005-0000-0000-0000-000000000401'::uuid, 44.0),
    ('b0000005-0000-0000-0000-000000000402'::uuid, 38.0),
    ('b0000005-0000-0000-0000-000000000501'::uuid, 46.0),
    ('b0000005-0000-0000-0000-000000000502'::uuid, 40.0),
    ('b0000005-0000-0000-0000-000000000601'::uuid, 21.0),
    ('b0000005-0000-0000-0000-000000000701'::uuid, 35.0),
    ('b0000005-0000-0000-0000-000000000801'::uuid, 39.0)
) AS r(tid, pct_total) ON r.tid = t.id
CROSS JOIN LATERAL generate_series(1, 9) AS m(mes)
ON CONFLICT (task_id, reported_year, reported_month) DO NOTHING;

-- 8.3 PRY-02 (rezagado): solo reportó enero y febrero, luego nada.
--     Es el caso que debe encender las alertas.
INSERT INTO follow_ups (task_id, reported_month, reported_year, physical_progress,
                        evidence_url, status, observations, reported_by)
SELECT t.id, m.mes, 2026,
       ROUND((t.physical_goal_value * 0.07)::numeric, 2),
       'https://drive.example.com/evidencias/2026/' || SUBSTRING(t.id::text, 1, 8) || '-' || LPAD(m.mes::text, 2, '0'),
       'REZAGADA',
       'Monitoreo suspendido: el contrato con el laboratorio acreditado no se ha renovado.',
       t.responsible_user_id
FROM tasks t
CROSS JOIN LATERAL generate_series(1, 2) AS m(mes)
WHERE t.id = 'b0000005-0000-0000-0000-000000000201'
ON CONFLICT (task_id, reported_year, reported_month) DO NOTHING;

-- PRY-03 no tiene reportes: es el escenario recién abierto.

COMMIT;

-- ---------------------------------------------------------------------------
-- 9. Resumen de lo sembrado
-- ---------------------------------------------------------------------------
\echo ''
\echo '=============================================================='
\echo ' SISEPP - datos de demostración cargados'
\echo '=============================================================='

SELECT 'Líneas estratégicas' AS entidad, COUNT(*) AS total FROM strategic_lines WHERE id::text LIKE 'b0000001%'
UNION ALL SELECT 'Programas',   COUNT(*) FROM programs   WHERE id::text LIKE 'b0000002%'
UNION ALL SELECT 'Proyectos',   COUNT(*) FROM projects   WHERE id::text LIKE 'b0000003%'
UNION ALL SELECT 'Actividades', COUNT(*) FROM activities WHERE id::text LIKE 'b0000004%'
UNION ALL SELECT 'Tareas',      COUNT(*) FROM tasks      WHERE id::text LIKE 'b0000005%'
UNION ALL SELECT 'Reportes de avance', COUNT(*) FROM follow_ups f
    JOIN tasks t ON t.id = f.task_id WHERE t.id::text LIKE 'b0000005%';

\echo ''
\echo '--- Avance por proyecto (ponderado por peso de tarea y actividad) ---'

SELECT
    p.code AS proyecto,
    LEFT(p.name, 44) AS nombre,
    ROUND(SUM(
        LEAST(COALESCE(fu.reportado, 0) / NULLIF(t.physical_goal_value, 0), 1.0)
        * (COALESCE(t.weight, 0) / 100.0)
        * (COALESCE(a.weight, 0) / 100.0)
    ) * 100, 1) AS avance_pct,
    COUNT(*) FILTER (WHERE t.status = 'PROGRAMADA' AND t.due_date < CURRENT_DATE) AS vencidas
FROM projects p
JOIN activities a ON a.project_id = p.id
JOIN tasks t      ON t.activity_id = a.id
LEFT JOIN LATERAL (
    SELECT SUM(physical_progress) AS reportado FROM follow_ups WHERE task_id = t.id
) fu ON TRUE
WHERE p.id::text LIKE 'b0000003%'
GROUP BY p.code, p.name
ORDER BY p.code;

\echo ''
\echo '--- Equipo: coordinadores y sus proyectos ---'

SELECT u.full_name AS coordinador, u.email,
       COUNT(pc.project_id) AS proyectos,
       string_agg(p.code, ', ' ORDER BY p.code) AS cuales
FROM users u
JOIN project_coordinators pc ON pc.user_id = u.id
JOIN projects p ON p.id = pc.project_id
WHERE u.role = 'COORDINADOR'
GROUP BY u.full_name, u.email
ORDER BY u.full_name;

\echo ''
\echo '--- Equipo: tecnicos y sus tareas ---'

SELECT u.full_name AS tecnico, u.email, COUNT(t.id) AS tareas
FROM users u
LEFT JOIN tasks t ON t.responsible_user_id = u.id
WHERE u.role = 'TECNICO'
GROUP BY u.full_name, u.email
ORDER BY u.full_name;

\echo ''
\echo '--- Tareas vencidas (abiertas con fecha pasada) ---'

SELECT t.name AS tarea, t.due_date AS vencio, CURRENT_DATE - t.due_date AS dias, t.responsible_name AS responsable
FROM tasks t
WHERE t.id::text LIKE 'b0000005%' AND t.status = 'PROGRAMADA' AND t.due_date < CURRENT_DATE
ORDER BY t.due_date;
