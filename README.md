# PortfolioApp

Aplicación web para reservar salas de reuniones, desarrollada como proyecto personal, con **Spring Boot** y Java en el backend y **React** en el frontend. Incluye dos tipos de usuario (usuario normal y administrador) y control de solapamiento de horarios para que no se pueda reservar una sala ya ocupada.

## Índice

- [Capturas](#capturas)
- [Cuentas de prueba](#cuentas-de-prueba)
- [Funcionalidades](#funcionalidades)
- [Cómo funciona por dentro](#cómo-funciona-por-dentro)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Instalación en local](#instalación-en-local)
- [Autor](#autor)

## Capturas

Salas disponibles | Reservar una sala
:---: | :---:
<img src="https://github.com/user-attachments/assets/6e2b8ac2-d4a4-4981-9619-2e2f639280f4" width="400" /> | <img src="https://github.com/user-attachments/assets/677c9edc-5486-4601-906c-1683de87b56e" width="400" />

Mis reservas | Todas las reservas (admin)
:---: | :---:
<img src="https://github.com/user-attachments/assets/063d3978-6fda-4bf9-bde5-5a022db86c21" width="400" /> | <img src="https://github.com/user-attachments/assets/27d95e28-1623-48de-81da-2ce80d837494" width="400" />


## Cuentas de prueba

El proyecto todavía no está desplegado en producción, así que no hay una demo en vivo con cuentas de prueba. Para probar el panel de administrador en local:

1. Regístrate normalmente desde la web (esto crea una cuenta de tipo usuario normal).
2. Conéctate a la base de datos y cambia tu rol a administrador:

```sql
UPDATE usuarios SET role = 'ADMIN' WHERE email = 'tu-email@ejemplo.com';
```

3. Vuelve a iniciar sesión: ahora entrarás directamente al panel de administrador.

## Funcionalidades

### Usuario normal
- Registro e inicio de sesión.
- Ver el catálogo de salas disponibles (esto no requiere estar registrado).
- Reservar una sala eligiendo fecha, hora de inicio y hora de fin. La app avisa si esa sala ya está ocupada en ese horario.
- Ver "Mis reservas" y cancelarlas cuando quiera.

### Administrador
- Todo lo del usuario normal, más:
- Crear, editar y eliminar salas.
- Ver y cancelar las reservas de cualquier usuario, no solo las propias.
- Al iniciar sesión, entra directamente al panel de administrador en vez de a la vista pública de salas.

## Cómo funciona por dentro

La app tiene dos partes separadas que se comunican entre sí:

```
Navegador → React (frontend) → peticiones al backend → Spring Boot (backend) → base de datos MySQL
```

El frontend no habla directamente con la base de datos. Todo pasa por el backend, que es quien decide qué se puede hacer y qué no.

**Login:** te registras o inicias sesión mandando tu email y contraseña. Si son correctos, el backend te devuelve un token (una especie de "carnet" digital que demuestra quién eres). El navegador lo guarda y lo manda en cada petición que necesita saber quién eres; el backend lo revisa en cada petición para saber si estás logueado y si eres administrador.

**Reservas:** al reservar, el backend comprueba si ya existe otra reserva para esa misma sala que se cruce con el horario elegido. Si se cruza, la rechaza; si no, la guarda asociada a tu usuario. Cancelar una reserva ajena solo lo puede hacer un administrador.

## Tecnologías utilizadas

**Backend:** Java, Spring Boot (Spring Security, Spring Data JPA, validación de datos, autenticación con JWT y roles), MySQL.

**Frontend:** React, Vite, React Router, Context API (fetch al backend).

**Herramientas:** Git, GitHub, Docker (para levantar MySQL en local), JUnit y Mockito para los tests.

## Estructura del proyecto

```
portfolioapp/
├── src/main/java/.../portfolioapp/
│   ├── config/        # Configuración de seguridad y CORS
│   ├── controller/     # Los endpoints de la API
│   ├── service/        # La lógica de negocio (reglas de la app)
│   ├── repository/     # Acceso a la base de datos
│   ├── entity/          # Las tablas de la base de datos
│   ├── dto/              # Los datos que entran y salen de la API
│   ├── security/       # Todo lo relacionado con el login y los tokens
│   └── exception/      # Manejo de errores
├── src/test/            # Tests
├── frontend/
│   ├── src/pages/       # Las pantallas de la app
│   ├── src/components/ # Piezas reutilizables (menú, rutas protegidas...)
│   ├── src/context/     # Estado de sesión del usuario
│   └── src/api/          # Donde se hacen las llamadas al backend
└── docker-compose.yml   # Para levantar MySQL fácilmente en local
```

## Instalación en local

Requiere Java 17+, Node.js 20+ y Docker.

```bash
git clone <url-del-repo>
cd portfolioapp

# Base de datos
docker-compose up -d

# Backend
cp .env.example .env
./mvnw spring-boot:run

# Frontend (en otra terminal)
cd frontend
npm install
npm run dev
```

La web quedará disponible en `http://localhost:5173` y la API en `http://localhost:8080/api`.

## Autor

Luis Alfredo Jiménez Jerez
Técnico Superior en Desarrollo de Aplicaciones Web

- GitHub: [github.com/luisjj12](https://github.com/luisjj12)
- Email: luisjmnzjln@gmail.com
