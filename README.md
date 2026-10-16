# SISEPP — Sistema de Seguimiento a Planes y Proyectos de Inversión Pública

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?logo=openjdk" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 3.3" />
  <img src="https://img.shields.io/badge/React-18.3-61DAFB?logo=react&logoColor=black" alt="React 18" />
  <img src="https://img.shields.io/badge/TypeScript-5.5-3178C6?logo=typescript&logoColor=white" alt="TypeScript 5.5" />
  <img src="https://img.shields.io/badge/PostgreSQL-14+-4169E1?logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Vite-5.4-646CFF?logo=vite&logoColor=white" alt="Vite" />
  <img src="https://img.shields.io/badge/CI-GitHub_Actions-2088FF?logo=githubactions&logoColor=white" alt="CI" />
  <img src="https://img.shields.io/badge/License-MIT-green" alt="MIT License" />
</p>

> **Demo de portafolio con datos ficticios.** No está afiliado a ninguna entidad gubernamental. Licencia MIT.

---

## Qué es SISEPP

Plataforma web fullstack para la **planificación, seguimiento y evaluación de proyectos de inversión pública**. Permite a directores, coordinadores y técnicos gestionar el ciclo completo de un plan de inversión: desde la definición de líneas estratégicas y programas, pasando por la creación de proyectos y actividades con metas anuales, hasta el seguimiento mensual de avance y la ejecución financiera.

### Problema que resuelve

Las entidades públicas manejan decenas de proyectos simultáneos con presupuestos asignados, cronogramas estrictos y múltiples responsables. Sin una herramienta centralizada, el seguimiento se fragmenta en hojas de cálculo, correos y reportes manuales, generando pérdida de trazabilidad, retrasos no detectados y ejecución presupuestal sin visibilidad en tiempo real.

SISEPP centraliza todo esto en una sola plataforma con roles diferenciados, trazabilidad completa y reportes consolidados.

---

## Stack Técnico

### Backend — Java 17 / Spring Boot 3.3

| Tecnología | Uso |
|---|---|
| **Spring Boot 3.3.2** | Framework principal, configuración auto, embedded Tomcat |
| **Spring Security + JWT** | Autenticación stateless con tokens JWT (jjwt 0.12), refresh, lockout por intentos fallidos |
| **Spring Data JPA / Hibernate** | ORM, queries derivadas, relaciones complejas (auto-referenciales, N:M) |
| **Spring Validation** | Validación declarativa con Jakarta Bean Validation en DTOs |
| **Spring Mail** | Envío de correos transaccionales (reset de contraseña, notificaciones) |
| **PostgreSQL 14+** | Base de datos relacional, constraints CHECK, extensión pgcrypto |
| **Flyway** | Migraciones versionadas (29 migraciones), perfiles separados para seeds |
| **Testcontainers** | Tests de integración con PostgreSQL real en Docker (no mocks) |
| **SpringDoc OpenAPI** | Documentación interactiva Swagger UI auto-generada |
| **Lombok** | Reducción de boilerplate (builders, getters, constructors) |
| **Maven Wrapper** | Build reproducible sin instalación previa de Maven |
| **Docker Compose** | Orquestación de servicios para despliegue |

### Frontend — React 18 / TypeScript / Vite

| Tecnología | Uso |
|---|---|
| **React 18.3** | UI declarativa con hooks (`useState`, `useEffect`, `useContext`, `useCallback`) |
| **TypeScript 5.5 (strict)** | Tipado estático, interfaces alineadas con DTOs del backend, union types como enums |
| **Vite 5.4** | Bundler con HMR instantáneo, build optimizado con tree-shaking |
| **React Router DOM 6** | Routing declarativo, rutas protegidas por rol, redirección por perfil |
| **Axios** | Cliente HTTP con interceptors para JWT y manejo centralizado de errores 401 |
| **Vanilla CSS + Design Tokens** | Sistema de diseño propio sin dependencias (sin Tailwind, Bootstrap ni CSS-in-JS) |

### DevOps / Tooling

| Herramienta | Uso |
|---|---|
| **GitHub Actions** | CI/CD: build + tests backend, build + type-check frontend |
| **Docker + Docker Compose** | Contenedorización para despliegue piloto |
| **gitleaks** | Escaneo automatizado de secretos en el repositorio |

---

## Arquitectura

### Backend — Hexagonal (Ports & Adapters)

El backend implementa **arquitectura hexagonal** con separación estricta entre dominio, aplicación e infraestructura:

```
dev.demo.sisepp
├── auth/                  # Autenticación, JWT, reset de contraseña
├── usuarios/              # CRUD de usuarios con 4 roles
├── planificacion/         # Instrumentos: PGAR → PAC → POAI
├── catalogo/              # Líneas estratégicas, indicadores ODS
├── programas/             # Programas del PAC
├── proyectos/             # Proyectos + asignación de coordinadores
├── actividades/           # Actividades + metas anuales
├── tareas/                # Tareas, cronograma, reprogramación
├── seguimiento/           # Seguimiento mensual con evidencia
├── financiero/            # Ejecución financiera por proyecto
├── estructura/            # Árbol consolidado PAC (dashboard)
└── shared/                # Excepciones globales, enums, TraceIdFilter
```

Cada módulo sigue la misma estructura interna:

```
domain/
  model/                   # Records inmutables (value objects)
  port/input/              # Use cases (interfaces)
  port/output/             # Repository ports (interfaces)
application/
  dto/                     # Request / Response records
  service/                 # Implementación de use cases
infrastructure/
  input/rest/              # Controllers REST
  output/persistence/      # JPA entities, adapters, mappers
```

**263 clases Java** organizadas en **13 módulos de dominio** con boundaries claros. Ningún módulo accede directamente a la persistencia de otro — toda comunicación pasa por los puertos definidos.

### Frontend — Atomic Design + Feature-based

```
src/
  api/                     # Cliente HTTP por contexto (auth, users, tasks, structure, ...)
  context/                 # AuthContext (JWT, roles, sesión)
  design-system/
    atoms/                 # 10 componentes base: Button, Input, Select, Badge, Avatar, Icon, ...
    molecules/             # 8 composiciones: FormField, Alert, PasswordField, TreeRow, ...
    organisms/             # 15+ componentes complejos: tablas, modales, formularios, Gantt
    templates/             # Layouts: AuthLayout (público), DashboardLayout (autenticado)
  pages/                   # 18 páginas conectadas a rutas
  routes/                  # ProtectedRoute con validación de roles
  styles/                  # Design tokens (tokens.css) + estilos globales
  types/                   # Interfaces TypeScript alineadas 1:1 con los DTOs del backend
  utils/                   # Helpers (manejo de errores API)
```

**66 componentes React** con **sistema de diseño propio** basado en design tokens CSS (colores, tipografía, radios, sombras). Sin dependencias de frameworks CSS — cada componente usa clases con prefijo por capa (`atom-btn`, `molecule-field`, `organism-table-card`).

---

## Funcionalidades Principales

### Planificación Estratégica
- Jerarquía de instrumentos de planificación: **PGAR → PAC → POAI** (auto-referencial)
- Líneas estratégicas con alineación a **17 Indicadores ODS** (Objetivos de Desarrollo Sostenible)
- Árbol consolidado que permite navegar toda la estructura desde un solo dashboard

### Gestión de Proyectos y Tareas
- Proyectos con asignación de coordinadores (relación N:M)
- Actividades con **metas anuales** cuantificables
- Tareas con cronograma, responsable y **reprogramación con trazabilidad**
- Vista Gantt para visualización de cronogramas
- Catálogo y consulta de tareas con filtros avanzados

### Seguimiento y Reportes
- Seguimiento mensual con registro de avance porcentual
- Ejecución financiera por proyecto (presupuesto vs. ejecutado)
- Reportes consolidados con exportación
- Tracking de metas vs. ejecución real

### Seguridad y Control de Acceso
- **4 roles** con permisos granulares: Director, Administrador, Coordinador, Técnico
- JWT stateless con expiración configurable (default 8h)
- Lockout automático tras 5 intentos fallidos (15 min)
- Fail-fast: la aplicación **no arranca** sin un secreto JWT de al menos 256 bits
- Reset de contraseña por email con token temporal
- Passwords hasheados con BCrypt

---

## API REST

40+ endpoints organizados bajo `/api/v1/`, documentados con **Swagger UI** (`/swagger-ui.html`):

| Módulo | Endpoints | Roles |
|---|---|---|
| Autenticación | Login, forgot/reset password | Público |
| Usuarios | CRUD, activar/desactivar | Director, Administrador |
| Instrumentos | PGAR, PAC, POAI | Director |
| Estructura | Líneas, programas, proyectos, actividades | Director, Coordinador, Administrador |
| Tareas | CRUD, reprogramación | Director, Coordinador, Administrador |
| Seguimiento | Avance mensual | Coordinador, Técnico |
| Financiero | Ejecución presupuestal | Director, Coordinador |
| Dashboard | Árbol consolidado por vigencia | Todos los autenticados |

---

## Testing

- **Tests de integración** con **Testcontainers**: PostgreSQL real en Docker, no mocks de base de datos
- `IntegrationTestBase` como clase base que configura el contenedor y el contexto Spring
- Tests end-to-end del ciclo de vida: creación de estructura → asignación → seguimiento
- CI ejecuta los tests automáticamente en cada push a `main`

---

## Modelo de Datos

Diseño relacional con 15+ tablas, constraints CHECK, índices FK y relaciones auto-referenciales:

```
planning_instruments (auto-referencial)
  PGAR (parent = null)
  └── PAC (parent → PGAR)
        └── POAI (parent → PAC)

strategic_lines → PGAR
  └── programs → strategic_line + PAC
        └── projects → program + PAC
              ├── project_coordinators (N:M con users)
              ├── activities → project
              │     ├── activity_annual_goals
              │     ├── activity_sdg_indicators (N:M)
              │     └── tasks → activity
              │           ├── task_schedules
              │           ├── task_reschedules
              │           └── follow_ups
              └── financial_executions
```

29 migraciones Flyway versionadas, con separación entre schema (`db/migration`) y datos de prueba (`db/seed-dev`).

---

## Cómo Ejecutar

### Requisitos

- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Docker (para tests de integración con Testcontainers)

### Backend

```bash
cd backend

# Configurar variables de entorno (ver .env.piloto.example)
export SISEPP_DB_PASS='<contraseña>'
export SISEPP_JWT_SECRET="$(openssl rand -base64 48)"
export SPRING_PROFILES_ACTIVE=dev
export SISEPP_SEED_PASSWORD='<contraseña para usuarios de prueba>'

# Ejecutar
./mvnw spring-boot:run
```

El servidor inicia en `http://localhost:8081`. Flyway aplica las migraciones automáticamente.

### Frontend

```bash
cd frontend
npm install
cp .env.example .env
npm run dev
```

La aplicación inicia en `http://localhost:5173`.

### Usuarios de prueba

| Rol | Email | Contraseña |
|---|---|---|
| Director | `director@example.com` | la definida en `SISEPP_SEED_PASSWORD` |
| Coordinador | `coordinador@example.com` | idem |
| Técnico | `tecnico@example.com` | idem |

---

## CI/CD

GitHub Actions con dos workflows independientes:

- **Backend CI**: compila con Maven, ejecuta tests con Testcontainers (JDK 17, Ubuntu)
- **Frontend CI**: instala dependencias, type-check TypeScript y build Vite (Node.js 20)

Ambos se ejecutan en push y PR a `main`, filtrados por paths para evitar builds innecesarios.

---

## Decisiones de Diseño

| Decisión | Justificación |
|---|---|
| Arquitectura hexagonal | Desacoplar lógica de negocio de la infraestructura; facilita testing y cambio de implementaciones |
| Records inmutables en dominio | Prevenir mutación accidental, alinear con principios de DDD |
| Vanilla CSS con design tokens | Control total del sistema de diseño sin dependencias externas ni lock-in de frameworks |
| Atomic Design en componentes | Reutilización consistente y escalable de UI |
| JWT stateless | Escalabilidad horizontal sin estado de sesión en servidor |
| Flyway con perfiles | Separar datos de prueba del schema de producción; fail-fast en secretos |
| Testcontainers | Garantizar que los tests reflejan el comportamiento real de PostgreSQL, no de mocks |

---

## Estructura del Repositorio

```
sisepp-demo/
├── .github/workflows/       # CI: backend-ci.yml, frontend-ci.yml
├── backend/                  # API REST (263 clases Java)
│   ├── src/main/java/        # Código fuente (hexagonal, 13 módulos)
│   ├── src/main/resources/   # Configuración, migraciones Flyway, Postman
│   ├── src/test/             # Tests de integración con Testcontainers
│   ├── docker-compose.piloto.yml
│   └── pom.xml
├── frontend/                 # SPA React (66 componentes)
│   ├── src/                  # Código fuente (Atomic Design)
│   ├── public/
│   ├── package.json
│   └── vite.config.ts
├── LICENSE                   # MIT
└── README.md
```

---

## Licencia

MIT License. Ver [LICENSE](LICENSE).
