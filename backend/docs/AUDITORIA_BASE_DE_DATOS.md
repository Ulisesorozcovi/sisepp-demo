# Auditoría de Base de Datos — SPC Backend (sisepp-backend)

**Fecha:** 2026-09-28
**Auditor:** Claude Opus 4.6
**Migraciones auditadas:** V1 a V4
**Última migración:** `V4__create_tasks.sql`

---

## Parte 1: Inventario de BD actual

### Tabla: `users` (V1)

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `id` | UUID | NO | `gen_random_uuid()` | **PK** |
| `full_name` | VARCHAR(150) | NO | — | — |
| `email` | VARCHAR(150) | NO | — | **UNIQUE** |
| `password_hash` | VARCHAR(255) | NO | — | — |
| `role` | VARCHAR(20) | NO | — | CHECK `IN ('DIRECTOR', 'COORDINADOR')` |
| `status` | VARCHAR(20) | NO | `'ACTIVO'` | CHECK `IN ('ACTIVO', 'INACTIVO')` |
| `failed_login_attempts` | INT | NO | `0` | — |
| `locked_until` | TIMESTAMP | SÍ | — | — |
| `last_login_at` | TIMESTAMP | SÍ | — | — |
| `created_at` | TIMESTAMP | NO | `now()` | — |
| `updated_at` | TIMESTAMP | NO | `now()` | — |

**Índices:** `idx_users_role (role)`, `idx_users_status (status)`
**JPA match:** `User.java` ✅ — campos coinciden

---

### Tabla: `password_reset_tokens` (V1)

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `id` | UUID | NO | `gen_random_uuid()` | **PK** |
| `user_id` | UUID | NO | — | **FK → users(id) ON DELETE CASCADE** |
| `token` | VARCHAR(255) | NO | — | **UNIQUE** |
| `expires_at` | TIMESTAMP | NO | — | — |
| `used` | BOOLEAN | NO | `FALSE` | — |
| `created_at` | TIMESTAMP | NO | `now()` | — |

**Índices:** `idx_password_reset_tokens_user_id (user_id)`
**JPA match:** `PasswordResetToken.java` ✅ — campos coinciden

---

### Tabla: `strategic_lines` (V2)

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `id` | UUID | NO | `gen_random_uuid()` | **PK** |
| `name` | VARCHAR(200) | NO | — | **UNIQUE** |
| `status` | VARCHAR(20) | NO | `'ACTIVO'` | CHECK `IN ('ACTIVO','INACTIVO')` |
| `created_at` | TIMESTAMP | NO | `now()` | — |
| `updated_at` | TIMESTAMP | NO | `now()` | — |

**Índices:** Ninguno adicional (PK + UNIQUE en name)
**JPA match:** `StrategicLine.java` ✅ — campos coinciden

---

### Tabla: `programs` (V2)

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `id` | UUID | NO | `gen_random_uuid()` | **PK** |
| `name` | VARCHAR(200) | NO | — | — |
| `strategic_line_id` | UUID | NO | — | **FK → strategic_lines(id) ON DELETE RESTRICT** |
| `status` | VARCHAR(20) | NO | `'ACTIVO'` | CHECK `IN ('ACTIVO','INACTIVO')` |
| `created_at` | TIMESTAMP | NO | `now()` | — |
| `updated_at` | TIMESTAMP | NO | `now()` | — |

**Unique constraint:** `(strategic_line_id, name)` — nombre único por línea estratégica
**Índices:** `idx_programs_strategic_line (strategic_line_id)`
**JPA match:** `Program.java` ✅ — campos coinciden

---

### Tabla: `projects` (V2)

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `id` | UUID | NO | `gen_random_uuid()` | **PK** |
| `name` | VARCHAR(200) | NO | — | — |
| `program_id` | UUID | NO | — | **FK → programs(id) ON DELETE RESTRICT** |
| `vigencia` | INT | NO | — | — |
| `status` | VARCHAR(20) | NO | `'ACTIVO'` | CHECK `IN ('ACTIVO','INACTIVO')` |
| `created_at` | TIMESTAMP | NO | `now()` | — |
| `updated_at` | TIMESTAMP | NO | `now()` | — |

**Índices:** `idx_projects_program (program_id)`
**JPA match:** `Project.java` ✅ — campos coinciden

---

### Tabla: `project_coordinators` (V2) — Tabla intermedia N:M

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `project_id` | UUID | NO | — | **FK → projects(id) ON DELETE CASCADE** |
| `user_id` | UUID | NO | — | **FK → users(id) ON DELETE CASCADE** |

**PK compuesta:** `(project_id, user_id)`
**JPA match:** Definida como `@ManyToMany` en `Project.java` vía `@JoinTable` ✅

---

### Tabla: `sdg_indicators` (V2 + V3 seed)

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `id` | INT | NO | — | **PK** (manual, 1-17) |
| `code` | VARCHAR(10) | NO | — | **UNIQUE** |
| `name` | VARCHAR(150) | NO | — | — |

**Datos seed (V3):** 17 registros (ODS 1 a ODS 17)
**JPA match:** `SdgIndicator.java` ✅ — campos coinciden

---

### Tabla: `activities` (V2)

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `id` | UUID | NO | `gen_random_uuid()` | **PK** |
| `name` | VARCHAR(200) | NO | — | — |
| `project_id` | UUID | NO | — | **FK → projects(id) ON DELETE RESTRICT** |
| `status` | VARCHAR(20) | NO | `'ACTIVO'` | CHECK `IN ('ACTIVO','INACTIVO')` |
| `created_at` | TIMESTAMP | NO | `now()` | — |
| `updated_at` | TIMESTAMP | NO | `now()` | — |

**Índices:** `idx_activities_project (project_id)`
**JPA match:** `Activity.java` ✅ — campos coinciden

---

### Tabla: `activity_sdg_indicators` (V2) — Tabla intermedia N:M

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `activity_id` | UUID | NO | — | **FK → activities(id) ON DELETE CASCADE** |
| `sdg_indicator_id` | INT | NO | — | **FK → sdg_indicators(id) ON DELETE RESTRICT** |

**PK compuesta:** `(activity_id, sdg_indicator_id)`
**JPA match:** Definida como `@ManyToMany` en `Activity.java` vía `@JoinTable` ✅

---

### Tabla: `tasks` (V4)

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `id` | UUID | NO | `gen_random_uuid()` | **PK** |
| `activity_id` | UUID | NO | — | **FK → activities(id) ON DELETE RESTRICT** |
| `name` | VARCHAR(200) | NO | — | — |
| `physical_goal_value` | NUMERIC(12,2) | NO | — | — |
| `physical_goal_unit` | VARCHAR(50) | NO | — | — |
| `due_date` | DATE | NO | — | — |
| `responsible_name` | VARCHAR(150) | NO | — | — |
| `responsible_user_id` | UUID | SÍ | — | **FK → users(id) ON DELETE SET NULL** |
| `status` | VARCHAR(20) | NO | `'PROGRAMADA'` | CHECK `IN ('PROGRAMADA','EN_EJECUCION','RETRASADA','COMPLETADA')` |
| `created_at` | TIMESTAMP | NO | `now()` | — |
| `updated_at` | TIMESTAMP | NO | `now()` | — |

**Índices:** `idx_tasks_activity (activity_id)`, `idx_tasks_due_date (due_date)`
**JPA match:** `Task.java` ✅ — campos coinciden

---

### Tabla: `task_reschedules` (V4)

| Columna | Tipo | Nullable | Default | Constraint |
|---------|------|----------|---------|------------|
| `id` | UUID | NO | `gen_random_uuid()` | **PK** |
| `task_id` | UUID | NO | — | **FK → tasks(id) ON DELETE CASCADE** |
| `previous_due_date` | DATE | NO | — | — |
| `new_due_date` | DATE | NO | — | — |
| `justification` | VARCHAR(500) | NO | — | — |
| `changed_by_user_id` | UUID | SÍ | — | **FK → users(id) ON DELETE SET NULL** |
| `changed_at` | TIMESTAMP | NO | `now()` | — |

**Índices:** `idx_task_reschedules_task (task_id)`
**JPA match:** `TaskReschedule.java` ✅ — campos coinciden

---

## Parte 2: Análisis de Brechas (Gap Analysis)

### Campos faltantes en tablas existentes

| Campo/Tabla | En Sheets | En BD actual | Estado | Acción requerida |
|-------------|-----------|-------------|--------|-----------------|
| `strategic_lines.description` | ✅ Descripción | ❌ No existe | ❌ FALTA | Agregar columna TEXT nullable |
| `programs.code` | ✅ ID_Programa "3201" | ❌ No existe | ❌ FALTA | Agregar VARCHAR UNIQUE NOT NULL |
| `programs.weight` | ✅ Ponderación | ❌ No existe | ❌ FALTA | Agregar DECIMAL nullable |
| `programs.description` | ✅ (implícito) | ❌ No existe | ❌ FALTA | Agregar TEXT nullable |
| `projects.code` | ✅ ID_Proyecto "320109001" | ❌ No existe | ❌ FALTA | Agregar VARCHAR UNIQUE NOT NULL |
| `projects.subdivision` | ✅ SUBDIRECCIÓN | ❌ No existe | ❌ FALTA | Agregar VARCHAR nullable |
| `projects.weight` | ✅ Ponderación | ❌ No existe | ❌ FALTA | Agregar DECIMAL nullable |
| `project_coordinators.role_in_project` | ✅ Rol (Líder/Directivo/Admin) | ❌ No existe | ❌ FALTA | Agregar VARCHAR con CHECK |
| `activities.code` | ✅ ID_Actividad "32010900101" | ❌ No existe | ❌ FALTA | Agregar VARCHAR NOT NULL |
| `activities.measurement_unit` | ✅ Unidad de medida | ❌ No existe | ❌ FALTA | Agregar VARCHAR nullable |
| `activities.goal` | ✅ Meta física | ❌ No existe | ❌ FALTA | Agregar DECIMAL nullable |
| `activities.weight` | ✅ Ponderación | ❌ No existe | ❌ FALTA | Agregar DECIMAL nullable |
| `sdg_indicators.description` | ✅ Descripción | ❌ No existe | ❌ FALTA | Agregar TEXT nullable |
| `tasks.task_type` | ✅ Tipo de tarea | ❌ No existe | ❌ FALTA | Agregar VARCHAR nullable |
| `tasks.weight` | ✅ Peso | ❌ No existe | ❌ FALTA | Agregar DECIMAL nullable |
| `tasks.observation` | ✅ Observaciones | ❌ No existe | ❌ FALTA | Agregar TEXT nullable |
| `tasks.start_date` | ✅ Fecha_Inicio | ❌ No existe | ❌ FALTA | Agregar DATE nullable |
| `users.role` CHECK | ✅ ADMINISTRADOR | ❌ Solo DIRECTOR, COORDINADOR | ❌ FALTA | Ampliar CHECK constraint |

### Tablas completamente faltantes

| Tabla | En Sheets | En BD actual | Estado | Acción requerida |
|-------|-----------|-------------|--------|-----------------|
| `task_schedules` | ✅ Hoja "Programación tareas" | ❌ No existe | ❌ FALTA | Crear tabla completa |
| `follow_ups` | ✅ ~969 registros de seguimiento | ❌ No existe | ❌ FALTA | Crear tabla completa |
| `financial_executions` | ✅ ~879×3 registros financieros | ❌ No existe | ❌ FALTA | Crear tabla completa |

### Campos que existen en BD pero NO en el modelo objetivo

| Campo | En BD | En Sheets | Observación |
|-------|-------|-----------|-------------|
| `tasks.responsible_name` | ✅ VARCHAR(150) | ⚠️ Parcial | Sheets tiene responsable como texto libre + FK. BD tiene ambos — OK |
| `tasks.physical_goal_unit` | ✅ VARCHAR(50) | ⚠️ Similar a `measurement_unit` | Puede ser redundante si activities ya tiene `measurement_unit` |
| Todas las tablas: `status` campo | ✅ ACTIVO/INACTIVO | ⚠️ No todas en Sheets | Soft-delete propio del sistema — correcto mantener |

---

## Parte 3: Migraciones Flyway sugeridas

### V5 — Agregar campos faltantes a tablas del PAC

```sql
-- V5__add_pac_business_fields.sql
-- Agrega códigos PAC, ponderaciones, subdirección y campos de negocio
-- faltantes según modelo de datos de producción (Google Sheets)

-- strategic_lines
ALTER TABLE strategic_lines
    ADD COLUMN description TEXT;

-- programs
ALTER TABLE programs
    ADD COLUMN code VARCHAR(20),
    ADD COLUMN weight NUMERIC(5,2),
    ADD COLUMN description TEXT;

-- El código debería ser NOT NULL UNIQUE, pero como hay datos existentes
-- primero se agrega nullable, se llena, y luego se restringe
-- (ver V6 para agregar constraints después de migrar datos)

-- projects
ALTER TABLE projects
    ADD COLUMN code VARCHAR(30),
    ADD COLUMN subdivision VARCHAR(100),
    ADD COLUMN weight NUMERIC(5,2);

-- project_coordinators: agregar rol en proyecto
ALTER TABLE project_coordinators
    ADD COLUMN role_in_project VARCHAR(30) DEFAULT 'Coordinador'
        CHECK (role_in_project IN ('Líder', 'Directivo', 'Administrador', 'Coordinador'));

-- activities
ALTER TABLE activities
    ADD COLUMN code VARCHAR(30),
    ADD COLUMN measurement_unit VARCHAR(50),
    ADD COLUMN goal NUMERIC(12,2),
    ADD COLUMN weight NUMERIC(5,2);

-- sdg_indicators
ALTER TABLE sdg_indicators
    ADD COLUMN description TEXT;

-- tasks
ALTER TABLE tasks
    ADD COLUMN task_type VARCHAR(100),
    ADD COLUMN weight NUMERIC(5,2),
    ADD COLUMN observation TEXT,
    ADD COLUMN start_date DATE;

-- users.role: ampliar enum para incluir ADMINISTRADOR
ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users
    ADD CONSTRAINT users_role_check
        CHECK (role IN ('DIRECTOR', 'COORDINADOR', 'ADMINISTRADOR'));

-- Índices para nuevos campos de búsqueda frecuente
CREATE INDEX idx_programs_code ON programs(code);
CREATE INDEX idx_projects_code ON projects(code);
CREATE INDEX idx_projects_subdivision ON projects(subdivision);
CREATE INDEX idx_activities_code ON activities(code);
CREATE INDEX idx_tasks_start_date ON tasks(start_date);
```

### V6 — Agregar constraints NOT NULL después de migración de datos

```sql
-- V6__enforce_pac_code_constraints.sql
-- Ejecutar DESPUÉS de migrar datos desde Sheets y llenar los campos code
-- No ejecutar en vacío o fallará por nulls existentes

-- Descomentar cuando los datos estén migrados:
-- ALTER TABLE programs ALTER COLUMN code SET NOT NULL;
-- ALTER TABLE programs ADD CONSTRAINT uq_programs_code UNIQUE (code);
-- ALTER TABLE projects ALTER COLUMN code SET NOT NULL;
-- ALTER TABLE projects ADD CONSTRAINT uq_projects_code UNIQUE (code);
-- ALTER TABLE activities ALTER COLUMN code SET NOT NULL;
```

### V7 — Crear tabla de programación de tareas

```sql
-- V7__create_task_schedules.sql
-- Programación de tareas: una tarea del catálogo puede programarse
-- para diferentes períodos con metas parciales

CREATE TABLE task_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    activity_id UUID NOT NULL REFERENCES activities(id) ON DELETE RESTRICT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    scheduled_goal NUMERIC(12,2),
    responsible VARCHAR(150),
    observation TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_schedule_dates CHECK (end_date >= start_date)
);

CREATE INDEX idx_task_schedules_task ON task_schedules(task_id);
CREATE INDEX idx_task_schedules_activity ON task_schedules(activity_id);
CREATE INDEX idx_task_schedules_dates ON task_schedules(start_date, end_date);
```

### V8 — Crear tabla de seguimiento mensual

```sql
-- V8__create_follow_ups.sql
-- Seguimiento mensual de avance por tarea (~969 registros en producción)

CREATE TABLE follow_ups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE RESTRICT,
    reported_month INT NOT NULL CHECK (reported_month BETWEEN 1 AND 12),
    reported_year INT NOT NULL,
    physical_progress NUMERIC(12,2),
    evidence_url TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'EN_PROCESO'
        CHECK (status IN ('EN_PROCESO', 'CUMPLIDA', 'REZAGADA', 'NO_APLICA')),
    observations TEXT,
    reported_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_followup_task_month UNIQUE (task_id, reported_year, reported_month)
);

CREATE INDEX idx_follow_ups_task ON follow_ups(task_id);
CREATE INDEX idx_follow_ups_period ON follow_ups(reported_year, reported_month);
CREATE INDEX idx_follow_ups_status ON follow_ups(status);
```

### V9 — Crear tabla de ejecución financiera

```sql
-- V9__create_financial_executions.sql
-- Ejecución presupuestal por proyecto y período (~879 registros por corte mensual)

CREATE TABLE financial_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE RESTRICT,
    period_month INT NOT NULL CHECK (period_month BETWEEN 1 AND 12),
    period_year INT NOT NULL,
    approved_budget NUMERIC(18,2) DEFAULT 0,
    commitments NUMERIC(18,2) DEFAULT 0,
    obligations NUMERIC(18,2) DEFAULT 0,
    payments NUMERIC(18,2) DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_financial_project_period UNIQUE (project_id, period_year, period_month)
);

CREATE INDEX idx_financial_project ON financial_executions(project_id);
CREATE INDEX idx_financial_period ON financial_executions(period_year, period_month);
```

**Nota:** `uncommitted_budget` es calculable (`approved_budget - commitments`), no se almacena.

---

## Parte 4: Relaciones y diagrama

### Relaciones actuales (V1-V4)

```
strategic_lines  1──N  programs            (FK: programs.strategic_line_id)
programs         1──N  projects            (FK: projects.program_id)
projects         N──M  users               (via project_coordinators)
projects         1──N  activities           (FK: activities.project_id)
activities       N──M  sdg_indicators       (via activity_sdg_indicators)
activities       1──N  tasks               (FK: tasks.activity_id)
tasks            N──1  users               (FK: tasks.responsible_user_id, nullable)
tasks            1──N  task_reschedules     (FK: task_reschedules.task_id)
task_reschedules N──1  users               (FK: task_reschedules.changed_by_user_id, nullable)
users            1──N  password_reset_tokens (FK: password_reset_tokens.user_id)
```

### Relaciones objetivo (con tablas nuevas V7-V9)

```
strategic_lines       1──N  programs
programs              1──N  projects
projects              N──M  users                   (via project_coordinators + role_in_project)
projects              1──N  activities
projects              1──N  financial_executions     ← NUEVA (V9)
activities            N──M  sdg_indicators           (via activity_sdg_indicators)
activities            1──N  tasks
tasks                 N──1  users                    (responsable)
tasks                 1──N  task_schedules           ← NUEVA (V7)
tasks                 1──N  task_reschedules
tasks                 1──N  follow_ups               ← NUEVA (V8)
follow_ups            N──1  users                    (reported_by)
task_reschedules      N──1  users                    (changed_by)
users                 1──N  password_reset_tokens
```

### Verificación de FK constraints e índices

| FK | Índice en columna FK | Estado |
|----|---------------------|--------|
| `programs.strategic_line_id` | ✅ `idx_programs_strategic_line` | OK |
| `projects.program_id` | ✅ `idx_projects_program` | OK |
| `project_coordinators.project_id` | ✅ Parte de PK compuesta | OK |
| `project_coordinators.user_id` | ⚠️ No tiene índice propio | **FALTA** — buscar coordinadores por user_id es lento |
| `activities.project_id` | ✅ `idx_activities_project` | OK |
| `activity_sdg_indicators.activity_id` | ✅ Parte de PK compuesta | OK |
| `activity_sdg_indicators.sdg_indicator_id` | ⚠️ No tiene índice propio | **FALTA** — buscar actividades por ODS es lento |
| `tasks.activity_id` | ✅ `idx_tasks_activity` | OK |
| `tasks.responsible_user_id` | ⚠️ No tiene índice | **FALTA** — buscar tareas por responsable |
| `task_reschedules.task_id` | ✅ `idx_task_reschedules_task` | OK |
| `task_reschedules.changed_by_user_id` | ⚠️ No tiene índice | Bajo impacto — consulta infrecuente |
| `password_reset_tokens.user_id` | ✅ `idx_password_reset_tokens_user_id` | OK |

---

## Parte 5: Problemas de diseño

### 1. Enum `UserRole` incompleto
- **BD (CHECK):** `'DIRECTOR', 'COORDINADOR'`
- **JPA (enum):** `DIRECTOR, COORDINADOR`
- **Sheets:** Tiene también `ADMINISTRADOR`
- **Impacto:** No se puede crear usuarios administradores

### 2. Enum `TaskStatus` vs Sheets
- **BD (CHECK):** `'PROGRAMADA', 'EN_EJECUCION', 'RETRASADA', 'COMPLETADA'`
- **Sheets:** Incluye también `'NO_APLICA'`, `'CANCELADA'`
- **Impacto:** No se pueden marcar tareas como no aplicables o canceladas

### 3. `sdg_indicators.id` es INT, no UUID
- **BD:** `id INT PRIMARY KEY` (valores manuales 1-17)
- **Modelo objetivo:** Sugiere UUID
- **Evaluación:** INT es **correcto** aquí — es un catálogo cerrado de exactamente 17 registros con IDs fijos. UUID sería over-engineering. **No cambiar.**

### 4. Falta `updated_at` en `task_reschedules`
- **BD:** Solo tiene `changed_at` (created)
- **JPA:** Solo `@PrePersist`, no `@PreUpdate`
- **Evaluación:** Un reschedule es inmutable (historial) — **correcto no tener** `updated_at`

### 5. `Activity.sdgIndicators` con FetchType.EAGER
- **JPA:** `@ManyToMany(fetch = FetchType.EAGER)` en `Activity.java:38`
- **Impacto:** Cada vez que se carga una actividad (incluso en un `findAll()`), se cargan TODOS sus indicadores ODS en un JOIN. Con muchas actividades, esto genera queries pesadas innecesarias.
- **Fix:** Cambiar a `FetchType.LAZY` y usar `@EntityGraph` o `JOIN FETCH` cuando se necesiten los indicadores.

### 6. `task_reschedules.justification` limitado a 500 chars
- **BD:** `VARCHAR(500)`
- **Sheets:** Campo de observaciones de texto libre
- **Evaluación:** 500 puede ser corto para justificaciones detalladas. Considerar `TEXT`.

### 7. Falta índice en `project_coordinators.user_id`
- **Query:** `ProjectRepository.findByCoordinators_Id(UUID userId)` genera un JOIN que filtra por `user_id`
- **Sin índice:** Full table scan en `project_coordinators` para encontrar proyectos de un coordinador
- **Fix:** `CREATE INDEX idx_project_coordinators_user ON project_coordinators(user_id);`

### 8. Falta índice en `tasks.responsible_user_id`
- **Query:** Filtrar tareas por responsable es un caso de uso frecuente
- **Fix:** `CREATE INDEX idx_tasks_responsible ON tasks(responsible_user_id);`

### 9. Inconsistencia en naming: `justification` vs `reason`
- **BD (task_reschedules):** `justification`
- **Modelo objetivo:** `reason`
- **Evaluación:** Ambos son válidos. `justification` es más descriptivo. **Mantener.**

### 10. No hay trigger para `updated_at`
- Las columnas `updated_at` en todas las tablas se llenan con `DEFAULT now()` solo al INSERT
- La actualización la maneja JPA con `@PreUpdate`, pero si alguien ejecuta SQL directo, `updated_at` no se actualizará
- **Fix opcional:** Crear un trigger PostgreSQL `set_updated_at()` o aceptar que solo JPA maneja las actualizaciones

### 11. `tasks.due_date` sin validación contra `start_date`
- Si se agrega `start_date` (V5), debería existir `CHECK (due_date >= start_date)`
- Actualmente no hay validación ni en BD ni en JPA de que la fecha límite sea futura

---

## Resumen de brechas

| Categoría | Campos/tablas faltantes | Prioridad |
|-----------|------------------------|-----------|
| Códigos PAC (code) | 3 campos (programs, projects, activities) | 🔴 ALTA — necesarios para migración de datos |
| Ponderaciones (weight) | 4 campos (programs, projects, activities, tasks) | 🟡 MEDIA — necesarios para cálculos de avance |
| Tabla `follow_ups` | Tabla completa — 969 registros en prod | 🔴 ALTA — funcionalidad core pendiente |
| Tabla `financial_executions` | Tabla completa — 2637 registros en prod | 🟡 MEDIA — módulo financiero no iniciado |
| Tabla `task_schedules` | Tabla completa | 🟡 MEDIA — programación por períodos |
| Campos adicionales | subdivision, description, task_type, observation | 🟢 BAJA — no bloquean pero enriquecen |
| Índices faltantes | 3 índices en FKs existentes | 🟡 MEDIA — rendimiento |
| Enum `UserRole` | Falta ADMINISTRADOR | 🔴 ALTA — bloquea creación de admins |
