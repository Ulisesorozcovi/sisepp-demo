# SISEPP Frontend

Proyecto de portafolio inspirado en un caso de seguimiento de planes de inversión pública.

> **Proyecto de portafolio con datos ficticios.** No está afiliado a ninguna entidad. Licencia MIT.

## Stack

| Capa | Tecnologia | Version |
|------|-----------|---------|
| Runtime | React | 18.3 |
| Lenguaje | TypeScript (strict) | 5.5 |
| Bundler | Vite | 5.4 |
| HTTP | Axios | 1.7 |
| Routing | React Router DOM | 6.26 |
| CSS | Vanilla CSS con design tokens | - |

No se usa Tailwind, Bootstrap ni preprocesadores CSS.

## Requisitos

- Node.js >= 18
- El backend Spring Boot corriendo en `http://localhost:8080`
- PostgreSQL con la base de datos `sisepp` (el backend la gestiona con Flyway)

## Instalacion y ejecucion

```bash
# Clonar e instalar dependencias
cd frontend
npm install

# Copiar variables de entorno
cp .env.example .env

# Iniciar servidor de desarrollo
npm run dev

# Build de produccion
npm run build
```

La app arranca en `http://localhost:5173` por defecto.

## Variables de entorno

| Variable | Descripcion | Default |
|----------|-------------|---------|
| `VITE_API_BASE_URL` | Base URL del backend API | `http://localhost:8080/api/v1` |

## Usuarios de prueba

| Rol | Email | Password |
|-----|-------|----------|
| DIRECTOR | director@example.com | la de `SISEPP_SEED_PASSWORD` |
| COORDINADOR | coordinador@example.com | la de `SISEPP_SEED_PASSWORD` |
| TECNICO | tecnico@example.com | la de `SISEPP_SEED_PASSWORD` |

Estos usuarios se crean al arrancar el backend con el perfil `dev`; la contraseña se define con `SISEPP_SEED_PASSWORD` y no está en el repositorio.

## Estructura del proyecto

```
src/
  api/              # Modulos Axios por contexto (auth, user, task, structure, ...)
  context/          # AuthContext (JWT en localStorage)
  design-system/
    atoms/          # Componentes basicos: Button, Input, Select, Badge, Label, Avatar, Logo
    molecules/      # Composiciones: FormField, Alert, PasswordField, SdgCheckboxGroup, UserCell
    organisms/      # Componentes complejos: tablas, formularios modales, paneles
    templates/      # Layouts: AuthLayout (login), DashboardLayout (navbar + body)
  pages/            # Paginas conectadas a rutas
  routes/           # ProtectedRoute, roles.ts (HOME_BY_ROLE)
  styles/           # tokens.css (design tokens), global.css (todos los estilos)
  types/            # Interfaces TypeScript alineadas con los DTOs del backend
  utils/            # Helpers (getApiErrorMessage)
```

## Patrones de arquitectura

### Atomic Design

Los componentes siguen la jerarquia Atomic Design:

- **Atoms**: elementos HTML envueltos con estilos (Button, Input, Select, Badge)
- **Molecules**: composicion de atoms (FormField = Label + Input + error)
- **Organisms**: logica de negocio + UI (tablas con fetch, modales con formularios)
- **Templates**: layouts que definen la estructura de la pagina
- **Pages**: conectan organismos con rutas, manejan estado de pagina

### CSS

- Design tokens en `src/styles/tokens.css` (colores, tipografia, radii, shadows)
- Un solo archivo de estilos globales `src/styles/global.css`
- Clases con prefijo por capa: `atom-btn`, `molecule-field`, `organism-table-card`
- Paleta monocromo neutra (ver `src/styles/tokens.css`)
- Sin CSS-in-JS, sin CSS modules: vanilla CSS puro

### API

- Instancia Axios centralizada en `src/api/axiosClient.ts`
- Interceptor de request: inyecta `Authorization: Bearer <token>` desde localStorage
- Interceptor de response: redirige a `/login` en 401
- Un archivo por contexto: `authApi`, `userApi`, `taskApi`, `structureApi`, `followUpApi`, `financialApi`, `planningApi`
- Todos los endpoints bajo `/api/v1/`

### Autenticacion

- JWT Bearer token almacenado en `localStorage` (`sisepp_token`, `sisepp_user`)
- `AuthContext` provee `user`, `login()`, `logout()`, `hasRole()`
- 4 roles: `DIRECTOR`, `COORDINADOR`, `ADMINISTRADOR`, `TECNICO`
- `ProtectedRoute` recibe `allowedRoles[]` y redirige a la home del rol si no tiene acceso
- `HOME_BY_ROLE` mapea cada rol a su pagina principal

### Manejo de errores

- `getApiErrorMessage(error, fallback)` en `src/utils/apiError.ts`
- Extrae `message` de la respuesta `ApiError` del backend, o usa el fallback
- Se usa en todos los catch blocks de la aplicacion

### Tipos

- Cada tipo en `src/types/` esta alineado con los DTOs Response del backend
- Barrel re-export en `src/types/index.ts` con aliases de compatibilidad
- Enums como union types de TypeScript (no enum nativo)

## Rutas y permisos

| Ruta | Roles permitidos | Pagina |
|------|-----------------|--------|
| `/login` | Publica | LoginPage |
| `/usuarios` | DIRECTOR, ADMINISTRADOR | UsersPage |
| `/estructura` | DIRECTOR, ADMINISTRADOR, COORDINADOR | StructurePage |
| `/tareas` | COORDINADOR, DIRECTOR, ADMINISTRADOR | TasksPage |
| `/mis-tareas` | TECNICO | MyTasksPage |
| `/seguimiento` | COORDINADOR, DIRECTOR, ADMINISTRADOR | FollowUpsPage |
| `/financiero` | DIRECTOR, ADMINISTRADOR, COORDINADOR | FinancialPage |

## Backend

El backend esta en el directorio hermano `backend/`. Es un proyecto Spring Boot con:

- PostgreSQL en `localhost:5432/sisepp` (usuario `sisepp_user`; la contraseña la define quien monta el entorno)
- Migraciones Flyway 
- Autenticacion JWT con BCrypt
- Endpoints REST bajo `/api/v1/`

Para correr el backend:

```bash
cd ../backend
./mvnw spring-boot:run
```
