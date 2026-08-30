# PortfolioApp — Sistema de reserva de salas

Aplicación full-stack para reservar salas de reuniones: los usuarios consultan la disponibilidad, reservan horarios y gestionan sus propias reservas, mientras que los administradores gestionan el catálogo de salas y todas las reservas del sistema.

Backend en **Spring Boot** (API REST + autenticación JWT) y frontend en **React** (SPA con Vite).

## Funcionalidades

- Registro e inicio de sesión con JWT (stateless, sin sesiones en servidor).
- Roles `USER` y `ADMIN` con permisos distintos por endpoint.
- Consulta pública de salas disponibles.
- Reserva de salas por fecha y horario, con validación de solapamiento (no se pueden reservar dos franjas que se crucen en la misma sala).
- Gestión de "Mis reservas": cada usuario ve y cancela únicamente sus propias reservas.
- Panel de administración: alta/edición/borrado de salas y cancelación de cualquier reserva del sistema.
- Rutas protegidas en el frontend según autenticación y rol.

## Cómo funciona

### Arquitectura general

El frontend (React) y el backend (Spring Boot) son dos aplicaciones independientes que se comunican por HTTP con JSON. El backend no sirve HTML ni mantiene sesión: cada petición del navegador debe llevar su propio token de autenticación, y el servidor no guarda estado entre peticiones (`SessionCreationPolicy.STATELESS`). Esto es lo que permite escalar el backend horizontalmente sin preocuparse por sesiones compartidas.

```
Navegador ⇄ React (Vite, :5173) ⇄ fetch JSON ⇄ Spring Boot (:8080) ⇄ MySQL (:3310)
```

### Flujo de autenticación (JWT)

1. El usuario se registra (`POST /api/auth/register`) o inicia sesión (`POST /api/auth/login`).
2. `AuthController` delega en `UsuarioService` (verifica credenciales con `AuthenticationManager` + `PasswordEncoder`) y, si son válidas, `JwtService` genera un token firmado (HMAC) que incluye el email, el id y el rol del usuario, con expiración de 24h.
3. El frontend guarda ese token en `localStorage` a través de `AuthContext` y lo adjunta como cabecera `Authorization: Bearer <token>` en cada petición protegida (`src/api/client.js`).
4. En el backend, `JwtAuthFilter` intercepta cada petición entrante, valida el token y, si es correcto, reconstruye la identidad del usuario en el `SecurityContext` para que Spring Security sepa quién hace la petición y con qué rol — sin tocar la base de datos de sesiones, porque no existe: todo vive en el propio token.
5. `SecurityConfig` decide, por rol y método HTTP, qué endpoints son públicos (`GET /api/salas`), cuáles requieren estar autenticado (`/api/reservas/**`) y cuáles requieren rol `ADMIN` (crear/editar/borrar salas).

### Flujo de una reserva

1. El usuario elige una sala en la vista `Salas`, indica fecha, hora de inicio y hora de fin, y envía el formulario (`POST /api/reservas?salaId=...`).
2. `ReservaService.crear()` primero busca todas las reservas existentes para esa sala en esa fecha, y comprueba si el nuevo rango de horas se solapa con alguna de ellas (`horaInicioExistente < horaFinNueva && horaInicioNueva < horaFinExistente`). Si hay solapamiento, lanza `ReservaSolapadaException` y el backend responde `409 Conflict` con un mensaje claro.
3. Si no hay conflicto, la reserva se guarda asociada a la sala y al usuario autenticado (extraído del token, nunca del cuerpo de la petición, para que un usuario no pueda reservar en nombre de otro).
4. El usuario puede ver sus reservas en "Mis reservas" y cancelarlas; `ReservaService.cancelar()` comprueba que la reserva pertenezca a quien la cancela (o que sea un `ADMIN`, que puede cancelar cualquiera) antes de borrarla.

### Capas de protección

Los permisos se aplican en dos sitios distintos, con propósitos distintos:
- **Frontend (`ProtectedRoute.jsx`)**: solo mejora la experiencia — oculta enlaces y redirige a `/login` si no hay sesión. No es seguridad real, porque cualquiera puede saltarse el frontend y llamar a la API directamente.
- **Backend (`SecurityConfig` + `JwtAuthFilter`)**: aquí es donde se aplican los permisos de verdad. Cada endpoint decide, según el rol del token, si la petición se procesa o se rechaza con `401`/`403`, independientemente de lo que haga el frontend.

## Stack técnico

**Backend**
- Java 17, Spring Boot 4
- Spring Security + JWT ([jjwt](https://github.com/jwtk/jjwt))
- Spring Data JPA + Hibernate
- MySQL (producción/desarrollo) y H2 en memoria (tests)
- Bean Validation (`jakarta.validation`)

**Frontend**
- React 19 + Vite
- React Router
- Context API para el estado de autenticación
- Fetch API con un cliente propio (`src/api/client.js`)

## Estructura del proyecto

```
portfolioapp/
├── src/main/java/.../portfolioapp/
│   ├── config/        # Seguridad, CORS, password encoder
│   ├── controller/     # Endpoints REST
│   ├── service/        # Lógica de negocio
│   ├── repository/     # Acceso a datos (Spring Data JPA)
│   ├── entity/          # Entidades JPA
│   ├── dto/              # Objetos de entrada/salida de la API
│   ├── security/       # Filtro y servicio JWT
│   └── exception/      # Excepciones de negocio + manejador global
├── src/test/            # Tests unitarios (JUnit 5 + Mockito)
├── frontend/
│   ├── src/pages/       # Vistas (salas, login, mis reservas, admin)
│   ├── src/components/ # Navbar, rutas protegidas
│   ├── src/context/     # AuthContext
│   └── src/api/          # Cliente HTTP hacia el backend
└── docker-compose.yml   # MySQL para desarrollo local
```

## Puesta en marcha

### Requisitos
- Java 17+
- Node.js 20+
- Docker (para MySQL) — opcional, también funciona con H2/MySQL locales

### 1. Base de datos

```bash
docker-compose up -d
```

Levanta MySQL en `localhost:3310`, base de datos `reservas_db`.

### 2. Backend

```bash
cp .env.example .env   # y ajusta los valores si hace falta
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080/api`. Con `spring.jpa.hibernate.ddl-auto=update` el esquema se crea automáticamente al arrancar.

Variables de entorno relevantes (ver [.env.example](.env.example)):

| Variable | Descripción | Valor por defecto (dev) |
|---|---|---|
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://127.0.0.1:3310/reservas_db` |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciales de la base de datos | `root` / `root` |
| `JWT_SECRET` | Clave para firmar los tokens JWT | clave de desarrollo (cámbiala en producción) |
| `JWT_EXPIRATION_MS` | Duración del token en milisegundos | `86400000` (24h) |

### 3. Frontend

```bash
cd frontend
cp .env.example .env   # opcional, por defecto apunta a localhost:8080
npm install
npm run dev
```

La app queda disponible en `http://localhost:5173`.

## Tests

```bash
./mvnw test
```

Incluye tests unitarios de la lógica de negocio: detección de solapamiento de reservas, permisos de cancelación (propietario vs. admin) y registro de usuarios (email duplicado, cifrado de contraseña).

## Roles y permisos

| Endpoint | Público | USER | ADMIN |
|---|:---:|:---:|:---:|
| `POST /api/auth/register`, `/login` | ✅ | ✅ | ✅ |
| `GET /api/salas` | ✅ | ✅ | ✅ |
| `POST/PUT/DELETE /api/salas` | ❌ | ❌ | ✅ |
| `POST /api/reservas` | ❌ | ✅ | ✅ |
| `GET /api/reservas/mias` | ❌ | ✅ (propias) | ✅ |
| `GET /api/reservas` (todas) | ❌ | ❌ | ✅ |
| `DELETE /api/reservas/{id}` | ❌ | ✅ (propias) | ✅ (cualquiera) |

## Licencia

Proyecto personal con fines de portafolio.
