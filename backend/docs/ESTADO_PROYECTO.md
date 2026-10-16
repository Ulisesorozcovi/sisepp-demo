# SISEPP Backend — Estado del Proyecto

**Fecha:** 2026-10-05
**Stack:** Spring Boot 3.3.2, Java 17+, PostgreSQL, Flyway, JWT (jjwt), BCrypt
**Puerto:** 8081
**Rama activa:** `feat/resolucion-pre-frontend`

---

## 1. Resumen de lo realizado

### Arquitectura Hexagonal (Ports & Adapters)

Cada módulo sigue el patrón:
- `domain/model/` — Records inmutables (entidades de dominio)
- `domain/port/input/` — Use cases (interfaces)
- `domain/port/output/` — Repository ports (interfaces)
- `application/service/` — Implementación de use cases
- `application/dto/` — Request/Response DTOs
- `infrastructure/input/rest/` — Controllers REST
- `infrastructure/output/persistence/` — JPA entities, repositories, adapters, mappers

### Módulos implementados

| Módulo | Paquete | Descripción |
|--------|---------|-------------|
| **Autenticación** | `auth` | Login JWT, forgot/reset password, BCrypt, bloqueo por intentos fallidos |
| **Usuarios** | `usuarios` | CRUD usuarios, roles (DIRECTOR, STAFF, COORDINADOR, TECNICO), activar/inactivar |
| **Planificación** | `planificacion` | CRUD instrumentos PGAR/PAC/POAI con vigencia |
| **Catálogos** | `catalogo` | Líneas estratégicas, unidades de medida, PIP, categorías temáticas, indicadores ODS, indicadores de gestión |
| **Programas** | `programas` | CRUD programas con ponderación y validación de pesos |
| **Proyectos** | `proyectos` | CRUD proyectos, asignación de coordinadores, ponderación |
| **Actividades** | `actividades` | CRUD actividades, metas anuales editables, presupuestos anuales editables, indicadores ODS |
| **Tareas** | `tareas` | CRUD tareas, reprogramación con justificación, endpoint `with-progress` |
| **Seguimiento** | `seguimiento` | CRUD follow-ups, detección de duplicados (409), auditoría (reportedByName, timestamps) |
| **Financiero** | `financiero` | Ejecuciones financieras por proyecto/año/mes |
| **Reportes** | `estructura` | Matriz PAC (metas + recursos por año) |
| **Carga masiva** | `bulk` | Carga de estructura PAC completa desde JSON |
| **Compartido** | `shared` | Excepciones globales, filtro TraceId, CORS, seguridad JWT, OpenAPI/Swagger |

### Endpoints REST principales

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/api/v1/auth/login` | Público | Login, retorna JWT |
| POST | `/api/v1/auth/forgot-password` | Público | Solicitar reset |
| POST | `/api/v1/auth/reset-password` | Público | Ejecutar reset |
| GET/POST | `/api/v1/users` | STAFF, DIRECTOR | Listar/crear usuarios |
| PUT | `/api/v1/users/{id}` | STAFF, DIRECTOR | Actualizar usuario |
| PATCH | `/api/v1/users/{id}/inactivate` | STAFF, DIRECTOR | Inactivar |
| PATCH | `/api/v1/users/{id}/activate` | STAFF, DIRECTOR | Activar |
| GET/POST/PUT | `/api/v1/planning-instruments` | STAFF, DIRECTOR | CRUD instrumentos |
| GET/POST/PUT | `/api/v1/structure/strategic-lines` | Autenticado | Líneas estratégicas |
| GET/POST/PUT | `/api/v1/structure/programs` | Autenticado | Programas |
| GET/POST/PUT | `/api/v1/structure/projects` | Autenticado | Proyectos |
| POST/DELETE | `/api/v1/structure/projects/{id}/coordinators` | STAFF, DIRECTOR | Coordinadores |
| GET/POST/PUT | `/api/v1/structure/activities` | Autenticado | Actividades (incluye annualGoals/annualBudgets) |
| GET | `/api/v1/structure/activities/{id}` | Autenticado | Detalle de actividad con metas y presupuestos |
| GET | `/api/v1/structure/tree` | Autenticado | Árbol completo |
| GET | `/api/v1/structure/programs/{id}/weight-summary` | Autenticado | Resumen pesos |
| GET | `/api/v1/structure/sdg-indicators` | Autenticado | Indicadores ODS |
| GET/POST | `/api/v1/tasks` | Autenticado | Listar/crear tareas |
| PATCH | `/api/v1/tasks/{id}/reschedule` | STAFF, DIRECTOR, COORD | Reprogramar |
| DELETE | `/api/v1/tasks/{id}` | STAFF | Eliminar tarea |
| GET | `/api/v1/tasks/with-progress` | Autenticado | Tareas con avance acumulado |
| GET/POST | `/api/v1/follow-ups` | Autenticado | Listar/crear seguimientos |
| PUT | `/api/v1/follow-ups/{id}` | Autenticado | Actualizar seguimiento |
| DELETE | `/api/v1/follow-ups/{id}` | STAFF, DIRECTOR, COORD | Eliminar |
| GET/POST/PUT/DELETE | `/api/v1/financial-executions` | Autenticado | Ejecuciones financieras |
| GET | `/api/v1/reports/pac-matrix` | Autenticado | Matriz PAC |
| POST | `/api/v1/bulk/pac-structure` | STAFF | Carga masiva |
| GET | `/api/v1/catalogs/*` | Autenticado | Catálogos (unidades, PIP, categorías, indicadores) |
| GET | `/v3/api-docs/**` | Público | OpenAPI JSON spec |
| GET | `/swagger-ui/**` | Público | Swagger UI interactivo |

### Documentación API (Swagger/OpenAPI)

Springdoc-openapi está configurado y funcional:
- **Config:** `shared/config/OpenApiConfig.java` con esquema JWT Bearer
- **Swagger UI:** accesible en `http://localhost:8081/swagger-ui.html`
- **JSON spec:** accesible en `http://localhost:8081/v3/api-docs`
- **Security:** rutas permitidas sin autenticación en `SecurityConfig`

---

## 2. Deuda técnica

| Prioridad | Descripción | Estado |
|-----------|-------------|--------|
| **Crítica** | Secretos hardcodeados en `application.yml`: JWT secret y credenciales de BD en código fuente versionado. | Pendiente — externalizar a variables de entorno |
| **Alta** | No hay tests unitarios ni de integración. Cero cobertura. No hay `src/test/` en el repo. | Pendiente |
| **Alta** | `GlobalExceptionHandler` sin catch-all `Exception.class`: excepciones no contempladas exponen stack traces al cliente (information disclosure). | Pendiente |
| **Alta** | Los proyectos se crean sin `pacId` — la relación PAC-Proyecto existe en la entidad pero nunca se asigna. El reporte PAC usa `vigenciaBetween` como workaround. | Pendiente |
| **Media** | N+1 en `PacMatrixService`: `thematicCategoryRepo.findById()` y `measurementUnitRepo.findById()` dentro del loop de actividades. Con 50 actividades = 100 queries extra. | Pendiente — resolver con bulk fetch |
| **Media** | No hay paginación en ningún endpoint de listado. Todos devuelven colecciones completas en memoria. | Pendiente |
| **Media** | ~~No hay pipeline CI/CD (GitHub Actions).~~ | **Resuelto** — `.github/workflows/ci.yml` (compile con JDK 17 en cada PR a main) |
| **Baja** | El modelo `StrategicLine` no tiene campo `weight` pero el DTO del reporte sí — siempre es null. | Pendiente |
| **Baja** | Falta validación `@Valid` + `@NotBlank` en varios DTOs de request (ej: `CreateFollowUpRequest`). | Pendiente |
| **Baja** | No hay auditoría de cambios (quién modificó qué y cuándo). Solo existen `createdAt`/`updatedAt`. | Pendiente |

---

## 3. Retos identificados

1. **Sin pruebas automatizadas:** No hay tests ni CI/CD. Cualquier regresión se detecta solo en tiempo de ejecución.
2. **Modelo de permisos simplificado:** Control de acceso por rol a nivel de endpoint (`@PreAuthorize`), sin permisos granulares (ej: COORDINADOR ve solo sus proyectos en algunos endpoints, pero no en todos).
3. **Relación PAC-Proyecto débil:** Los proyectos deberían vincularse explícitamente al PAC, no inferirse por vigencia.
4. **Seguridad de configuración:** Secretos en texto plano en archivos versionados. Sin profiles de Spring para separar dev/prod.
5. **Sin sistema de notificaciones:** No hay mecanismo para alertar sobre tareas vencidas o próximas a vencer.

---

## 4. Bloques de spec completados

| Bloque | Descripción | Estado |
|--------|-------------|--------|
| **6** | Endpoint `GET /tasks/with-progress?activityId={uuid}` | Completado |
| **1** | Columnas de auditoría en follow-ups (reportedByName, createdAt/updatedAt) | Completado |
| **4** | Fix reporte PAC — proyectos por vigencia en vez de pacId | Completado |
| **5** | `PUT /activities/{id}` acepta annualGoals/annualBudgets + `GET /activities/{id}` detalle | Completado |

---

## 5. Pendientes para continuar desarrollo

### Funcionalidades por implementar

| Feature | Descripción | Complejidad |
|---------|-------------|-------------|
| **Catch-all en GlobalExceptionHandler** | Agregar `@ExceptionHandler(Exception.class)` con log + respuesta genérica | Baja (30 min) |
| **Externalizar secretos** | Mover JWT secret y DB credentials a variables de entorno | Baja (1 hora) |
| **Fix N+1 PacMatrixService** | Bulk fetch de categorías y unidades antes del loop | Baja (45 min) |
| **Paginación** | Agregar `Pageable` a endpoints de listado críticos (tareas, follow-ups, usuarios) | Media (4 horas) |
| **Notificaciones por vencimiento** | Endpoint que retorne tareas vencidas/próximas a vencer; opcionalmente scheduled job | Alta (8 horas) |
| **Auditoría de cambios** | Implementar con Hibernate Envers o tabla de audit log manual | Alta (12 horas) |

### Infraestructura

| Item | Estado |
|------|--------|
| Pipeline CI/CD (GitHub Actions) | **Configurado** — `.github/workflows/ci.yml` |
| Tests unitarios (services) | No existen |
| Tests de integración (Testcontainers) | No existen |
| Documentación API (Swagger/OpenAPI) | **Configurado y funcional** |
