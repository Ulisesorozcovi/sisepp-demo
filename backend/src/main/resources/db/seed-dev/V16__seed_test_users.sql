-- V16: Actualizar contraseña del director y crear usuarios de prueba (TECNICO, COORDINADOR)

-- 1. Cambiar contraseña del director a: la definida en seed_password
UPDATE users
SET password_hash = crypt('${seed_password}', gen_salt('bf', 10)),
    updated_at    = now()
WHERE email = 'director@example.com';

-- 2. Crear usuario COORDINADOR de prueba
INSERT INTO users (full_name, email, password_hash, role, status)
VALUES (
    'Coordinador Prueba',
    'coordinador@example.com',
    crypt('${seed_password}', gen_salt('bf', 10)),
    'COORDINADOR',
    'ACTIVO'
)
ON CONFLICT (email) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    full_name     = EXCLUDED.full_name,
    role          = EXCLUDED.role,
    status        = EXCLUDED.status,
    updated_at    = now();

-- 3. Crear usuario TECNICO de prueba
INSERT INTO users (full_name, email, password_hash, role, status)
VALUES (
    'Técnico Prueba',
    'tecnico@example.com',
    crypt('${seed_password}', gen_salt('bf', 10)),
    'TECNICO',
    'ACTIVO'
)
ON CONFLICT (email) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    full_name     = EXCLUDED.full_name,
    role          = EXCLUDED.role,
    status        = EXCLUDED.status,
    updated_at    = now();
