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
