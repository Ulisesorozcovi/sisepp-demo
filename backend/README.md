# SISEPP — Backend

Proyecto de portafolio inspirado en un caso de seguimiento de planes de inversión pública.

> **Proyecto de portafolio con datos ficticios.** No está afiliado a ninguna entidad. Licencia MIT.

API REST construida con Spring Boot 3.3, PostgreSQL y autenticación JWT.

---

## Stack Técnico

| Componente | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.2 |
| PostgreSQL | 14+ |
| Flyway | Migraciones automáticas (V1–V16) |
| JWT (jjwt) | 0.12.5 |
| Lombok | 1.18.48 |
| Maven Wrapper | Incluido (`./mvnw`) |

---

## Inicio Rápido

### 1. Requisitos previos

- Java 17+
- PostgreSQL corriendo en `localhost:5432`

### 2. Crear la base de datos

```sql
CREATE USER sisepp_user WITH PASSWORD '<CAMBIAR>';
CREATE DATABASE sisepp OWNER sisepp_user;
-- Extensión requerida para hash de contraseñas en migraciones seed
\c sisepp
CREATE EXTENSION IF NOT EXISTS pgcrypto;
```

### 3. Ejecutar

Variables obligatorias (no tienen valor por defecto):

```bash
export SISEPP_DB_PASS='<la contraseña de sisepp_user>'
export SISEPP_JWT_SECRET="$(openssl rand -base64 48)"   # mín. 32 caracteres
# Solo para cuentas de prueba (perfil dev):
export SPRING_PROFILES_ACTIVE=dev
export SISEPP_SEED_PASSWORD='<elija una contraseña>'

./mvnw spring-boot:run        # Linux / Mac
mvnw.cmd spring-boot:run      # Windows (use `set` en lugar de `export`)
```

El servidor inicia en **http://localhost:8081**.  
Flyway aplica las migraciones automáticamente al arrancar.

### 4. Swagger UI

Documentación interactiva disponible en:

```
http://localhost:8081/swagger-ui.html
```

---

## Arquitectura

Hexagonal (Ports & Adapters) por módulo:

```
dev.demo.sisepp
├── auth/                 # Login, JWT, reset de contraseña
├── usuarios/             # CRUD de usuarios (4 roles)
├── planificacion/        # Instrumentos: PGAR → PAC → POAI
├── catalogo/             # Líneas Estratégicas, Indicadores ODS
├── programas/            # Programas del PAC
├── proyectos/            # Proyectos + asignación de coordinadores
├── actividades/          # Actividades + metas anuales
├── tareas/               # Tareas, cronograma, reprogramación
├── seguimiento/          # Seguimiento mensual (follow-ups)
├── financiero/           # Ejecución financiera por proyecto
├── estructura/           # Árbol consolidado de estructura PAC
└── shared/               # Excepciones, enums, TraceIdFilter
```

Cada módulo sigue:
```
domain/
  model/           # Records inmutables
  port/input/      # Use cases (interfaces)
  port/output/     # Repository ports
application/
  dto/             # Request / Response records
  service/         # Implementación de use cases
infrastructure/
  input/rest/      # Controllers REST
  output/persistence/  # JPA entities, adapters, mappers
```

---

## Endpoints API (`/api/v1`)

### Autenticación — `/auth`

| Método | Ruta | Roles | Descripción |
|---|---|---|---|
| POST | `/auth/login` | Público | Login → JWT token |
| POST | `/auth/forgot-password` | Público | Solicitar reset de contraseña |
| POST | `/auth/reset-password` | Público | Aplicar reset con token |

### Usuarios — `/users`

| Método | Ruta | Roles | Descripción |
|---|---|---|---|
| GET | `/users` | DIR, ADM | Listar (filtros: role, status, search) |
| POST | `/users` | DIR, ADM | Crear usuario |
| PUT | `/users/{id}` | DIR, ADM | Actualizar usuario |
| PATCH | `/users/{id}/inactivate` | DIR, ADM | Desactivar |
| PATCH | `/users/{id}/activate` | DIR, ADM | Reactivar |

### Instrumentos de Planificación — `/planning-instruments`

| Método | Ruta | Roles | Descripción |
|---|---|---|---|
| GET | `/planning-instruments` | DIR, COO, ADM | Listar (filtros: type, status) |
| GET | `/planning-instruments/{id}` | DIR, COO, ADM | Detalle |
| POST | `/planning-instruments` | DIR | Crear PGAR/PAC/POAI |
| PUT | `/planning-instruments/{id}` | DIR | Actualizar |

### Estructura PAC — `/structure`

| Método | Ruta | Roles | Descripción |
|---|---|---|---|
| GET | `/structure/tree` | DIR, COO, ADM | Árbol completo (vigencia) |
| GET | `/structure/strategic-lines` | DIR, COO, ADM | Líneas Estratégicas |
| POST | `/structure/strategic-lines` | DIR | Crear línea |
| PUT | `/structure/strategic-lines/{id}` | DIR | Actualizar línea |
| GET | `/structure/sdg-indicators` | DIR, COO, ADM | 17 Indicadores ODS |
| GET | `/structure/programs` | DIR, COO, ADM | Programas |
| POST | `/structure/programs` | DIR | Crear programa |
| PUT | `/structure/programs/{id}` | DIR | Actualizar programa |
| GET | `/structure/projects` | DIR, COO, ADM | Proyectos (mine=true filtra) |
| POST | `/structure/projects` | DIR | Crear proyecto |
| PUT | `/structure/projects/{id}` | DIR | Actualizar proyecto |
| POST | `/structure/projects/{id}/coordinators` | DIR | Asignar coordinador |
| DELETE | `/structure/projects/{id}/coordinators/{userId}` | DIR | Remover coordinador |
| GET | `/structure/activities` | DIR, COO, ADM | Actividades |
| POST | `/structure/activities` | DIR | Crear actividad |
| PUT | `/structure/activities/{id}` | DIR | Actualizar actividad |

### Tareas — `/tasks`

| Método | Ruta | Roles | Descripción |
|---|---|---|---|
| GET | `/tasks` | Autenticado | Listar (filtros: projectId, activityId, status, responsible, from, to) |
| POST | `/tasks` | DIR, COO, ADM | Crear tarea |
| PATCH | `/tasks/{id}/reschedule` | DIR, COO, ADM | Reprogramar fecha |

### Seguimiento — `/follow-ups`

| Método | Ruta | Roles | Descripción |
|---|---|---|---|
| GET | `/follow-ups` | Autenticado | Listar (filtros: taskId, year, month) |
| POST | `/follow-ups` | COO, TEC | Registrar avance mensual |
| PUT | `/follow-ups/{id}` | COO, TEC | Actualizar avance |
| DELETE | `/follow-ups/{id}` | DIR, COO | Eliminar |

### Ejecución Financiera — `/financial-executions`

| Método | Ruta | Roles | Descripción |
|---|---|---|---|
| GET | `/financial-executions` | DIR, COO, ADM | Listar (filtros: projectId, year, month) |
| POST | `/financial-executions` | DIR, COO | Registrar ejecución |
| PUT | `/financial-executions/{id}` | DIR, COO | Actualizar |
| DELETE | `/financial-executions/{id}` | DIR | Eliminar |

---

## Roles del Sistema

| Rol | Código | Permisos principales |
|---|---|---|
| Director | `DIRECTOR` | Acceso total: estructura, tareas, seguimiento, financiero, usuarios |
| Administrador | `ADMINISTRADOR` | Similar a Director, gestiona usuarios y estructura |
| Coordinador | `COORDINADOR` | Crea tareas en sus proyectos asignados, registra avance |
| Técnico | `TECNICO` | Ve tareas asignadas, registra avance/evidencia |

---

## Migraciones Flyway (V1–V16)

| Ver | Propósito |
|---|---|
| V1 | Tabla `users` + `password_reset_tokens` |
| V2 | Estructura PAC: strategic_lines, programs, projects, activities |
| V3 | Seed 17 indicadores ODS |
| V4 | Tablas `tasks` + `task_reschedules` |
| V5 | Campos de negocio: codes, weights, descriptions |
| V6 | Índices FK faltantes (performance) |
| V7 | Tabla `task_schedules` |
| V8 | Tabla `follow_ups` (seguimiento mensual) |
| V9 | Tabla `financial_executions` |
| V10 | Tabla `planning_instruments` (auto-referencial) |
| V11 | FK de strategic_lines→PGAR, programs→PAC, projects→PAC |
| V12 | Tabla `activity_annual_goals` |
| V13 | Seed: 1 PGAR, 1 PAC, 1 POAI |
| V14 | Rol TECNICO en CHECK constraint |
| V15 | Normalizar roles coordinador a mayúsculas |
| V16 | Seed usuarios de prueba + password admin |

---

## Modelo de Datos (Jerarquía)

```
planning_instruments (auto-referencial)
  PGAR (parent_id = null)
  └── PAC (parent_id → PGAR)
        └── POAI (parent_id → PAC)

strategic_lines → pgar_id → planning_instruments
  └── programs → strategic_line_id, pac_id
        └── projects → program_id, pac_id
              ├── project_coordinators (N:M users, con rol)
              ├── activities → project_id
              │     ├── activity_annual_goals
              │     ├── activity_sdg_indicators (N:M)
              │     └── tasks → activity_id
              │           ├── task_schedules
              │           ├── task_reschedules
              │           └── follow_ups
              └── financial_executions
```

---

## Usuarios de Prueba

Las cuentas de prueba solo existen con el perfil `dev` (`SPRING_PROFILES_ACTIVE=dev`), que aplica las semillas de `db/seed-dev`.
La contraseña **no está en el repositorio**: se define al arrancar con `SISEPP_SEED_PASSWORD`.

| Rol | Email | Contraseña |
|---|---|---|
| Director | `director@example.com` | la de `SISEPP_SEED_PASSWORD` |
| Coordinador | `coordinador@example.com` | la de `SISEPP_SEED_PASSWORD` |
| Técnico | `tecnico@example.com` | la de `SISEPP_SEED_PASSWORD` |
| Staff | `staff@example.com` | la de `SISEPP_SEED_PASSWORD` |

---

## Configuración

Archivo: `src/main/resources/application.yml`

| Propiedad | Valor por defecto | Descripción |
|---|---|---|
| `server.port` | 8081 | Puerto del servidor |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/sisepp` | URL de PostgreSQL |
| `sisepp.security.jwt-secret` | **obligatorio, sin valor por defecto** (`SISEPP_JWT_SECRET`) | Secreto JWT (mín. 32 caracteres); la app no arranca sin él |
| `sisepp.security.jwt-expiration-minutes` | 480 | Duración del token (8h) |
| `sisepp.login.max-failed-attempts` | 5 | Intentos antes de bloqueo |
| `sisepp.login.lock-minutes` | 15 | Minutos de bloqueo |

---

## Comandos Útiles

```bash
# Compilar sin tests
./mvnw clean package -DskipTests

# Ejecutar tests
./mvnw test

# Ejecutar en desarrollo
./mvnw spring-boot:run

# Verificar migraciones pendientes
./mvnw flyway:info
```

---

## Troubleshooting Windows (MSI / PowerShell)

### JAVA_HOME apunta a JRE 1.8 en vez de JDK 17+

**Cuándo ocurre:** El sistema tiene un JRE antiguo (ej. 1.8.0_301) como JAVA_HOME por defecto, pero se necesita JDK 17+ para compilar Spring Boot 3.3.

**Síntoma:** `mvnw.cmd` falla con errores de versión de Java o no encuentra `javac`.

**Solución en el MSI (JDK 20 instalado en `C:\Program Files\Java\jdk-20`):**

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-20"
```

> Esto solo aplica para la sesión actual de PowerShell. Para hacerlo permanente, configurar la variable de entorno del sistema.

### `mvnw.cmd` falla con "Usage: java [options] \<mainclass\>"

**Cuándo ocurre:** El script `mvnw.cmd` usa `%~dp0` para obtener el directorio base, que siempre termina en `\`. Cuando Java parsea el argumento `"-Dmaven.multiModuleProjectDirectory=C:\...\sisepp-backend\"`, la `\"` al final escapa la comilla de cierre, rompiendo el parseo de todos los argumentos posteriores. Java no reconoce la main class y muestra el mensaje de ayuda.

**Síntoma:**
```
"-Dmaven.multiModuleProjectDirectory=C:\...\sisepp-backend\"
Usage: java [options] <mainclass> [args...]
```

**Solución:** En `mvnw.cmd`, agregar esta línea después de `@SET BASEDIR=%~dp0`:

```batch
@IF "%BASEDIR:~-1%"=="\" SET BASEDIR=%BASEDIR:~0,-1%
```

Esto elimina la `\` final del path antes de usarlo en argumentos entre comillas.

### Comando completo para compilar y correr en el MSI

```powershell
# 1. Setear JAVA_HOME (una vez por sesión de PowerShell)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-20"

# 2. Ir al directorio del backend
cd C:\Users\felip\Desktop\SPC\sisepp\sisepp-backend

# 3. Compilar
cmd /c "mvnw.cmd clean compile"

# 4. Ejecutar (en otra terminal o después de compilar)
cmd /c "mvnw.cmd spring-boot:run"
```

> **Nota:** En PowerShell 5.1 no existe el operador `&&`. Ejecutar cada comando por separado, esperando que termine antes de lanzar el siguiente.
