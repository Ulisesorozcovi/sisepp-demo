-- V14: Agregar rol TECNICO al CHECK de users.role
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users
    ADD CONSTRAINT users_role_check
        CHECK (role IN ('DIRECTOR', 'COORDINADOR', 'ADMINISTRADOR', 'TECNICO'));
