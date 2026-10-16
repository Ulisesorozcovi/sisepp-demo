-- V26: Garantizar la existencia del usuario DIRECTOR y de un usuario STAFF.
--
-- Motivo: V16 solo ejecuta UPDATE sobre director@example.com, asumiendo
-- que la fila ya existe. En una base de datos creada desde cero (Flyway V1..V25)
-- ninguna migracion inserta al DIRECTOR, de modo que el rol con mayores
-- privilegios queda inexistente y no es posible crear la estructura del PAC.
--
-- Esta migracion es idempotente: si el usuario ya existe, reafirma su rol,
-- estado y contrasena conocida sin duplicar la fila.

-- 1. Usuario DIRECTOR
INSERT INTO users (full_name, email, password_hash, role, status)
VALUES (
    'Director Prueba',
    'director@example.com',
    crypt('${seed_password}', gen_salt('bf', 10)),
    'DIRECTOR',
    'ACTIVO'
)
ON CONFLICT (email) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    full_name     = EXCLUDED.full_name,
    role          = EXCLUDED.role,
    status        = EXCLUDED.status,
    updated_at    = now();

-- 2. Usuario STAFF (rol introducido en V22, sin usuario de prueba hasta ahora)
INSERT INTO users (full_name, email, password_hash, role, status)
VALUES (
    'Staff Prueba',
    'staff@example.com',
    crypt('${seed_password}', gen_salt('bf', 10)),
    'STAFF',
    'ACTIVO'
)
ON CONFLICT (email) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    full_name     = EXCLUDED.full_name,
    role          = EXCLUDED.role,
    status        = EXCLUDED.status,
    updated_at    = now();

-- 3. Desbloquear los usuarios de prueba por si quedaron con intentos fallidos
UPDATE users
SET failed_login_attempts = 0,
    locked_until          = NULL
WHERE email IN (
    'director@example.com',
    'staff@example.com',
    'coordinador@example.com',
    'tecnico@example.com'
);
