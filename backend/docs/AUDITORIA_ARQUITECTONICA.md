# Auditoría Arquitectónica — SPC Backend (sisepp-backend)

**Fecha:** 2026-09-28
**Auditor:** Claude Opus 4.6
**Commit auditado:** `4669333` (main)
**Arquitectura de referencia:** Hexagonal (Ports & Adapters) + Monolito Modular

---

## 1. Estructura de Proyecto

### 1.1 Organización por Bounded Contexts

**❌ NO CUMPLE**

El proyecto está organizado por **capas técnicas**, no por bounded contexts:

```
src/main/java/dev/demo/sisepp/
├── bootstrap/          ← DataSeeder
├── config/             ← SecurityConfig
├── controller/         ← TODOS los controllers juntos
├── domain/entity/      ← TODAS las entidades juntas
├── dto/                ← TODOS los DTOs juntos
├── exception/          ← TODAS las excepciones juntas
├── repository/         ← TODOS los repositorios juntos
├── security/           ← JWT filter + service
└── service/            ← TODOS los servicios juntos
```

**Lo esperado:**
```
sisepp/
├── proyectos/
│   ├── domain/model/
│   ├── domain/port/input/
│   ├── domain/port/output/
│   ├── application/service/
│   ├── application/dto/
│   └── infrastructure/input/rest/
│                       output/persistence/
├── tareas/
│   └── (misma estructura)
├── auth/
│   └── (misma estructura)
└── shared/
```

### 1.2 Bounded Contexts esperados

| Bounded Context | ¿Existe? | Estado |
|----------------|----------|--------|
| programas | ⚠️ Código existe pero mezclado en paquetes planos | `ProgramService`, `ProgramController`, `Program` — sin módulo propio |
| proyectos | ⚠️ Igual | `ProjectService`, `ProjectController`, `Project` |
| actividades | ⚠️ Igual | `ActivityService`, `ActivityController`, `Activity` |
| tareas | ⚠️ Igual | `TaskService`, `TaskController`, `Task`, `TaskReschedule` |
| seguimiento | 🔲 No iniciado | No hay código de reportes de avance ni evidencias |
| financiero | 🔲 No iniciado | No hay código de ejecución presupuestal |
| usuarios/auth | ⚠️ Código existe, mezclado | `AuthService`, `UserService`, `JwtAuthFilter`, `SecurityConfig` |

**Contexto adicional no listado:** `structure` — `StructureService` + `StructureController` para consulta de árbol PAC (HU-05), transversal a programas/proyectos/actividades.

---

## 2. Modelos de Dominio

### 2.1 Pureza del dominio

**❌ NO CUMPLE** — Ningún modelo es Java puro.

Todas las clases en `domain/entity/` son **entidades JPA directas**:

| Modelo | `@Entity` | `@Table` | `@Column` | `@ManyToOne` / `@ManyToMany` | `@PrePersist` | Factory methods | Self-validation |
|--------|-----------|----------|-----------|------------------------------|---------------|-----------------|-----------------|
| `Project` | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ❌ No | ❌ No |
| `Program` | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ❌ No | ❌ No |
| `Activity` | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene (EAGER) | ✅ Tiene | ❌ No | ❌ No |
| `Task` | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ❌ No | ❌ No |
| `User` | ✅ Tiene | ✅ Tiene | ✅ Tiene | — | ✅ Tiene | ❌ No | ❌ No |
| `StrategicLine` | ✅ Tiene | ✅ Tiene | ✅ Tiene | — | ✅ Tiene | ❌ No | ❌ No |
| `SdgIndicator` | ✅ Tiene | ✅ Tiene | ✅ Tiene | — | — | ❌ No | ❌ No |
| `TaskReschedule` | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ❌ No | ❌ No |
| `PasswordResetToken` | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ✅ Tiene | ❌ No | ❌ No |

**Violaciones concretas encontradas:**
- `import jakarta.persistence.*;` en TODOS los modelos
- Usa `@GeneratedValue`, `@JoinColumn`, `@JoinTable`, `@Enumerated`
- Usa callbacks JPA: `@PrePersist`, `@PreUpdate`
- `Activity.java:38` — `@ManyToMany(fetch = FetchType.EAGER)` carga ODS siempre — riesgo de N+1
- No hay constructores que validen invariantes (todos usan `@NoArgsConstructor` + `@Builder`)
- No hay self-validation (una `Task` puede crearse con `dueDate` en el pasado, `physicalGoalValue` negativo, etc.)

### 2.2 Entidades JPA separadas

**❌ NO CUMPLE**

No existen entidades JPA separadas. Las clases en `domain/entity/` SON las entidades JPA. No hay:
- `ProyectoJpaEntity` en `infrastructure/output/persistence/`
- Mappers dominio ↔ JPA
- Separación alguna entre modelo de dominio y modelo de persistencia

---

## 3. Puertos e Interfaces

### 3.1 Input Ports (casos de uso)

**❌ NO CUMPLE**

No existe **ninguna interfaz** de caso de uso. Los servicios son clases concretas sin contrato:

| Servicio | ¿Implementa interfaz? | Casos de uso que ofrece |
|----------|----------------------|------------------------|
| `ProjectService` | ❌ No | create, list, listForCoordinator, update, assignCoordinator, removeCoordinator |
| `TaskService` | ❌ No | create, list, reschedule |
| `ProgramService` | ❌ No | create, list, update |
| `ActivityService` | ❌ No | create, list, update |
| `StrategicLineService` | ❌ No | create, list, update |
| `StructureService` | ❌ No | buildTree |
| `AuthService` | ❌ No | login, forgotPassword, resetPassword |
| `UserService` | ❌ No | create, list, update, toggleStatus |
| `SdgIndicatorService` | ❌ No | listAll |

**Lo esperado:** interfaces como `RegistrarProyectoUseCase`, `ConsultarTareasUseCase`, etc. en `domain/port/input/`.

### 3.2 Output Ports (repositorios)

**❌ NO CUMPLE**

Los repositorios son interfaces de Spring Data JPA directamente en `repository/`:

```java
// ProjectRepository.java — EXTIENDE DIRECTAMENTE JpaRepository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByProgramId(UUID programId);
    List<Project> findByVigencia(Integer vigencia);
    List<Project> findByCoordinators_Id(UUID userId);
}
```

No hay:
- Interfaces de puerto de salida en `domain/port/output/` (e.g., `ProyectoRepositoryPort`)
- Adaptadores que implementen esos puertos
- Separación entre el contrato del dominio y Spring Data

Los servicios dependen **directamente** de `JpaRepository`:
- `ProjectService` → `ProjectRepository`, `ProgramRepository`, `UserRepository`
- `TaskService` → `TaskRepository`, `ActivityRepository`, `TaskRescheduleRepository`, `UserRepository`

---

## 4. Servicios (Application Layer)

### 4.1 Implementación de casos de uso

**⚠️ PARCIAL**

| Servicio | `@Service` | Single Responsibility | Depende de output ports | Implementa input port |
|----------|------------|----------------------|------------------------|-----------------------|
| `ProjectService` | ✅ | ⚠️ Mezcla CRUD + asignar coordinadores | ❌ Depende de JpaRepository | ❌ No |
| `TaskService` | ✅ | ⚠️ Mezcla CRUD + reprogramación + autorización | ❌ Depende de JpaRepository | ❌ No |
| `AuthService` | ✅ | ⚠️ Mezcla login + reset password + bloqueo | ❌ Depende de JpaRepository | ❌ No |
| `UserService` | ✅ | ✅ Gestión de usuarios | ❌ Depende de JpaRepository | ❌ No |
| `StructureService` | ✅ | ✅ Construir árbol PAC | ❌ Depende de 4 JpaRepositories | ❌ No |
| `ProgramService` | ✅ | ✅ CRUD de programas | ❌ Depende de JpaRepository | ❌ No |

**Problemas encontrados:**
- `TaskService.list()` (líneas 65-86): filtra **en memoria** con 6 `.filter()` encadenados en vez de usar queries SQL. Con muchas tareas, esto es un problema de rendimiento.
- `StructureService.buildTree()` (líneas 28-69): tiene un problema clásico de **N+1 queries** — carga líneas estratégicas, luego por cada una carga programas, luego por cada programa carga proyectos, luego por cada proyecto carga actividades. Con 10 líneas × 5 programas × 3 proyectos × 4 actividades = **198 queries** en cascada.

### 4.2 DTOs y mappers

**⚠️ PARCIAL**

**DTOs existen:** `CreateProjectRequest`, `ProjectResponse`, `CreateTaskRequest`, `TaskResponse`, etc. — todos como `record` de Java, lo cual es buena práctica.

**Problema:** El mapeo dominio → DTO se hace con métodos estáticos `fromEntity()` dentro del DTO mismo:

```java
// ProjectResponse.java — el DTO conoce la entidad de dominio
public static ProjectResponse fromEntity(Project project) {
    // accede directamente a project.getProgram().getId(), etc.
}
```

Esto acopla los DTOs al modelo de dominio/JPA. Lo correcto sería tener mappers separados (MapStruct o manual) en la capa de aplicación.

---

## 5. Infrastructure

### 5.1 Controllers (Input Adapters)

**⚠️ PARCIAL**

| Controller | Base Path | Usa `@RestController` | Depende de | Versionado API |
|------------|-----------|----------------------|------------|----------------|
| `AuthController` | `/api/auth` | ✅ | `AuthService` (concreto) | ❌ No `/v1/` |
| `UserController` | `/api/users` | ✅ | `UserService` (concreto) | ❌ No `/v1/` |
| `ProjectController` | `/api/structure/projects` | ✅ | `ProjectService` (concreto) | ❌ No `/v1/` |
| `ProgramController` | `/api/structure/programs` | ✅ | `ProgramService` (concreto) | ❌ No `/v1/` |
| `ActivityController` | `/api/structure/activities` | ✅ | `ActivityService` (concreto) | ❌ No `/v1/` |
| `StrategicLineController` | `/api/structure/strategic-lines` | ✅ | `StrategicLineService` (concreto) | ❌ No `/v1/` |
| `SdgIndicatorController` | `/api/sdg-indicators` | ✅ | `SdgIndicatorService` (concreto) | ❌ No `/v1/` |
| `StructureController` | `/api/structure` | ✅ | `StructureService` (concreto) | ❌ No `/v1/` |
| `TaskController` | `/api/tasks` | ✅ | `TaskService` (concreto) | ❌ No `/v1/` |

**Problemas:**
- Todos dependen de **clases concretas**, no de interfaces/input ports
- No hay versionado de API (`/api/v1/...`)
- No viven en `infrastructure/input/rest/`, sino en `controller/`

### 5.2 Repository Adapters (Output Adapters)

**❌ NO CUMPLE**

No hay adaptadores. Los servicios usan `JpaRepository` directamente:

```
ProjectService → ProjectRepository (extends JpaRepository<Project, UUID>)
                 ProgramRepository (extends JpaRepository<Program, UUID>)
                 UserRepository    (extends JpaRepository<User, UUID>)
```

**Lo esperado:**
```
ProjectService → ProyectoRepositoryPort (interfaz en domain/port/output/)
                     ↑ implementado por
ProyectoRepositoryAdapter (en infrastructure/output/persistence/)
    → usa ProyectoJpaRepository (extends JpaRepository)
    → usa ProyectoMapper (JpaEntity ↔ Domain)
```

### 5.3 Cross-module Adapters (Anti-corrupción)

**❌ NO CUMPLE**

Hay dependencias directas entre lo que serían módulos distintos:

- `ProjectService` importa directamente `Program`, `User`, `ProgramRepository`, `UserRepository`
- `TaskService` importa directamente `Activity`, `User`, `Project`, `UserRole`, `ActivityRepository`, `UserRepository`
- `StructureService` importa directamente entidades y repositorios de 4 "módulos": strategic_lines, programs, projects, activities

No hay capa anti-corrupción. Si `tareas` necesita datos de `proyectos`, lo importa directo.

---

## 6. Cross-Cutting Concerns

### 6.1 Manejo de excepciones

**⚠️ PARCIAL**

`GlobalExceptionHandler` (`@RestControllerAdvice`) **existe** y maneja 10 tipos de excepciones:

| Excepción | HTTP Status | ¿Bien mapeada? |
|-----------|-------------|-----------------|
| `BadCredentialsAppException` | 401 UNAUTHORIZED | ✅ |
| `AccountLockedException` | 423 LOCKED | ✅ |
| `AccountInactiveException` | 403 FORBIDDEN | ✅ |
| `DuplicateEmailException` | 409 CONFLICT | ✅ |
| `CannotInactivateDirectorException` | 409 CONFLICT | ✅ |
| `InvalidTokenException` | 400 BAD_REQUEST | ✅ |
| `UserNotFoundException` | 404 NOT_FOUND | ✅ |
| `ResourceNotFoundException` | 404 NOT_FOUND | ✅ |
| `DuplicateNameException` | 409 CONFLICT | ✅ |
| `InvalidCoordinatorRoleException` | 400 BAD_REQUEST | ✅ |
| `MethodArgumentNotValidException` | 400 BAD_REQUEST | ✅ |
| `AccessDeniedException` | 403 FORBIDDEN | ✅ |

**Problemas:**
- `ApiError` es demasiado simple: `record ApiError(String message) {}` — solo tiene `message`, falta:
  - `code` (código de error de aplicación)
  - `timestamp`
  - `path` (el endpoint que falló)
- No captura `Exception.class` genérica como fallback (stack trace puede exponerse en errores no previstos)
- `UserNotFoundException` y `ResourceNotFoundException` son redundantes — ambas retornan 404

### 6.2 Trace ID

**❌ NO CUMPLE**

No existe filtro de `X-Trace-Id`. No hay ningún mecanismo de trazabilidad por request.

### 6.3 Configuración de seguridad

**✅ CUMPLE**

`SecurityConfig.java`:
- CORS configurado (origen: `http://localhost:5173`, métodos: GET/POST/PUT/PATCH/DELETE/OPTIONS)
- CSRF deshabilitado (correcto para API REST stateless)
- Sesiones stateless
- JWT filter antes del `UsernamePasswordAuthenticationFilter`
- `/api/auth/**` público, todo lo demás autenticado
- `@EnableMethodSecurity(prePostEnabled = true)` para `@PreAuthorize`

**Observación:** CORS hardcodeado a `localhost:5173`. En producción necesitará el dominio real.

### 6.4 Documentación API

**❌ NO CUMPLE**

- No hay dependencia de SpringDoc/OpenAPI en `pom.xml`
- No hay configuración de Swagger UI
- No se puede acceder a documentación interactiva de la API

---

## 7. Persistencia y Migraciones

### 7.1 Flyway

**✅ CUMPLE**

Flyway está configurado con 4 migraciones que siguen la convención correcta:

| Migración | Descripción |
|-----------|-------------|
| `V1__create_users_table.sql` | Tabla `users` + `password_reset_tokens` + índices |
| `V2__create_pac_structure.sql` | Tablas `strategic_lines`, `programs`, `projects`, `project_coordinators`, `sdg_indicators`, `activities`, `activity_sdg_indicators` + índices |
| `V3__seed_sdg_indicators.sql` | Datos semilla de los 17 ODS |
| `V4__create_tasks.sql` | Tablas `tasks` + `task_reschedules` + índices |

**Bien hecho:** Todas usan `UUID` como PK, tienen `CHECK` constraints en los enums, y definen índices para FKs.

### 7.2 Base de datos

**⚠️ PARCIAL**

- PostgreSQL configurado en `application.yml` ✅
- `ddl-auto: validate` (buena práctica con Flyway) ✅
- `open-in-view: false` (evita lazy loading fuera de transacciones) ✅
- **NO hay perfil para H2 (tests)** ❌ — No hay `application-test.yml` ni dependencia H2 en pom.xml

---

## 8. Testing

### 8.1 Cobertura por capa

**❌ NO CUMPLE**

**No existe la carpeta `src/test/`.** Cero clases de test.

| Capa | Tests |
|------|-------|
| Dominio (modelos, validaciones) | 0 |
| Servicios (unit tests) | 0 |
| Controllers (`@WebMvcTest`) | 0 |
| Repositories (`@DataJpaTest`) | 0 |
| Adapters | 0 |
| Integración | 0 |
| **Total** | **0** |

Las dependencias de test **sí están** en `pom.xml`:
- `spring-boot-starter-test`
- `spring-security-test`

Pero no se usan.

### 8.2 Calidad de tests

**🔲 NO APLICA** — No hay tests que evaluar.

---

## 9. Build y Deploy

### 9.1 Maven/Gradle

**⚠️ PARCIAL**

- Usa **Maven** con `pom.xml` ✅
- Spring Boot 3.3.2 con Java 17 ✅
- **No tiene Maven Wrapper (`mvnw`)** ❌ — depende de instalación local de Maven

### 9.2 Docker

**❌ NO CUMPLE**

- No existe `Dockerfile`
- No existe `docker-compose.yml`

### 9.3 CI

**❌ NO CUMPLE**

- No existe `.github/actions/` ni ninguna configuración de CI
- No hay pipeline de build, test o deploy automatizado

---

## 10. Resumen Ejecutivo

| Categoría | Estado | Observación |
|-----------|--------|-------------|
| Estructura hexagonal | ❌ | Organizado por capas técnicas, no por bounded contexts |
| Modelos de dominio puros | ❌ | Todos los modelos son entidades JPA con `@Entity`, `@Column`, etc. |
| Puertos definidos | ❌ | No existe ninguna interfaz de input port ni output port |
| Servicios bien acoplados | ❌ | Dependen directamente de JpaRepository, sin interfaces |
| Controllers como adapters | ⚠️ | Son `@RestController` pero dependen de clases concretas, sin versionado |
| Repository adapters | ❌ | No hay adaptadores — se usa JpaRepository directamente |
| Cross-module anti-corrupción | ❌ | Dependencias directas entre "módulos" sin capa de anti-corrupción |
| Excepciones centralizadas | ⚠️ | Handler existe pero `ApiError` muy simple, falta fallback genérico |
| Testing | ❌ | 0 tests. Carpeta `src/test/` vacía |
| Flyway + BD dual | ⚠️ | Flyway OK con 4 migraciones. No hay perfil H2 para tests |
| Docker + CI | ❌ | No existe Dockerfile, docker-compose, ni pipeline CI |

---

## Top 5 — Correcciones Más Urgentes

### 1. Reorganizar por bounded contexts (impacto: ALTO)
Mover las clases a paquetes por módulo de negocio. Empezar con los módulos que ya tienen código completo:
```
sisepp/
├── auth/         → AuthController, AuthService, JwtAuthFilter, JwtService, SecurityConfig
├── usuarios/     → UserController, UserService, User, UserRole, UserStatus
├── proyectos/    → ProjectController, ProjectService, Project, Program, StrategicLine
├── actividades/  → ActivityController, ActivityService, Activity, SdgIndicator
├── tareas/       → TaskController, TaskService, Task, TaskReschedule
└── shared/       → GlobalExceptionHandler, ApiError, DataSeeder
```

### 2. Separar modelos de dominio de entidades JPA (impacto: ALTO)
Para cada entidad:
1. Crear modelo de dominio puro (sin anotaciones JPA) en `<modulo>/domain/model/`
2. Mover la entidad JPA actual a `<modulo>/infrastructure/output/persistence/` renombrándola (e.g., `ProjectJpaEntity`)
3. Crear mapper entre ambas

### 3. Definir puertos de entrada y salida (impacto: ALTO)
1. Crear interfaces de caso de uso en `<modulo>/domain/port/input/` (e.g., `CrearProyectoUseCase`)
2. Crear interfaces de repositorio en `<modulo>/domain/port/output/` (e.g., `ProyectoRepositoryPort`)
3. Hacer que los servicios implementen los input ports y dependan solo de output ports
4. Crear adaptadores de persistencia que implementen los output ports

### 4. Escribir tests mínimos (impacto: ALTO)
Priorizar:
1. Tests unitarios de servicios con mocks de los output ports
2. `@WebMvcTest` para validar seguridad de endpoints (roles)
3. `@DataJpaTest` para queries personalizadas de repositorios
4. Configurar perfil `test` con H2 en `application-test.yml`

### 5. Corregir N+1 en StructureService.buildTree() (impacto: MEDIO)
Reemplazar las 4 queries anidadas por un `@Query` con `JOIN FETCH` o una proyección que traiga todo el árbol en 1-2 queries. Igualmente, mover el filtrado de `TaskService.list()` de memoria a SQL (JPA Specification o `@Query`).
