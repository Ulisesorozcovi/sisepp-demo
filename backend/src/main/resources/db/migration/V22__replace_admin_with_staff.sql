-- Replace ADMINISTRADOR role with STAFF
UPDATE users SET role = 'STAFF' WHERE role = 'ADMINISTRADOR';

-- Update CHECK constraint to reflect new role
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users
    ADD CONSTRAINT users_role_check
        CHECK (role IN ('DIRECTOR', 'COORDINADOR', 'STAFF', 'TECNICO'));
