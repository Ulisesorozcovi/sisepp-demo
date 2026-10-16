-- =============================================================
-- V13: Datos semilla de instrumentos de planificación vigentes
-- PGAR → PAC → POAI
-- =============================================================

-- PGAR vigente
INSERT INTO planning_instruments (id, type, name, start_year, end_year, parent_id, approved_date, approval_ref, status)
VALUES (
    'a0000001-0000-0000-0000-000000000001',
    'PGAR',
    'PGAR 2026-2037',
    2026, 2037,
    NULL,
    NULL,
    NULL,
    'ACTIVO'
);

-- PAC vigente (hijo del PGAR)
INSERT INTO planning_instruments (id, type, name, start_year, end_year, parent_id, approved_date, approval_ref, status)
VALUES (
    'a0000001-0000-0000-0000-000000000002',
    'PAC',
    'Plan de Acción 2024-2027 (demo)',
    2024, 2027,
    'a0000001-0000-0000-0000-000000000001',
    '2024-06-05',
    'Acuerdo de ejemplo 001 de 2024',
    'ACTIVO'
);

-- POAI 2026 (hijo del PAC)
INSERT INTO planning_instruments (id, type, name, start_year, end_year, parent_id, approved_date, approval_ref, status)
VALUES (
    'a0000001-0000-0000-0000-000000000003',
    'POAI',
    'Plan Operativo Anual de Inversión 2026',
    2026, 2026,
    'a0000001-0000-0000-0000-000000000002',
    NULL,
    'Acuerdo 012 de 2025',
    'ACTIVO'
);
