-- =============================================================
-- V29: Semilla para el piloto de usabilidad
--
-- Crea 39 cuentas de prueba (+ 2 de ensayo), 8 proyectos con
-- actividades, tareas, asignaciones y avances previos.
--
-- Dominio de correo: @prueba.test (dominio reservado para pruebas)
-- Contrasena unica para todos: la definida en SISEPP_SEED_PASSWORD
-- =============================================================

-- ===================== 1. CUENTAS DE PRUEBA =====================

-- Password comun: placeholder seed_password (bcrypt via pgcrypto)
-- Se genera UNA vez y se reutiliza para las 41 cuentas.
DO $$
DECLARE
    pw_hash TEXT := crypt('${seed_password}', gen_salt('bf', 10));
BEGIN

-- 1.1 DIRECTOR (1)
INSERT INTO users (id, full_name, email, password_hash, role, status)
VALUES (
    'b0000000-0000-0000-0001-000000000001',
    'Director Piloto 01',
    'dir01@prueba.test',
    pw_hash, 'DIRECTOR', 'ACTIVO'
) ON CONFLICT (email) DO NOTHING;

-- 1.2 STAFF (3)
INSERT INTO users (id, full_name, email, password_hash, role, status) VALUES
    ('b0000000-0000-0000-0002-000000000001', 'Staff Piloto 01', 'stf01@prueba.test', pw_hash, 'STAFF', 'ACTIVO'),
    ('b0000000-0000-0000-0002-000000000002', 'Staff Piloto 02', 'stf02@prueba.test', pw_hash, 'STAFF', 'ACTIVO'),
    ('b0000000-0000-0000-0002-000000000003', 'Staff Piloto 03', 'stf03@prueba.test', pw_hash, 'STAFF', 'ACTIVO')
ON CONFLICT (email) DO NOTHING;

-- 1.3 COORDINADORES (25)
INSERT INTO users (id, full_name, email, password_hash, role, status) VALUES
    ('b0000000-0000-0000-0003-000000000001', 'Coordinador Piloto 01', 'coo01@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000002', 'Coordinador Piloto 02', 'coo02@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000003', 'Coordinador Piloto 03', 'coo03@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000004', 'Coordinador Piloto 04', 'coo04@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000005', 'Coordinador Piloto 05', 'coo05@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000006', 'Coordinador Piloto 06', 'coo06@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000007', 'Coordinador Piloto 07', 'coo07@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000008', 'Coordinador Piloto 08', 'coo08@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000009', 'Coordinador Piloto 09', 'coo09@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000010', 'Coordinador Piloto 10', 'coo10@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000011', 'Coordinador Piloto 11', 'coo11@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000012', 'Coordinador Piloto 12', 'coo12@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000013', 'Coordinador Piloto 13', 'coo13@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000014', 'Coordinador Piloto 14', 'coo14@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000015', 'Coordinador Piloto 15', 'coo15@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000016', 'Coordinador Piloto 16', 'coo16@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000017', 'Coordinador Piloto 17', 'coo17@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000018', 'Coordinador Piloto 18', 'coo18@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000019', 'Coordinador Piloto 19', 'coo19@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000020', 'Coordinador Piloto 20', 'coo20@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000021', 'Coordinador Piloto 21', 'coo21@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000022', 'Coordinador Piloto 22', 'coo22@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000023', 'Coordinador Piloto 23', 'coo23@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000024', 'Coordinador Piloto 24', 'coo24@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO'),
    ('b0000000-0000-0000-0003-000000000025', 'Coordinador Piloto 25', 'coo25@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO')
ON CONFLICT (email) DO NOTHING;

-- 1.4 TECNICOS (10)
INSERT INTO users (id, full_name, email, password_hash, role, status) VALUES
    ('b0000000-0000-0000-0004-000000000001', 'Tecnico Piloto 01', 'tec01@prueba.test', pw_hash, 'TECNICO', 'ACTIVO'),
    ('b0000000-0000-0000-0004-000000000002', 'Tecnico Piloto 02', 'tec02@prueba.test', pw_hash, 'TECNICO', 'ACTIVO'),
    ('b0000000-0000-0000-0004-000000000003', 'Tecnico Piloto 03', 'tec03@prueba.test', pw_hash, 'TECNICO', 'ACTIVO'),
    ('b0000000-0000-0000-0004-000000000004', 'Tecnico Piloto 04', 'tec04@prueba.test', pw_hash, 'TECNICO', 'ACTIVO'),
    ('b0000000-0000-0000-0004-000000000005', 'Tecnico Piloto 05', 'tec05@prueba.test', pw_hash, 'TECNICO', 'ACTIVO'),
    ('b0000000-0000-0000-0004-000000000006', 'Tecnico Piloto 06', 'tec06@prueba.test', pw_hash, 'TECNICO', 'ACTIVO'),
    ('b0000000-0000-0000-0004-000000000007', 'Tecnico Piloto 07', 'tec07@prueba.test', pw_hash, 'TECNICO', 'ACTIVO'),
    ('b0000000-0000-0000-0004-000000000008', 'Tecnico Piloto 08', 'tec08@prueba.test', pw_hash, 'TECNICO', 'ACTIVO'),
    ('b0000000-0000-0000-0004-000000000009', 'Tecnico Piloto 09', 'tec09@prueba.test', pw_hash, 'TECNICO', 'ACTIVO'),
    ('b0000000-0000-0000-0004-000000000010', 'Tecnico Piloto 10', 'tec10@prueba.test', pw_hash, 'TECNICO', 'ACTIVO')
ON CONFLICT (email) DO NOTHING;

-- 1.5 CUENTAS DE ENSAYO (no cuentan en la muestra)
INSERT INTO users (id, full_name, email, password_hash, role, status) VALUES
    ('b0000000-0000-0000-0009-000000000001', 'Ensayo Director', 'ensayo-dir@prueba.test', pw_hash, 'DIRECTOR', 'ACTIVO'),
    ('b0000000-0000-0000-0009-000000000002', 'Ensayo Coordinador', 'ensayo-coo@prueba.test', pw_hash, 'COORDINADOR', 'ACTIVO')
ON CONFLICT (email) DO NOTHING;

END $$;


-- ===================== 2. ESTRUCTURA PAC =====================
-- 3 lineas estrategicas -> 4 programas -> 8 proyectos
-- Todos vinculados al PAC vigente (V13)

-- IDs fijos del PAC (de V13)
-- PGAR: a0000001-0000-0000-0000-000000000001
-- PAC:  a0000001-0000-0000-0000-000000000002

-- 2.1 Lineas estrategicas
INSERT INTO strategic_lines (id, name, description, pgar_id) VALUES
    ('c0000000-0001-0000-0000-000000000001', 'Gestion Integral del Recurso Hidrico', 'Conservacion y manejo sostenible del agua', 'a0000001-0000-0000-0000-000000000001'),
    ('c0000000-0001-0000-0000-000000000002', 'Bosques, Biodiversidad y Servicios Ecosistemicos', 'Proteccion de ecosistemas estrategicos', 'a0000001-0000-0000-0000-000000000001'),
    ('c0000000-0001-0000-0000-000000000003', 'Gestion Ambiental Sectorial y Urbana', 'Regulacion ambiental y calidad del aire', 'a0000001-0000-0000-0000-000000000001')
ON CONFLICT DO NOTHING;

-- 2.2 Programas
INSERT INTO programs (id, name, strategic_line_id, code, weight, pac_id) VALUES
    ('c0000000-0002-0000-0000-000000000001', 'Ordenamiento del Recurso Hidrico', 'c0000000-0001-0000-0000-000000000001', 'P01', 30.00, 'a0000001-0000-0000-0000-000000000002'),
    ('c0000000-0002-0000-0000-000000000002', 'Calidad del Agua y Saneamiento', 'c0000000-0001-0000-0000-000000000001', 'P02', 20.00, 'a0000001-0000-0000-0000-000000000002'),
    ('c0000000-0002-0000-0000-000000000003', 'Conservacion de Ecosistemas', 'c0000000-0001-0000-0000-000000000002', 'P03', 30.00, 'a0000001-0000-0000-0000-000000000002'),
    ('c0000000-0002-0000-0000-000000000004', 'Control Ambiental', 'c0000000-0001-0000-0000-000000000003', 'P04', 20.00, 'a0000001-0000-0000-0000-000000000002')
ON CONFLICT DO NOTHING;

-- 2.3 Proyectos (8 total, vigencia 2026)
INSERT INTO projects (id, name, program_id, vigencia, code, weight, pac_id, start_year, end_year) VALUES
    ('c0000000-0003-0000-0000-000000000001', 'Monitoreo de cuencas hidrograficas', 'c0000000-0002-0000-0000-000000000001', 2026, 'PY01', 50.00, 'a0000001-0000-0000-0000-000000000002', 2024, 2027),
    ('c0000000-0003-0000-0000-000000000002', 'Reforestacion riberas Rio Leon', 'c0000000-0002-0000-0000-000000000001', 2026, 'PY02', 50.00, 'a0000001-0000-0000-0000-000000000002', 2024, 2027),
    ('c0000000-0003-0000-0000-000000000003', 'Control vertimientos industriales', 'c0000000-0002-0000-0000-000000000002', 2026, 'PY03', 50.00, 'a0000001-0000-0000-0000-000000000002', 2024, 2027),
    ('c0000000-0003-0000-0000-000000000004', 'Acueductos rurales zona norte', 'c0000000-0002-0000-0000-000000000002', 2026, 'PY04', 50.00, 'a0000001-0000-0000-0000-000000000002', 2024, 2027),
    ('c0000000-0003-0000-0000-000000000005', 'Restauracion manglar Turbo', 'c0000000-0002-0000-0000-000000000003', 2026, 'PY05', 35.00, 'a0000001-0000-0000-0000-000000000002', 2024, 2027),
    ('c0000000-0003-0000-0000-000000000006', 'Monitoreo fauna silvestre', 'c0000000-0002-0000-0000-000000000003', 2026, 'PY06', 35.00, 'a0000001-0000-0000-0000-000000000002', 2024, 2027),
    ('c0000000-0003-0000-0000-000000000007', 'Vivero forestal Apartado', 'c0000000-0002-0000-0000-000000000003', 2026, 'PY07', 30.00, 'a0000001-0000-0000-0000-000000000002', 2024, 2027),
    ('c0000000-0003-0000-0000-000000000008', 'Gestion residuos solidos Uraba', 'c0000000-0002-0000-0000-000000000004', 2026, 'PY08', 100.00, 'a0000001-0000-0000-0000-000000000002', 2024, 2027)
ON CONFLICT DO NOTHING;


-- ===================== 3. ASIGNAR COORDINADORES A PROYECTOS =====================
-- Cada coordinador tiene 1-2 proyectos. Maximo 3 coordinadores por proyecto.
-- COO 01-03 -> PY01, COO 04-06 -> PY02, COO 07-09 -> PY03, COO 10-12 -> PY04
-- COO 13-15 -> PY05, COO 16-18 -> PY06, COO 19-21 -> PY07, COO 22-25 -> PY08
-- Segundas asignaciones: COO 01 -> PY02, COO 07 -> PY04, COO 13 -> PY06, COO 19 -> PY08

INSERT INTO project_coordinators (project_id, user_id) VALUES
    -- PY01
    ('c0000000-0003-0000-0000-000000000001', 'b0000000-0000-0000-0003-000000000001'),
    ('c0000000-0003-0000-0000-000000000001', 'b0000000-0000-0000-0003-000000000002'),
    ('c0000000-0003-0000-0000-000000000001', 'b0000000-0000-0000-0003-000000000003'),
    -- PY02
    ('c0000000-0003-0000-0000-000000000002', 'b0000000-0000-0000-0003-000000000004'),
    ('c0000000-0003-0000-0000-000000000002', 'b0000000-0000-0000-0003-000000000005'),
    ('c0000000-0003-0000-0000-000000000002', 'b0000000-0000-0000-0003-000000000006'),
    ('c0000000-0003-0000-0000-000000000002', 'b0000000-0000-0000-0003-000000000001'),
    -- PY03
    ('c0000000-0003-0000-0000-000000000003', 'b0000000-0000-0000-0003-000000000007'),
    ('c0000000-0003-0000-0000-000000000003', 'b0000000-0000-0000-0003-000000000008'),
    ('c0000000-0003-0000-0000-000000000003', 'b0000000-0000-0000-0003-000000000009'),
    -- PY04
    ('c0000000-0003-0000-0000-000000000004', 'b0000000-0000-0000-0003-000000000010'),
    ('c0000000-0003-0000-0000-000000000004', 'b0000000-0000-0000-0003-000000000011'),
    ('c0000000-0003-0000-0000-000000000004', 'b0000000-0000-0000-0003-000000000012'),
    ('c0000000-0003-0000-0000-000000000004', 'b0000000-0000-0000-0003-000000000007'),
    -- PY05
    ('c0000000-0003-0000-0000-000000000005', 'b0000000-0000-0000-0003-000000000013'),
    ('c0000000-0003-0000-0000-000000000005', 'b0000000-0000-0000-0003-000000000014'),
    ('c0000000-0003-0000-0000-000000000005', 'b0000000-0000-0000-0003-000000000015'),
    -- PY06
    ('c0000000-0003-0000-0000-000000000006', 'b0000000-0000-0000-0003-000000000016'),
    ('c0000000-0003-0000-0000-000000000006', 'b0000000-0000-0000-0003-000000000017'),
    ('c0000000-0003-0000-0000-000000000006', 'b0000000-0000-0000-0003-000000000018'),
    ('c0000000-0003-0000-0000-000000000006', 'b0000000-0000-0000-0003-000000000013'),
    -- PY07
    ('c0000000-0003-0000-0000-000000000007', 'b0000000-0000-0000-0003-000000000019'),
    ('c0000000-0003-0000-0000-000000000007', 'b0000000-0000-0000-0003-000000000020'),
    ('c0000000-0003-0000-0000-000000000007', 'b0000000-0000-0000-0003-000000000021'),
    -- PY08
    ('c0000000-0003-0000-0000-000000000008', 'b0000000-0000-0000-0003-000000000022'),
    ('c0000000-0003-0000-0000-000000000008', 'b0000000-0000-0000-0003-000000000023'),
    ('c0000000-0003-0000-0000-000000000008', 'b0000000-0000-0000-0003-000000000024'),
    ('c0000000-0003-0000-0000-000000000008', 'b0000000-0000-0000-0003-000000000025'),
    ('c0000000-0003-0000-0000-000000000008', 'b0000000-0000-0000-0003-000000000019')
    -- Ensayo
,   ('c0000000-0003-0000-0000-000000000001', 'b0000000-0000-0000-0009-000000000002')
ON CONFLICT DO NOTHING;


-- ===================== 4. ACTIVIDADES (2 por proyecto = 16 total) =====================

INSERT INTO activities (id, name, project_id, code, measurement_unit, goal, weight) VALUES
    -- PY01
    ('c0000000-0004-0000-0000-000000000001', 'Realizar monitoreo de calidad de agua en cuencas prioritarias', 'c0000000-0003-0000-0000-000000000001', 'A01-01', 'Monitoreos', 12.00, 50.00),
    ('c0000000-0004-0000-0000-000000000002', 'Elaborar informes tecnicos de resultados de monitoreo', 'c0000000-0003-0000-0000-000000000001', 'A01-02', 'Informes', 4.00, 50.00),
    -- PY02
    ('c0000000-0004-0000-0000-000000000003', 'Establecer viveros para reforestacion de riberas', 'c0000000-0003-0000-0000-000000000002', 'A02-01', 'Hectareas', 50.00, 60.00),
    ('c0000000-0004-0000-0000-000000000004', 'Capacitar comunidades riberenas en conservacion', 'c0000000-0003-0000-0000-000000000002', 'A02-02', 'Talleres', 8.00, 40.00),
    -- PY03
    ('c0000000-0004-0000-0000-000000000005', 'Realizar visitas de inspeccion a vertimientos', 'c0000000-0003-0000-0000-000000000003', 'A03-01', 'Visitas', 24.00, 50.00),
    ('c0000000-0004-0000-0000-000000000006', 'Procesar permisos de vertimiento', 'c0000000-0003-0000-0000-000000000003', 'A03-02', 'Permisos', 15.00, 50.00),
    -- PY04
    ('c0000000-0004-0000-0000-000000000007', 'Diagnosticar estado de acueductos rurales', 'c0000000-0003-0000-0000-000000000004', 'A04-01', 'Diagnosticos', 10.00, 50.00),
    ('c0000000-0004-0000-0000-000000000008', 'Disenar mejoras para sistemas de abastecimiento', 'c0000000-0003-0000-0000-000000000004', 'A04-02', 'Disenos', 5.00, 50.00),
    -- PY05
    ('c0000000-0004-0000-0000-000000000009', 'Delimitar areas de manglar para restauracion', 'c0000000-0003-0000-0000-000000000005', 'A05-01', 'Hectareas', 30.00, 50.00),
    ('c0000000-0004-0000-0000-000000000010', 'Sembrar plantulas de mangle en zonas degradadas', 'c0000000-0003-0000-0000-000000000005', 'A05-02', 'Plantulas', 5000.00, 50.00),
    -- PY06
    ('c0000000-0004-0000-0000-000000000011', 'Realizar censos de fauna en areas protegidas', 'c0000000-0003-0000-0000-000000000006', 'A06-01', 'Censos', 6.00, 50.00),
    ('c0000000-0004-0000-0000-000000000012', 'Instalar camaras trampa en corredores biologicos', 'c0000000-0003-0000-0000-000000000006', 'A06-02', 'Camaras', 20.00, 50.00),
    -- PY07
    ('c0000000-0004-0000-0000-000000000013', 'Producir plantulas nativas en vivero', 'c0000000-0003-0000-0000-000000000007', 'A07-01', 'Plantulas', 10000.00, 60.00),
    ('c0000000-0004-0000-0000-000000000014', 'Entregar plantulas a comunidades', 'c0000000-0003-0000-0000-000000000007', 'A07-02', 'Entregas', 20.00, 40.00),
    -- PY08
    ('c0000000-0004-0000-0000-000000000015', 'Caracterizar puntos criticos de disposicion de residuos', 'c0000000-0003-0000-0000-000000000008', 'A08-01', 'Puntos', 40.00, 50.00),
    ('c0000000-0004-0000-0000-000000000016', 'Ejecutar jornadas de limpieza comunitaria', 'c0000000-0003-0000-0000-000000000008', 'A08-02', 'Jornadas', 12.00, 50.00)
ON CONFLICT DO NOTHING;


-- ===================== 5. TAREAS (3-4 por actividad, asignadas a tecnicos) =====================
-- Cada tecnico tiene al menos 2 tareas. Algunas con start_date y EN_EJECUCION.

INSERT INTO tasks (id, activity_id, name, physical_goal_value, physical_goal_unit, due_date, start_date, responsible_name, responsible_user_id, status, weight) VALUES
    -- Act A01-01 (Monitoreo cuencas) -> TEC 01, 02
    ('d0000000-0001-0000-0000-000000000001', 'c0000000-0004-0000-0000-000000000001', 'Muestreo cuenca Rio Turbo - trimestre 1', 3.00, 'Monitoreos', '2026-03-31', '2026-01-15', 'Tecnico Piloto 01', 'b0000000-0000-0000-0004-000000000001', 'COMPLETADA', 25.00),
    ('d0000000-0001-0000-0000-000000000002', 'c0000000-0004-0000-0000-000000000001', 'Muestreo cuenca Rio Turbo - trimestre 2', 3.00, 'Monitoreos', '2026-06-30', '2026-04-01', 'Tecnico Piloto 01', 'b0000000-0000-0000-0004-000000000001', 'EN_EJECUCION', 25.00),
    ('d0000000-0001-0000-0000-000000000003', 'c0000000-0004-0000-0000-000000000001', 'Muestreo cuenca Rio Leon - trimestre 1', 3.00, 'Monitoreos', '2026-03-31', '2026-01-15', 'Tecnico Piloto 02', 'b0000000-0000-0000-0004-000000000002', 'COMPLETADA', 25.00),
    ('d0000000-0001-0000-0000-000000000004', 'c0000000-0004-0000-0000-000000000001', 'Muestreo cuenca Rio Leon - trimestre 2', 3.00, 'Monitoreos', '2026-06-30', '2026-04-01', 'Tecnico Piloto 02', 'b0000000-0000-0000-0004-000000000002', 'PROGRAMADA', 25.00),

    -- Act A02-01 (Viveros reforestacion) -> TEC 03, 04
    ('d0000000-0001-0000-0000-000000000005', 'c0000000-0004-0000-0000-000000000003', 'Preparar terreno sector norte', 15.00, 'Hectareas', '2026-04-30', '2026-02-01', 'Tecnico Piloto 03', 'b0000000-0000-0000-0004-000000000003', 'EN_EJECUCION', 30.00),
    ('d0000000-0001-0000-0000-000000000006', 'c0000000-0004-0000-0000-000000000003', 'Sembrar plantulas sector norte', 20.00, 'Hectareas', '2026-07-31', '2026-05-01', 'Tecnico Piloto 03', 'b0000000-0000-0000-0004-000000000003', 'PROGRAMADA', 40.00),
    ('d0000000-0001-0000-0000-000000000007', 'c0000000-0004-0000-0000-000000000003', 'Preparar terreno sector sur', 15.00, 'Hectareas', '2026-05-31', '2026-03-01', 'Tecnico Piloto 04', 'b0000000-0000-0000-0004-000000000004', 'EN_EJECUCION', 30.00),

    -- Act A03-01 (Visitas vertimientos) -> TEC 05, 06
    ('d0000000-0001-0000-0000-000000000008', 'c0000000-0004-0000-0000-000000000005', 'Inspecciones zona industrial Apartado', 8.00, 'Visitas', '2026-04-30', '2026-01-15', 'Tecnico Piloto 05', 'b0000000-0000-0000-0004-000000000005', 'EN_EJECUCION', 33.00),
    ('d0000000-0001-0000-0000-000000000009', 'c0000000-0004-0000-0000-000000000005', 'Inspecciones zona industrial Turbo', 8.00, 'Visitas', '2026-06-30', '2026-04-01', 'Tecnico Piloto 05', 'b0000000-0000-0000-0004-000000000005', 'PROGRAMADA', 33.00),
    ('d0000000-0001-0000-0000-000000000010', 'c0000000-0004-0000-0000-000000000005', 'Inspecciones zona bananera Carepa', 8.00, 'Visitas', '2026-08-31', '2026-06-01', 'Tecnico Piloto 06', 'b0000000-0000-0000-0004-000000000006', 'PROGRAMADA', 34.00),

    -- Act A05-02 (Siembra mangle) -> TEC 07, 08
    ('d0000000-0001-0000-0000-000000000011', 'c0000000-0004-0000-0000-000000000010', 'Siembra sector bahia Turbo fase 1', 2000.00, 'Plantulas', '2026-05-31', '2026-02-15', 'Tecnico Piloto 07', 'b0000000-0000-0000-0004-000000000007', 'EN_EJECUCION', 40.00),
    ('d0000000-0001-0000-0000-000000000012', 'c0000000-0004-0000-0000-000000000010', 'Siembra sector bahia Turbo fase 2', 2000.00, 'Plantulas', '2026-09-30', '2026-06-01', 'Tecnico Piloto 07', 'b0000000-0000-0000-0004-000000000007', 'PROGRAMADA', 40.00),
    ('d0000000-0001-0000-0000-000000000013', 'c0000000-0004-0000-0000-000000000010', 'Siembra sector Necocli', 1000.00, 'Plantulas', '2026-07-31', '2026-04-01', 'Tecnico Piloto 08', 'b0000000-0000-0000-0004-000000000008', 'PROGRAMADA', 20.00),

    -- Act A07-01 (Produccion vivero) -> TEC 09, 10
    ('d0000000-0001-0000-0000-000000000014', 'c0000000-0004-0000-0000-000000000013', 'Produccion lote enero-junio', 5000.00, 'Plantulas', '2026-06-30', '2026-01-10', 'Tecnico Piloto 09', 'b0000000-0000-0000-0004-000000000009', 'EN_EJECUCION', 50.00),
    ('d0000000-0001-0000-0000-000000000015', 'c0000000-0004-0000-0000-000000000013', 'Produccion lote julio-diciembre', 5000.00, 'Plantulas', '2026-12-31', '2026-07-01', 'Tecnico Piloto 09', 'b0000000-0000-0000-0004-000000000009', 'PROGRAMADA', 50.00),
    ('d0000000-0001-0000-0000-000000000016', 'c0000000-0004-0000-0000-000000000014', 'Entregar plantulas a juntas veredales', 10.00, 'Entregas', '2026-06-30', '2026-03-01', 'Tecnico Piloto 10', 'b0000000-0000-0000-0004-000000000010', 'EN_EJECUCION', 50.00),
    ('d0000000-0001-0000-0000-000000000017', 'c0000000-0004-0000-0000-000000000014', 'Entregar plantulas a colegios rurales', 10.00, 'Entregas', '2026-11-30', '2026-07-01', 'Tecnico Piloto 10', 'b0000000-0000-0000-0004-000000000010', 'PROGRAMADA', 50.00),

    -- Act A08-02 (Jornadas limpieza) -> TEC 01 (segunda tarea)
    ('d0000000-0001-0000-0000-000000000018', 'c0000000-0004-0000-0000-000000000016', 'Jornada limpieza playa Turbo', 4.00, 'Jornadas', '2026-04-30', '2026-02-01', 'Tecnico Piloto 01', 'b0000000-0000-0000-0004-000000000001', 'EN_EJECUCION', 33.00),

    -- Tarea para reprogramar (mision C4 del coordinador): vence pronto
    ('d0000000-0001-0000-0000-000000000019', 'c0000000-0004-0000-0000-000000000007', 'Diagnostico acueducto vereda El Tres', 2.00, 'Diagnosticos', '2026-11-15', '2026-09-01', 'Tecnico Piloto 06', 'b0000000-0000-0000-0004-000000000006', 'PROGRAMADA', 20.00),
    ('d0000000-0001-0000-0000-000000000020', 'c0000000-0004-0000-0000-000000000011', 'Censo fauna Parque Paramillo sector 1', 2.00, 'Censos', '2026-11-30', '2026-09-15', 'Tecnico Piloto 08', 'b0000000-0000-0000-0004-000000000008', 'PROGRAMADA', 33.00)
ON CONFLICT DO NOTHING;

-- Marcar completed_at para tareas COMPLETADA
UPDATE tasks SET completed_at = '2026-03-28 16:00:00'
WHERE id IN ('d0000000-0001-0000-0000-000000000001', 'd0000000-0001-0000-0000-000000000003');


-- ===================== 6. AVANCES (follow_ups) =====================
-- Cada tecnico debe tener al menos un avance registrado.
-- Algunos con status CUMPLIDA (tareas completadas), otros EN_PROCESO.

INSERT INTO follow_ups (id, task_id, reported_month, reported_year, physical_progress, status, observations, reported_by) VALUES
    -- TEC 01: tarea completada (3/3 monitoreos)
    ('e0000000-0001-0000-0000-000000000001', 'd0000000-0001-0000-0000-000000000001', 1, 2026, 1.00, 'EN_PROCESO', 'Primer muestreo Rio Turbo realizado', 'b0000000-0000-0000-0004-000000000001'),
    ('e0000000-0001-0000-0000-000000000002', 'd0000000-0001-0000-0000-000000000001', 2, 2026, 1.00, 'EN_PROCESO', 'Segundo muestreo completado', 'b0000000-0000-0000-0004-000000000001'),
    ('e0000000-0001-0000-0000-000000000003', 'd0000000-0001-0000-0000-000000000001', 3, 2026, 1.00, 'CUMPLIDA', 'Tercer muestreo, meta cumplida', 'b0000000-0000-0000-0004-000000000001'),
    -- TEC 01: tarea en ejecucion (1/3 monitoreos T2)
    ('e0000000-0001-0000-0000-000000000004', 'd0000000-0001-0000-0000-000000000002', 4, 2026, 1.00, 'EN_PROCESO', 'Primer muestreo trimestre 2', 'b0000000-0000-0000-0004-000000000001'),
    -- TEC 01: jornada limpieza (1/4)
    ('e0000000-0001-0000-0000-000000000005', 'd0000000-0001-0000-0000-000000000018', 2, 2026, 1.00, 'EN_PROCESO', 'Primera jornada realizada en playa norte', 'b0000000-0000-0000-0004-000000000001'),

    -- TEC 02: tarea completada
    ('e0000000-0001-0000-0000-000000000006', 'd0000000-0001-0000-0000-000000000003', 1, 2026, 1.00, 'EN_PROCESO', 'Muestreo Rio Leon enero', 'b0000000-0000-0000-0004-000000000002'),
    ('e0000000-0001-0000-0000-000000000007', 'd0000000-0001-0000-0000-000000000003', 2, 2026, 1.00, 'EN_PROCESO', 'Muestreo Rio Leon febrero', 'b0000000-0000-0000-0004-000000000002'),
    ('e0000000-0001-0000-0000-000000000008', 'd0000000-0001-0000-0000-000000000003', 3, 2026, 1.00, 'CUMPLIDA', 'Meta de monitoreos cumplida', 'b0000000-0000-0000-0004-000000000002'),

    -- TEC 03: en ejecucion (5/15 hectareas)
    ('e0000000-0001-0000-0000-000000000009', 'd0000000-0001-0000-0000-000000000005', 2, 2026, 3.00, 'EN_PROCESO', 'Preparacion terreno zona A', 'b0000000-0000-0000-0004-000000000003'),
    ('e0000000-0001-0000-0000-000000000010', 'd0000000-0001-0000-0000-000000000005', 3, 2026, 2.00, 'EN_PROCESO', 'Preparacion terreno zona B', 'b0000000-0000-0000-0004-000000000003'),

    -- TEC 04: en ejecucion (4/15 hectareas)
    ('e0000000-0001-0000-0000-000000000011', 'd0000000-0001-0000-0000-000000000007', 3, 2026, 4.00, 'EN_PROCESO', 'Terreno sector sur avance parcial', 'b0000000-0000-0000-0004-000000000004'),

    -- TEC 05: en ejecucion (3/8 visitas)
    ('e0000000-0001-0000-0000-000000000012', 'd0000000-0001-0000-0000-000000000008', 1, 2026, 1.00, 'EN_PROCESO', 'Visita Empresa Bananera SA', 'b0000000-0000-0000-0004-000000000005'),
    ('e0000000-0001-0000-0000-000000000013', 'd0000000-0001-0000-0000-000000000008', 2, 2026, 1.00, 'EN_PROCESO', 'Visita Procesadora del Uraba', 'b0000000-0000-0000-0004-000000000005'),
    ('e0000000-0001-0000-0000-000000000014', 'd0000000-0001-0000-0000-000000000008', 3, 2026, 1.00, 'EN_PROCESO', 'Visita Empacadora del Norte', 'b0000000-0000-0000-0004-000000000005'),

    -- TEC 07: en ejecucion (800/2000 plantulas)
    ('e0000000-0001-0000-0000-000000000015', 'd0000000-0001-0000-0000-000000000011', 2, 2026, 300.00, 'EN_PROCESO', 'Primera siembra bahia norte', 'b0000000-0000-0000-0004-000000000007'),
    ('e0000000-0001-0000-0000-000000000016', 'd0000000-0001-0000-0000-000000000011', 3, 2026, 500.00, 'EN_PROCESO', 'Segunda siembra bahia sur', 'b0000000-0000-0000-0004-000000000007'),

    -- TEC 09: en ejecucion (1500/5000 plantulas vivero)
    ('e0000000-0001-0000-0000-000000000017', 'd0000000-0001-0000-0000-000000000014', 1, 2026, 500.00, 'EN_PROCESO', 'Primer lote germinado', 'b0000000-0000-0000-0004-000000000009'),
    ('e0000000-0001-0000-0000-000000000018', 'd0000000-0001-0000-0000-000000000014', 2, 2026, 500.00, 'EN_PROCESO', 'Segundo lote en crecimiento', 'b0000000-0000-0000-0004-000000000009'),
    ('e0000000-0001-0000-0000-000000000019', 'd0000000-0001-0000-0000-000000000014', 3, 2026, 500.00, 'EN_PROCESO', 'Tercer lote trasplantado', 'b0000000-0000-0000-0004-000000000009'),

    -- TEC 10: en ejecucion (3/10 entregas)
    ('e0000000-0001-0000-0000-000000000020', 'd0000000-0001-0000-0000-000000000016', 3, 2026, 1.00, 'EN_PROCESO', 'Entrega vereda El Carmen', 'b0000000-0000-0000-0004-000000000010'),
    ('e0000000-0001-0000-0000-000000000021', 'd0000000-0001-0000-0000-000000000016', 4, 2026, 1.00, 'EN_PROCESO', 'Entrega vereda La Union', 'b0000000-0000-0000-0004-000000000010'),
    ('e0000000-0001-0000-0000-000000000022', 'd0000000-0001-0000-0000-000000000016', 5, 2026, 1.00, 'EN_PROCESO', 'Entrega vereda San Jose', 'b0000000-0000-0000-0004-000000000010')
ON CONFLICT DO NOTHING;

-- Desbloquear todas las cuentas del piloto por si quedaron con intentos fallidos
UPDATE users
SET failed_login_attempts = 0,
    locked_until = NULL
WHERE email LIKE '%@prueba.test';
