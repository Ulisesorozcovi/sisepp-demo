-- Unidades de medida (46 registros del Excel)
CREATE TABLE measurement_units (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        INT NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL UNIQUE,
    status      VARCHAR(10) NOT NULL DEFAULT 'ACTIVO'
                    CHECK (status IN ('ACTIVO', 'INACTIVO')),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

-- Programas de Inversión Pública - PIP (catálogo del DNP)
CREATE TABLE public_investment_programs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(20) NOT NULL UNIQUE,
    name        VARCHAR(300) NOT NULL,
    sector      VARCHAR(100) DEFAULT 'Ambiente',
    source_url  VARCHAR(500),
    status      VARCHAR(10) NOT NULL DEFAULT 'ACTIVO'
                    CHECK (status IN ('ACTIVO', 'INACTIVO')),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

-- Categorías temáticas
CREATE TABLE thematic_categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(200) NOT NULL UNIQUE,
    status      VARCHAR(10) NOT NULL DEFAULT 'ACTIVO'
                    CHECK (status IN ('ACTIVO', 'INACTIVO')),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

-- Indicadores mínimos de gestión (Resolución 667 de 2016)
CREATE TABLE management_indicators (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(20),
    name            VARCHAR(500) NOT NULL,
    regulation_ref  VARCHAR(100) DEFAULT 'Resolución 667 de 2016',
    indicator_type  VARCHAR(30) DEFAULT 'IMG'
                        CHECK (indicator_type IN ('IMG', 'COMPLEMENTARIO', 'PROPIO')),
    status          VARCHAR(10) NOT NULL DEFAULT 'ACTIVO'
                        CHECK (status IN ('ACTIVO', 'INACTIVO')),
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);
