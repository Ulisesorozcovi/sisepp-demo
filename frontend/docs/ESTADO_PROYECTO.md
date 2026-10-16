# SISEPP Frontend — Estado del Proyecto

**Fecha:** 2026-10-05
**Stack:** React 18.3 + TypeScript 5.5 (strict) + Vite 5.4 + Axios 1.7 + React Router 6.26
**Rama activa:** `main`
**Puerto dev:** 5173
**API base:** `http://localhost:8081/api/v1` (configurable via `VITE_API_BASE_URL`)

---

## 1. Resumen de lo realizado

### Arquitectura y patrones

- **Atomic Design:** atoms → molecules → organisms → templates → pages
- **CSS vanilla** con design tokens en `tokens.css` (sin Tailwind/Bootstrap)
- **4 dependencias runtime:** React, React DOM, React Router DOM, Axios
- **Auth:** JWT Bearer en localStorage (`sisepp_token`, `sisepp_user`)
- **RBAC:** 4 roles (DIRECTOR, STAFF, COORDINADOR, TECNICO) con control en rutas y sidebar

### Inventario de páginas (14)

| Página | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| LoginPage | `/login` | Público | Formulario de autenticación |
| HomePage | `/` | Todos | Dashboard con tarjetas de navegación por rol |
| PlanningInstrumentsPage | `/instrumentos` | DIRECTOR, STAFF | CRUD PGAR/PAC/POAI |
| StructurePage | `/estructura` | DIRECTOR, STAFF, COORD | Árbol PAC con CRUD inline + DetailPanel con metas/presupuestos editables |
| TaskCatalogPage | `/catalogo-tareas` | COORD, DIRECTOR, STAFF | Catálogo de tareas agrupadas por actividad |
| SchedulePage | `/cronograma` | COORD, DIRECTOR, STAFF | Cronograma de tareas con fechas y metas |
| TasksPage | `/tareas` | COORD, DIRECTOR, STAFF | Gestión de tareas (sin entrada en sidebar) |
| FollowUpsPage | `/seguimiento` | COORD, DIRECTOR, STAFF | Tabla de registros de seguimiento |
| FollowUpFormPage | `/registrar-avance` | Todos | Formulario de registro de avance físico con detección de duplicados |
| MyTasksPage | `/mis-tareas` | TECNICO | Tareas asignadas con historial |
| GoalTrackingPage | `/consulta-metas` | COORD, DIRECTOR, STAFF | Balance comparativo meta vs ejecutado |
| FinancialPage | `/financiero` | DIRECTOR, STAFF, COORD | Ejecución financiera |
| ReportsPage | `/reportes` | DIRECTOR, STAFF, COORD | Matriz PAC con tabs Metas/Recursos + exportación CSV |
| UsersPage | `/usuarios` | DIRECTOR, STAFF | Administración de usuarios |

### Componentes del design system

| Capa | Cantidad | Componentes |
|------|----------|-------------|
| **Atoms** | 10 | Avatar, Badge, Button, Icon, Input, Label, LevelIcon, Logo, Select, StatusBadge |
| **Molecules** | 8 | Alert, FollowUpHistoryItem, FormField, PasswordField, SdgCheckboxGroup, TreeRow, TreeToolbar, UserCell |
| **Organisms** | 19 | ActivitiesPanel, DetailPanel, FinancialFormModal, FinancialTable, FollowUpFormModal, FollowUpsTable, LoginForm, MyTasksList, Navbar, PacTreeTable, ProgramsPanel, ProjectsPanel, RescheduleModal, Sidebar, StrategicLinesPanel, TaskFormModal, TasksTable, UserFormModal, UsersTable |
| **Templates** | 2 | AuthLayout, DashboardLayout |

### Capa API (10 módulos)

| Módulo | Endpoints |
|--------|-----------|
| authApi | login, forgot-password, reset-password |
| userApi | list, create, update, inactivate, activate |
| planningApi | list, getById, create, update |
| structureApi | strategic-lines, programs, projects, activities (list, getById, create, update con annualGoals/annualBudgets), sdg-indicators, tree, weight-summary, coordinators |
| taskApi | list, create, reschedule, delete, listWithProgress |
| followUpApi | list, create, update, remove |
| financialApi | list, create, update, remove |
| catalogApi | measurement-units, public-investment-programs, thematic-categories, management-indicators |
| reportApi | pac-matrix |
| bulkApi | pac-structure |

### Sidebar (12 entradas)

| Orden | Ruta | Label | Roles |
|-------|------|-------|-------|
| 1 | `/` | Inicio | Todos |
| 2 | `/instrumentos` | PGAR / PAC / POAI | DIRECTOR, STAFF |
| 3 | `/estructura` | Estructura PAC | DIRECTOR, STAFF, COORD |
| 4 | `/catalogo-tareas` | Catálogo de Tareas | COORD, DIRECTOR, STAFF |
| 5 | `/cronograma` | Cronograma | COORD, DIRECTOR, STAFF |
| 6 | `/seguimiento` | Registro de Avance | COORD, DIRECTOR, STAFF |
| 7 | `/registrar-avance` | Registrar Avance | Todos |
| 8 | `/mis-tareas` | Mis Tareas | TECNICO |
| 9 | `/consulta-metas` | Consulta de Metas | COORD, DIRECTOR, STAFF |
| 10 | `/financiero` | Financiero | DIRECTOR, STAFF, COORD |
| 11 | `/reportes` | Reportes | DIRECTOR, STAFF, COORD |
| 12 | `/usuarios` | Usuarios | DIRECTOR, STAFF |

---

## 2. Bloques de spec completados

| Bloque | Descripción | Estado |
|--------|-------------|--------|
| **1** | Columnas de auditoría en FollowUpsTable (reportedByName, createdAt/updatedAt) | Completado |
| **2** | FollowUpFormPage — formulario de registro de avance con cascading selects y detección de duplicados 409 | Completado |
| **3** | GoalTrackingPage — balance comparativo meta vs ejecutado por tarea | Completado |
| **5** | DetailPanel — mini-tablas editables de metas anuales y presupuestos anuales en actividades | Completado |

---

## 3. Deuda técnica

| Prioridad | Descripción | Estado |
|-----------|-------------|--------|
| **Alta** | No hay tests. Cero: ni unitarios, ni de integración, ni E2E. No hay framework de testing instalado (ni Vitest, ni Testing Library). | Pendiente |
| **Alta** | No hay linting ni formateo configurado (sin ESLint, sin Prettier). | Pendiente |
| **Media** | CSS global monolítico (`global.css`, 850+ líneas). Debería migrar a CSS modules o al menos dividir por componente/sección. | Pendiente |
| **Media** | `FollowUpsPage` tiene un TODO: el botón "Editar" no abre modal de edición — `handleEdit` es un stub vacío. El backend ya tiene `PUT /follow-ups/{id}` y el frontend ya tiene `followUpApi.update()` + `FollowUpFormModal`. Solo falta conectar. | Pendiente |
| **Media** | Ruta `/tareas` (TasksPage) existe en el router pero no tiene entrada en el sidebar — es inaccesible desde la UI. | Pendiente |
| **Media** | ~~No hay pipeline CI/CD (GitHub Actions).~~ | **Resuelto** — `.github/workflows/ci.yml` (build + typecheck en cada PR a main) |
| **Baja** | Duplicación: `axiosClient.ts` exporta `extractErrorMessage()` y `utils/apiError.ts` exporta `getApiErrorMessage()` — hacen lo mismo. | Pendiente |
| **Baja** | Navbar.tsx legacy se conserva pero no se usa (el layout usa Sidebar). | Pendiente |
| **Baja** | No hay path aliases configurados en Vite/TS — imports usan rutas relativas largas. | Pendiente |

---

## 4. Retos identificados

1. **Sin pruebas automatizadas:** Cada cambio requiere verificación manual. No hay confianza en refactorings.
2. **CSS global sin scope:** Estilos pueden colisionar conforme crece el proyecto. El archivo ya supera 850 líneas.
3. **Estado distribuido en useState:** No hay store centralizado. Componentes complejos manejan mucho estado local.
4. **Sin caché de datos:** Cada navegación re-fetcha. No hay React Query, SWR, ni caché local.
5. **Responsive limitado:** Hay breakpoints en CSS pero no se han testeado exhaustivamente en móvil.

---

## 5. Pendientes para continuar desarrollo

### Funcionalidades por implementar

| Feature | Descripción | Complejidad | Dependencia backend |
|---------|-------------|-------------|---------------------|
| **Modal de edición de seguimiento** | Completar el TODO en FollowUpsPage — abrir `FollowUpFormModal` con datos existentes | Baja (2 horas) | Ya tiene `PUT /follow-ups/{id}` |
| **Entrada sidebar para /tareas** | Agregar nav item para TasksPage | Trivial (15 min) | Ninguna |
| **Dashboard con métricas reales** | HomePage muestra solo tarjetas de navegación, no KPIs | Media (6 horas) | Necesita endpoints de resumen |
| **Exportación Excel/PDF** | Solo CSV implementado en ReportsPage; agregar xlsx y PDF | Media (4 horas) | Ninguna (frontend-only) |
| **Notificaciones de vencimiento** | Banner o lista de tareas vencidas/próximas | Alta (8 horas) | Necesita endpoint backend |

### Infraestructura recomendada

| Item | Estado |
|------|--------|
| Vitest + Testing Library | No instalado |
| ESLint + Prettier | No configurado |
| Path aliases (`@/api`, `@/components`) | No configurado |
| React Query o SWR | No instalado |
| GitHub Actions CI (build + typecheck) | **Configurado** — `.github/workflows/ci.yml` |

---

## 6. Design tokens y sistema visual

### Paleta de colores

| Token | Uso |
|-------|-----|
| `--blue-900/700/600/100` | Primario, acciones, foco |
| `--green-700/600/400/100` | Éxito, completado, positivo |
| `--yellow-600/500/100` | Advertencia, en proceso |
| `--orange-600/100` | Alerta media |
| `--purple-600/100` | Indicadores especiales |
| `--sand-50` | Fondo general |
| `--ink-900/600/400` | Texto (oscuro → claro) |
| `--danger` / `--danger-bg` | Error, eliminar |

### Tipografía

- **Display:** Fraunces (serif) — títulos y marca
- **Body:** Inter (sans-serif) — texto general, forms, tablas

### Radios

- `--radius-sm`: 8px (botones, inputs, cards)
- `--radius-md`: 14px (modales, paneles)

### Fixes aplicados (2026-10-05)

- Puerto por defecto en `axiosClient.ts` corregido de `8080` → `8081`
