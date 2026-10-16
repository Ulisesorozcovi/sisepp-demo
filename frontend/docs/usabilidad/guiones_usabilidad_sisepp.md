# Guiones de Usabilidad — SISEPP

**Fecha de generacion:** 2026-10-14
**Rama auditada:** `fix/seguridad-permisos`
**Carpetas:** backend / frontend

---

## 1. Resumen ejecutivo

**Estado general: LISTO CON AJUSTES**

El sistema SISEPP tiene un control de permisos robusto a nivel de backend mediante `@PreAuthorize` en controladores y validaciones de ownership en servicios. Las operaciones de escritura (crear, editar, eliminar) estan correctamente restringidas por rol. Sin embargo, se encontraron **3 discrepancias** y **2 hallazgos de seguridad de lectura**:

1. **Critico**: El endpoint `GET /api/v1/tasks/with-progress` no filtra por rol — un TECNICO puede ver tareas de otros.
2. **Critico**: El endpoint `GET /api/v1/follow-ups?taskId=` no filtra por ownership — un TECNICO puede leer avances ajenos.
3. **Medio**: Las vistas de columnas en `/estructura` (frontend) muestran formularios de creacion al COORDINADOR sin restriccion de rol; el backend si lo bloquea.
4. Los tests de integracion (`DashboardScopeIT`, `TaskLifecycleIT`) requieren Docker (Testcontainers) que no esta instalado en el entorno de desarrollo local.
5. Los datos de prueba detallados (proyectos, tareas, avances) dependen de ejecutar manualmente `scripts/seed-demo.sql`.

---

## 2. Tabla de verificacion de permisos

### 2.1 DIRECTOR

| Accion | Esperado | Resultado | Archivo:Linea | Estado |
|--------|----------|-----------|---------------|--------|
| Crear usuarios (POST /api/v1/users) | DENEGADO | DENEGADO | `UserController.java:40` `@PreAuthorize("hasRole('STAFF')")` | OK |
| Listar usuarios (GET /api/v1/users) | PERMITIDO | PERMITIDO | `UserController.java:25` `@PreAuthorize("hasAnyRole('STAFF','DIRECTOR')")` | OK |
| Activar/desactivar usuario | DENEGADO | DENEGADO | `UserController.java:55,61` `@PreAuthorize("hasRole('STAFF')")` | OK |
| Consultar PGAR vigente | PERMITIDO | PERMITIDO | `PlanningInstrumentController.java:27` `hasAnyRole('DIRECTOR','COORDINADOR','STAFF')` | OK |
| Ver conexion PGAR-PAC (estructura) | PERMITIDO | PERMITIDO | `StructureController.java:22` `hasAnyRole('DIRECTOR','COORDINADOR','STAFF')` | OK |
| Crear programa en PAC | PERMITIDO | PERMITIDO | `ProgramController.java:30` `hasAnyRole('DIRECTOR','STAFF')` | OK |
| Registrar linea estrategica | PERMITIDO | PERMITIDO | `StrategicLineController.java:30` `hasAnyRole('DIRECTOR','STAFF')` | OK |
| Crear proyecto POAI | PERMITIDO | PERMITIDO | `ProjectController.java:39` `hasAnyRole('DIRECTOR','STAFF')` | OK |
| Asignar coordinador a proyecto | PERMITIDO | PERMITIDO | `ProjectController.java:51` `hasAnyRole('DIRECTOR','STAFF')` | OK |
| Crear tareas | DENEGADO | DENEGADO | `TaskController.java:50` `hasAnyRole('STAFF','COORDINADOR')` | OK |
| Reprogramar tareas | DENEGADO | DENEGADO | `TaskController.java:57` `hasAnyRole('STAFF','COORDINADOR')` | OK |
| Eliminar tareas | DENEGADO | DENEGADO | `TaskController.java:64` `hasRole('STAFF')` | OK |
| Acceso a /tareas (frontend) | PERMITIDO | PERMITIDO | `App.tsx:84` roles: COORDINADOR, DIRECTOR, STAFF | OK |
| Ver sidebar "PGAR / PAC / POAI" | PERMITIDO | PERMITIDO | `Sidebar.tsx:18` roles: DIRECTOR, STAFF | OK |

### 2.2 COORDINADOR

| Accion | Esperado | Resultado | Archivo:Linea | Estado |
|--------|----------|-----------|---------------|--------|
| Crear tareas (en proyecto asignado) | PERMITIDO | PERMITIDO | `TaskController.java:50` + `TaskService.java:60` ownership check | OK |
| Crear tareas (en proyecto NO asignado) | DENEGADO | DENEGADO | `TaskService.java:157-163` `assertCoordinatorOwnsProject` | OK |
| Reprogramar tarea con motivo | PERMITIDO | PERMITIDO | `TaskController.java:57` + `TaskService.java:129` ownership check | OK |
| Buscar tareas por estado/responsable | PERMITIDO | PERMITIDO | `TaskService.java:94` filtra por `findByCoordinatorId` | OK |
| Registrar avance | PERMITIDO | PERMITIDO | `FollowUpController.java:40` + `FollowUpService.java:239-247` ownership | OK |
| Corregir avance propio | PERMITIDO | PERMITIDO | `FollowUpService.java:77` `validateOwnership` | OK |
| Ver estructura PAC | PERMITIDO | PERMITIDO | `StructureController.java:22` incluye COORDINADOR | OK |
| Asignar tarea a TECNICO | PERMITIDO | PERMITIDO | `TaskService.java:172` acepta TECNICO | OK |
| Asignar tarea a COORDINADOR | PERMITIDO | PERMITIDO | `TaskService.java:172` acepta COORDINADOR | OK |
| Asignar tarea a usuario INACTIVO | DENEGADO | DENEGADO | `TaskService.java:169` "El usuario responsable no esta activo" | OK |
| Asignar tarea a DIRECTOR o STAFF | DENEGADO | DENEGADO | `TaskService.java:172` "debe tener rol Coordinador o Tecnico" | OK |
| Crear programas/lineas (backend) | DENEGADO | DENEGADO | `ProgramController.java:30` solo DIRECTOR,STAFF | OK |
| Crear programas/lineas (frontend columnas) | PERMITIDO | PERMITIDO | `StrategicLinesPanel.tsx:93` sin filtro de rol | DISCREPANCIA |

### 2.3 TECNICO

| Accion | Esperado | Resultado | Archivo:Linea | Estado |
|--------|----------|-----------|---------------|--------|
| Crear tareas | DENEGADO | DENEGADO | `TaskController.java:50` no incluye TECNICO | OK |
| Ver solo tareas propias (list) | SI | SI | `TaskService.java:95` `findByResponsibleUserId` | OK |
| Ver tareas ajenas via with-progress | DENEGADO | PERMITIDO | `TaskController.java:42` `isAuthenticated()` sin filtro | DISCREPANCIA |
| Registrar avance en tarea propia | PERMITIDO | PERMITIDO | `FollowUpService.java:232-236` ownership check | OK |
| Registrar avance en tarea ajena | DENEGADO | DENEGADO | `FollowUpService.java:232-236` `AccessDeniedException` | OK |
| Corregir avance en tarea ajena | DENEGADO | DENEGADO | `FollowUpService.java:77` `validateOwnership` | OK |
| Leer avances de tareas ajenas | DENEGADO | PERMITIDO | `FollowUpController.java:25` `isAuthenticated()` sin filtro | DISCREPANCIA |
| Acceso a /mis-tareas | SI | SI | `App.tsx:92` solo TECNICO | OK |
| Acceso a /tareas | DENEGADO | DENEGADO | `App.tsx:84` no incluye TECNICO | OK |
| Acceso a /estructura | DENEGADO | DENEGADO | `App.tsx:60` no incluye TECNICO | OK |
| Acceso a /usuarios | DENEGADO | DENEGADO | `App.tsx:52` no incluye TECNICO | OK |

### 2.4 STAFF

| Accion | Esperado | Resultado | Archivo:Linea | Estado |
|--------|----------|-----------|---------------|--------|
| Crear usuarios | PERMITIDO | PERMITIDO | `UserController.java:40` `hasRole('STAFF')` | OK |
| Activar/desactivar usuario | PERMITIDO | PERMITIDO | `UserController.java:55,61` `hasRole('STAFF')` | OK |
| Crear tareas | PERMITIDO | PERMITIDO | `TaskController.java:50` incluye STAFF | OK |
| Eliminar tareas | PERMITIDO | PERMITIDO | `TaskController.java:64` `hasRole('STAFF')` | OK |
| Acceso completo a estructura | PERMITIDO | PERMITIDO | Todos los controladores incluyen STAFF | OK |

### 2.5 Resumen de discrepancias

| # | Discrepancia | Severidad | Detalle |
|---|-------------|-----------|---------|
| 1 | `GET /api/v1/tasks/with-progress` sin filtro por rol | ALTA | `TaskService.java:179-199` no filtra por rol. Un TECNICO puede ver tareas de cualquier actividad/proyecto via API directa. |
| 2 | `GET /api/v1/follow-ups?taskId=` sin filtro de ownership | ALTA | `FollowUpService.java:96-98` `listByTask` no valida ownership. Un TECNICO puede leer avances de tareas ajenas. |
| 3 | Vista columnas en `/estructura` muestra formularios al COORDINADOR | MEDIA | `StrategicLinesPanel.tsx:93`, `ProgramsPanel.tsx:126`, `ProjectsPanel.tsx:200`, `ActivitiesPanel.tsx:170` — sin filtro de rol en UI. Backend si bloquea (HTTP 403). |
| 4 | Sidebar vs rutas: DIRECTOR no ve "Catalogo de Tareas" en sidebar pero puede acceder via URL | BAJA | `Sidebar.tsx:20` excluye DIRECTOR, `App.tsx:68` lo incluye. |
| 5 | Sidebar vs rutas: DIRECTOR no ve "Registrar Avance" en sidebar pero puede acceder via URL | BAJA | `Sidebar.tsx:23` excluye DIRECTOR, `App.tsx:108` lo incluye. |

---

## 3. Tabla de datos de prueba

| Registro | Estado | Donde se define | Accion necesaria |
|----------|--------|-----------------|------------------|
| PGAR 2026-2037 vigente | EXISTE (Flyway) | `V13__seed_planning_instruments.sql` | Ninguna |
| Lineas estrategicas del PGAR | DEPENDE DE CARGA MANUAL | `scripts/seed-demo.sql:91-103` | Ejecutar `seed-demo.sql` |
| PAC 2024-2027 | EXISTE (Flyway) | `V13__seed_planning_instruments.sql` | Ninguna |
| Programas vinculados al PAC | DEPENDE DE CARGA MANUAL | `scripts/seed-demo.sql:108-128` | Ejecutar `seed-demo.sql` |
| Proyecto "Reforestacion Uraba 2026" | NO EXISTE | — | Crear manualmente o ajustar seed |
| Proyecto "Monitoreo de Aguas" (con coordinador) | NO EXISTE (hay similar) | `seed-demo.sql:146` "Monitoreo de la calidad del agua..." | Ejecutar `seed-demo.sql` (nombre difiere) |
| Usuario "Juan Perez" COORDINADOR ACTIVO | NO EXISTE | — | Usar "Tomas Gomez Restrepo" o "Laura Mendoza Perez" del seed |
| Usuario "Carlos Ruiz" TECNICO ACTIVO | NO EXISTE | — | Usar "Andres Cuesta Mosquera" o "Diana Palacios Renteria" del seed |
| Usuarios de prueba por rol (Flyway) | EXISTE | V16 y V26 | Ninguna |
| — Director de prueba | `director@example.com` | `V26` | Ninguna |
| — Coordinador de prueba | `coordinador@example.com` | `V16` | Ninguna |
| — Tecnico de prueba | `tecnico@example.com` | `V16` | Ninguna |
| — Staff de prueba | `staff@example.com` | `V26` | Ninguna |
| Proyecto con tareas y fechas distintas | DEPENDE DE CARGA MANUAL | `seed-demo.sql` multiples proyectos | Ejecutar `seed-demo.sql` |
| Tareas con metas numericas (ej. "24 Monitoreos") | DEPENDE DE CARGA MANUAL | `seed-demo.sql:378` | Ejecutar `seed-demo.sql` |
| Avances (follow-ups) registrados | DEPENDE DE CARGA MANUAL | `seed-demo.sql:471-544` | Ejecutar `seed-demo.sql` |
| Contrasena "12345" como prueba | RECHAZADA por politica | `ResetPasswordRequest.java:12` exige mayuscula+minuscula+digito+especial | No aplica; la contrasena del seed la define SISEPP_SEED_PASSWORD |

### Contrasenas de prueba (Flyway)

Las semillas V16 y V26 (perfil `dev`) insertan hashes BCrypt directamente via `pgcrypto`. Su contrasena es la definida al arrancar:

| Rol | Email | Contrasena |
|-----|-------|------------|
| DIRECTOR | `director@example.com` | `[clave_prueba]` |
| COORDINADOR | `coordinador@example.com` | `[clave_prueba]` |
| TECNICO | `tecnico@example.com` | `[clave_prueba]` |
| STAFF | `staff@example.com` | `[clave_prueba]` |

La contrasena comun es la definida en `SISEPP_SEED_PASSWORD` (cumple la politica de complejidad vigente).

---

## 4. Reglas de negocio

| Regla | Validacion backend | Mensaje de error | Frontend muestra mensaje | Estado |
|-------|-------------------|------------------|--------------------------|--------|
| Suma pesos proyectos por programa <= 100% | `ProjectService.java:124-137` `WeightExceededException` | "La ponderacion total de los proyectos seria X%, excediendo el maximo de 100%. Peso disponible: Y%" | Si, via HTTP 422 code `WEIGHT_EXCEEDED` | OK (nota: valida que no exceda, no que sume exactamente 100%) |
| Suma pesos actividades por proyecto <= 100% | `ActivityService.java:115-128` `WeightExceededException` | Mismo formato, "actividades" | Si, via HTTP 422 | OK (misma nota) |
| Suma pesos programas por linea <= 100% | `ProgramService.java:67-78` `WeightExceededException` | Mismo formato, "programas" | Si, via HTTP 422 | OK |
| Tarea sin responsable | `CreateTaskRequest.java:16` `@NotNull` | "Debe seleccionar un usuario responsable" | Si, browser `required` + HTTP 400 | OK |
| Tarea sin meta fisica | `CreateTaskRequest.java:12` `@NotNull @Positive` | "La meta fisica es obligatoria" / "debe ser mayor a cero" | Si, browser `required` + HTTP 400 | OK |
| Tarea sin fecha limite | `CreateTaskRequest.java:14` `@NotNull @Future` | "La fecha limite es obligatoria" / "debe ser posterior a hoy" | Si, browser `required` + HTTP 400 | OK |
| Asignar responsable INACTIVO | `TaskService.java:169` | "El usuario responsable no esta activo" | Si, via HTTP 400 code `INVALID_ARGUMENT` | OK |
| Asignar responsable con rol invalido | `TaskService.java:172` | "El usuario responsable debe tener rol Coordinador o Tecnico" | Si, via HTTP 400 | OK |
| Ordenamiento tareas TECNICO | `TaskService.java:115` `.sorted(comparing dueDate)` | — | Ascendente por fecha limite | OK |
| Reporte duplicado mismo mes/anio | `FollowUpService.java` unique constraint | "Ya existe un registro de avance para esta tarea en el periodo seleccionado." | Si, via HTTP 409 | OK |
| Historial de avances con autor y fecha | `follow_ups` tabla con `reported_by`, `created_at` | — | `FollowUpResponse` incluye `reportedByName` | OK |
| Historial de reprogramaciones | `task_reschedules` tabla con `changed_by_user_id`, `changed_at`, `justification` | — | Disponible via API | OK |
| Auditoria general de cambios en campos | NO EXISTE | — | — | PENDIENTE (no hay audit trail general) |

---

## 5. Guiones corregidos por rol

### Precondiciones globales

1. Ejecutar `scripts/seed-demo.sql` contra la base de datos para contar con datos completos.
2. Servidor backend corriendo en `http://localhost:8081`.
3. Frontend corriendo en `http://localhost:5173`.
4. Usuarios de prueba disponibles (ver seccion 3).

---

### ROL: DIRECTOR

**Usuario de prueba:** `director@example.com` / `[clave_prueba]`

#### Mision D1: Consultar el PGAR y la estructura del PAC

**Objetivo:** Verificar que el DIRECTOR puede navegar el arbol estrategico completo.
**Actividades del formulario:** B1 (Navegacion), C1 (Comprension de informacion)

**Pasos:**
1. Iniciar sesion como Director.
2. En el sidebar, hacer clic en "PGAR / PAC / POAI".
3. Verificar que se muestra el PGAR vigente 2026-2037.
4. Navegar a la vista de estructura (sidebar > "Estructura PAC").
5. Verificar que se ven programas, proyectos y actividades en la vista arbol.
6. Expandir un programa y verificar que se ven sus proyectos.

**Precondiciones de datos:** PGAR y PAC creados por Flyway (V13). Programas/proyectos requieren `seed-demo.sql`.

---

#### Mision D2: Crear un programa en el PAC

**Objetivo:** Verificar que el DIRECTOR puede crear estructura del PAC.
**Actividades del formulario:** B2 (Creacion de registro), C2 (Formulario), D1 (Eficiencia)

**Pasos:**
1. Iniciar sesion como Director.
2. Ir a "PGAR / PAC / POAI" o "Estructura PAC".
3. Seleccionar una linea estrategica existente.
4. Hacer clic en "Nuevo programa" (o boton equivalente).
5. Llenar codigo, nombre y ponderacion.
6. Guardar. Verificar que aparece en la lista.

**Precondiciones de datos:** Al menos una linea estrategica existente (requiere `seed-demo.sql`).

---

#### Mision D3: Crear un proyecto POAI y asignar coordinador

**Objetivo:** Verificar creacion de proyecto y asignacion de coordinador.
**Actividades del formulario:** B2 (Creacion), B3 (Asignacion), C2 (Formulario), D2 (Satisfaccion)

**Pasos:**
1. Iniciar sesion como Director.
2. Ir a "Estructura PAC".
3. Seleccionar un programa existente.
4. Crear un nuevo proyecto: codigo, nombre, vigencia, ponderacion.
5. Guardar el proyecto.
6. Asignar un coordinador al proyecto (debe aparecer solo usuarios con rol COORDINADOR activos).
7. Verificar que el coordinador quedo asignado.

**Precondiciones de datos:** Programa existente y al menos un usuario COORDINADOR activo.

---

#### Mision D4 (NEGATIVA): Intentar crear una tarea como DIRECTOR

**Objetivo:** Confirmar que el DIRECTOR NO puede crear tareas.
**Actividades del formulario:** Prueba de permisos / validacion

**Pasos:**
1. Iniciar sesion como Director.
2. Navegar a `/tareas` (via URL directa si no aparece en sidebar).
3. Verificar que NO hay boton "Programar tarea" o que al intentar crear recibe un error 403.
4. (Via API) Enviar `POST /api/v1/tasks` con token de DIRECTOR. Verificar respuesta 403 Forbidden.

**Resultado esperado:** HTTP 403. El DIRECTOR no puede crear tareas por diseno.

---

#### Mision D5 (NEGATIVA): Intentar crear un usuario como DIRECTOR

**Objetivo:** Confirmar que el DIRECTOR NO puede crear usuarios.
**Actividades del formulario:** Prueba de permisos / validacion

**Pasos:**
1. Iniciar sesion como Director.
2. Ir a `/usuarios`.
3. Hacer clic en "Nuevo usuario".
4. Llenar el formulario y enviar.
5. Verificar que recibe un error 403 del backend.

**Resultado esperado:** HTTP 403. Solo STAFF puede crear usuarios.

**Nota:** El frontend SI muestra el boton "Nuevo usuario" al DIRECTOR (no hay filtro de rol en `UsersPage.tsx:18`), pero el backend bloquea la operacion. Esto es una inconsistencia de UX.

---

### ROL: COORDINADOR

**Usuario de prueba:** `coordinador@example.com` / `[clave_prueba]`

#### Mision C1: Crear una tarea en un proyecto asignado

**Objetivo:** Verificar que el COORDINADOR puede programar tareas en sus proyectos.
**Actividades del formulario:** B2 (Creacion), C2 (Formulario), D1 (Eficiencia)

**Pasos:**
1. Iniciar sesion como Coordinador.
2. Ir a "Cronograma" o "Tareas" en el sidebar.
3. Hacer clic en "Programar tarea".
4. Seleccionar una actividad de un proyecto asignado.
5. Llenar: nombre, meta fisica (valor y unidad), fecha limite, responsable.
6. Verificar que el selector de responsables muestra solo usuarios TECNICO y COORDINADOR activos.
7. Seleccionar un responsable y guardar.
8. Verificar que la tarea aparece en la lista con estado PROGRAMADA.

**Precondiciones de datos:** El coordinador de prueba debe tener al menos un proyecto asignado (`seed-demo.sql` o crear asignacion manual).

---

#### Mision C2: Reprogramar una tarea con justificacion

**Objetivo:** Verificar reprogramacion con registro de motivo.
**Actividades del formulario:** B4 (Edicion), C3 (Mensaje de confirmacion), D1 (Eficiencia)

**Pasos:**
1. Iniciar sesion como Coordinador.
2. Ir a "Tareas".
3. Seleccionar una tarea existente.
4. Hacer clic en "Reprogramar".
5. Ingresar nueva fecha y justificacion (minimo 10 caracteres).
6. Guardar. Verificar que la fecha se actualizo.
7. (Opcional) Verificar via API que existe un registro en `task_reschedules`.

**Precondiciones de datos:** Al menos una tarea existente en proyecto del coordinador.

---

#### Mision C3: Registrar un avance

**Objetivo:** Verificar registro de avance y cambio de estado de tarea.
**Actividades del formulario:** B2 (Creacion), C2 (Formulario), E1 (Avance automatico)

**Pasos:**
1. Iniciar sesion como Coordinador.
2. Ir a "Registrar Avance" o "Seguimiento".
3. Seleccionar proyecto > actividad > tarea.
4. Registrar un avance: mes, anio, valor del avance, URL de evidencia (opcional), observaciones.
5. Guardar. Verificar que el avance aparece en el historial.
6. Verificar que el estado de la tarea cambio (ej. de PROGRAMADA a EN_EJECUCION).

**Precondiciones de datos:** Al menos una tarea con estado PROGRAMADA.

---

#### Mision C4 (NEGATIVA): Crear tarea en proyecto NO asignado

**Objetivo:** Verificar que el COORDINADOR no puede crear tareas en proyectos de otros.
**Actividades del formulario:** Prueba de permisos / validacion

**Pasos:**
1. Iniciar sesion como Coordinador.
2. (Via API) Enviar `POST /api/v1/tasks` con `activityId` de un proyecto NO asignado al coordinador.
3. Verificar respuesta 403 Forbidden con mensaje de denegacion.

**Resultado esperado:** HTTP 403. El backend valida ownership del proyecto.

---

#### Mision C5 (NEGATIVA): Asignar tarea a usuario INACTIVO

**Objetivo:** Verificar validacion de usuario responsable.
**Actividades del formulario:** Prueba de permisos / validacion

**Pasos:**
1. Iniciar sesion como Coordinador.
2. Ir a "Programar tarea".
3. Verificar que el selector de responsables (`/users/assignable`) NO muestra usuarios inactivos.
4. (Via API) Enviar `POST /api/v1/tasks` con `responsibleUserId` de un usuario INACTIVO.
5. Verificar respuesta 400: "El usuario responsable no esta activo".

**Resultado esperado:** HTTP 400 con mensaje claro.

---

#### Mision C6 (NEGATIVA): Guardar pesos que excedan 100%

**Objetivo:** Verificar validacion de ponderaciones.
**Actividades del formulario:** Prueba de validacion

**Pasos:**
1. Iniciar sesion como Coordinador (o Director/Staff).
2. Intentar crear un proyecto con ponderacion que haga que la suma exceda 100% en su programa.
3. Verificar respuesta HTTP 422 con mensaje: "La ponderacion total de los proyectos seria X%, excediendo el maximo de 100%. Peso disponible: Y%".
4. Repetir con actividades en un proyecto.

**Resultado esperado:** HTTP 422 `WEIGHT_EXCEEDED` con mensaje descriptivo.

---

### ROL: TECNICO

**Usuario de prueba:** `tecnico@example.com` / `[clave_prueba]`

#### Mision T1: Consultar mis tareas asignadas

**Objetivo:** Verificar que el TECNICO ve solo sus tareas.
**Actividades del formulario:** B1 (Navegacion), C1 (Comprension), D1 (Eficiencia)

**Pasos:**
1. Iniciar sesion como Tecnico.
2. En el sidebar, hacer clic en "Mis Tareas".
3. Verificar que se muestra una lista de tareas.
4. Verificar que todas las tareas listadas estan asignadas al tecnico logueado.
5. Verificar que estan ordenadas por fecha limite (ascendente, la mas proxima primero).

**Precondiciones de datos:** Ejecutar `seed-demo.sql` para tener tareas asignadas al tecnico.

---

#### Mision T2: Registrar un avance en mi tarea

**Objetivo:** Verificar que el TECNICO puede reportar avance en su tarea.
**Actividades del formulario:** B2 (Creacion), C2 (Formulario), E1 (Avance automatico)

**Pasos:**
1. Iniciar sesion como Tecnico.
2. Ir a "Registrar Avance".
3. Seleccionar una tarea propia de la lista.
4. Llenar: mes, anio, valor del avance, enlace de evidencia, observaciones.
5. Guardar. Verificar confirmacion.
6. Verificar que el avance aparece en el historial con su nombre como autor.

**Precondiciones de datos:** Al menos una tarea asignada al tecnico con meta numerica.

---

#### Mision T3: Completar una tarea al 100%

**Objetivo:** Verificar que al alcanzar la meta la tarea cambia a COMPLETADA.
**Actividades del formulario:** E1 (Avance automatico), E2 (Estado de tarea)

**Pasos:**
1. Iniciar sesion como Tecnico.
2. Registrar un avance que iguale o supere la meta de la tarea (ej. meta = 100, avance acumulado = 100).
3. Guardar. Verificar que el estado de la tarea cambio a COMPLETADA.
4. Verificar que `completed_at` se registro.

**Precondiciones de datos:** Tarea con meta definida y avance parcial o sin avance.

---

#### Mision T4 (NEGATIVA): Intentar crear una tarea como TECNICO

**Objetivo:** Confirmar que el TECNICO NO puede crear tareas.
**Actividades del formulario:** Prueba de permisos / validacion

**Pasos:**
1. Iniciar sesion como Tecnico.
2. Verificar que en el sidebar NO aparece "Tareas" ni "Cronograma".
3. Navegar directamente a `/tareas`. Verificar redireccion a pagina de inicio.
4. (Via API) Enviar `POST /api/v1/tasks` con token de TECNICO. Verificar respuesta 403.

**Resultado esperado:** Ruta bloqueada en frontend + HTTP 403 en backend.

---

#### Mision T5 (NEGATIVA): Intentar registrar avance en tarea ajena

**Objetivo:** Verificar que el TECNICO no puede reportar avance en tareas de otros.
**Actividades del formulario:** Prueba de permisos / validacion

**Pasos:**
1. Iniciar sesion como Tecnico.
2. (Via API) Enviar `POST /api/v1/follow-ups` con `taskId` de una tarea NO asignada al tecnico.
3. Verificar respuesta 403: "Su rol no permite modificar reportes de avance."

**Resultado esperado:** HTTP 403 `AccessDeniedException`.

---

### ROL: STAFF

**Usuario de prueba:** `staff@example.com` / `[clave_prueba]`

#### Mision S1: Crear un usuario nuevo

**Objetivo:** Verificar creacion de usuario.
**Actividades del formulario:** B2 (Creacion), C2 (Formulario), F1 (Gestion de usuarios)

**Pasos:**
1. Iniciar sesion como Staff.
2. Ir a "Usuarios" en el sidebar.
3. Hacer clic en "Nuevo usuario".
4. Llenar: nombre completo, email, rol (seleccionar TECNICO).
5. Guardar. Verificar que el usuario aparece en la lista con estado ACTIVO.

**Precondiciones de datos:** Ninguna especial.

---

#### Mision S2: Desactivar y reactivar un usuario

**Objetivo:** Verificar control de estado de usuarios.
**Actividades del formulario:** F2 (Gestion de estado), C3 (Confirmacion)

**Pasos:**
1. Iniciar sesion como Staff.
2. Ir a "Usuarios".
3. Localizar un usuario ACTIVO (que no sea DIRECTOR).
4. Hacer clic en "Inactivar". Confirmar.
5. Verificar que el estado cambio a INACTIVO.
6. Hacer clic en "Activar". Confirmar.
7. Verificar que el estado volvio a ACTIVO.

**Nota:** No se puede inactivar a un DIRECTOR (`UsersTable.tsx:123-126`).

---

#### Mision S3: Eliminar una tarea

**Objetivo:** Verificar que solo STAFF puede eliminar tareas.
**Actividades del formulario:** F3 (Eliminacion), C3 (Confirmacion)

**Pasos:**
1. Iniciar sesion como Staff.
2. Ir a "Tareas".
3. Verificar que aparece el boton "Eliminar" en las filas de tareas.
4. Eliminar una tarea. Confirmar.
5. Verificar que la tarea desaparecio de la lista.

**Precondiciones de datos:** Al menos una tarea existente.

---

## 6. Cobertura del formulario

| Seccion | Item | Tiene mision | Mision |
|---------|------|-------------|--------|
| B1 | Navegacion general | SI | D1, T1 |
| B2 | Creacion de registro | SI | D2, D3, C1, C3, T2, S1 |
| B3 | Asignacion de recurso | SI | D3 |
| B4 | Edicion de registro | SI | C2 |
| C1 | Comprension de informacion | SI | D1, T1 |
| C2 | Formularios | SI | D2, C1, C3, T2, S1 |
| C3 | Mensajes de confirmacion/error | SI | C2, S2, S3, C6 |
| D1 | Eficiencia (tiempo en tarea) | SI | D2, C1, T1 |
| D2 | Satisfaccion | SI | D3 |
| E1 | Avance automatico | SI | C3, T2, T3 |
| E2 | Estado de tarea | SI | T3 |
| F1 | Gestion de usuarios (crear) | SI | S1 |
| F2 | Gestion de usuarios (estado) | SI | S2 |
| F3 | Eliminacion | SI | S3 |
| G1 | Reportes y consolidados | NO | — |
| G2 | Financiero | NO | — |

**Items sin mision:**
- **G1 (Reportes):** Las paginas `/reportes` y `/reporte-consolidado` existen pero no fueron cubiertas en guiones. Se recomienda agregar misiones de consulta de reportes.
- **G2 (Financiero):** La pagina `/financiero` existe con creacion/eliminacion de registros. Se recomienda agregar misiones de gestion financiera.

---

## 7. Hallazgos de seguridad

| # | Hallazgo | Severidad | Detalle | Accion recomendada |
|---|----------|-----------|---------|-------------------|
| 1 | Fuga de datos: `GET /api/v1/tasks/with-progress` sin filtro por rol | ALTA | `TaskService.java:179-199` — Un TECNICO puede llamar al endpoint con cualquier `activityId` o `projectId` y ver tareas de otros usuarios. Solo requiere autenticacion, no verifica ownership. | Agregar filtro de rol/ownership en `listWithProgress()` similar al de `list()`. |
| 2 | Fuga de datos: `GET /api/v1/follow-ups?taskId=` sin filtro de ownership | ALTA | `FollowUpService.java:96-98` — Cualquier usuario autenticado puede leer los avances de cualquier tarea. Las operaciones de escritura SI validan ownership, pero la lectura no. | Agregar `validateOwnership` en `listByTask()` o filtrar por rol. |
| 3 | Frontend muestra formularios de creacion al COORDINADOR en `/estructura` (vista columnas) | MEDIA | `StrategicLinesPanel.tsx:93`, `ProgramsPanel.tsx:126`, `ProjectsPanel.tsx:200`, `ActivitiesPanel.tsx:170` — El COORDINADOR ve formularios de crear/editar pero el backend devuelve 403. Confunde al usuario. | Agregar `canEdit(role)` check en paneles de columnas, igual que en `DetailPanel.tsx:40-42`. |
| 4 | Frontend permite STAFF crear usuario con rol DIRECTOR | MEDIA | `UserFormModal.tsx:90-94` — El dropdown de roles muestra las 4 opciones sin restriccion. El backend lo acepta (no valida quien puede crear que rol). | Evaluar si STAFF deberia poder crear DIRECTOR. Si no, agregar validacion en backend. |
| 5 | Inconsistencia sidebar/rutas para DIRECTOR | BAJA | "Catalogo de Tareas" y "Registrar Avance" no aparecen en sidebar para DIRECTOR pero si son accesibles via URL directa. | Alinear sidebar con rutas o viceversa. |

---

## 8. Pendientes para el equipo

### Pendientes criticos

1. **Ejecutar tests de integracion** (`DashboardScopeIT`, `TaskLifecycleIT`): Requieren Docker para Testcontainers. No se pudieron ejecutar en el entorno local actual. Se corrigio un error de compilacion en `TaskLifecycleIT` (metodos `update` y `delete` de `FollowUpUseCase` ahora exigen `requesterEmail`). **Accion:** Instalar Docker Desktop o correr en CI (GitHub Actions).

2. **Corregir fuga de datos en endpoints de lectura** (hallazgos #1 y #2): Agregar filtro de rol en `TaskService.listWithProgress()` y `FollowUpService.listByTask()` para que el TECNICO solo vea datos de sus tareas.

3. **Verificar en ejecucion** que:
   - El DIRECTOR puede crear una tarea desde la UI y que recibe error (mision D4).
   - El selector de responsables queda vacio para roles no permitidos.
   - Los textos reales de error coinciden con los documentados.
   - El TECNICO no puede editar avances de otros via la UI (403 del backend se muestra correctamente).

### Pendientes menores

4. **Datos de prueba**: Los nombres "Juan Perez" y "Carlos Ruiz" no existen en el seed. Usar los nombres reales del `seed-demo.sql` o crear registros especificos.

5. **Guiones para reportes y financiero** (secciones G1, G2): No se generaron guiones para estas secciones. Se recomienda agregarlos.

6. **Auditoria general**: No existe un sistema de audit trail para cambios arbitrarios en campos de entidades. Solo se auditan avances (`follow_ups`) y reprogramaciones (`task_reschedules`). Evaluar si se necesita `django-simple-history` equivalente (Hibernate Envers o similar).

7. **Tests compilados pero no ejecutados**: El fix en `TaskLifecycleIT.java` (agregar `COORDINADOR` como tercer parametro a `update` y `delete`) necesita ser commiteado y verificado con Docker.
