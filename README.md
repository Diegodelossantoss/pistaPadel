# PistaPadel Backend

Backend del proyecto de reserva de pistas de pádel desarrollado con Spring Boot.

## Tecnologías
- Java 21
- Spring Boot
- Maven
- H2 Database
- JUnit
- MockMvc
- GitHub Actions

## Cómo ejecutar el proyecto
1. Abrir el proyecto en IntelliJ
2. Esperar a que Maven cargue las dependencias
3. Ejecutar la clase principal `PistaPadelApplication`
4. El backend arranca en:
   `http://localhost:8080`

## Base de datos
El proyecto usa H2 en memoria.

Configuración principal en `application.properties`:
- URL: `jdbc:h2:mem:pistapadel`
- usuario: `sa`
- consola H2 habilitada en:
  `http://localhost:8080/h2-console`

## Endpoints principales
### Auth
- `POST /pistaPadel/auth/register`
- `POST /pistaPadel/auth/login`
- `POST /pistaPadel/auth/logout`
- `GET /pistaPadel/auth/me`

### Usuarios
- `GET /pistaPadel/users`
- `GET /pistaPadel/users/{id}`
- `PATCH /pistaPadel/users/{id}`

### Pistas
- `GET /pistaPadel/courts`
- `GET /pistaPadel/courts/{id}`
- `POST /pistaPadel/courts`

### Reservas
- `GET /pistaPadel/reservations`
- `GET /pistaPadel/reservations/{id}`
- `POST /pistaPadel/reservations`
- `PATCH /pistaPadel/reservations/{id}`
- `DELETE /pistaPadel/reservations/{id}`

### Disponibilidad y salud
- `GET /pistaPadel/availability`
- `GET /pistaPadel/health`

## Tests
Para ejecutar los tests en local:

```bash
./mvnw test