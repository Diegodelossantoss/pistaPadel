# PistaPadel Backend

Frontend en GitHub Pages: https://blancamanriquesz.github.io/pistaPadel/frontend/

## Integrantes del equipo

Diego de los Santos González
Blanca Manrique Sanz
Inés González Lázaro
Lucía Victoria Hohenleitner Bueno


## Usuarios de prueba

Al arrancar la aplicación se crean automáticamente los siguientes usuarios:

### Administrador
| Campo | Valor |
|-------|-------|
| Email | `adminapp@test.com` |
| Contraseña | `1234` |
| Rol | `ADMIN` |


### Usuario normal
Regístrate desde la página de login con cualquier email y contraseña. El rol asignado será `USER` automáticamente.

| Campo | Valor de ejemplo |
|-------|-----------------|
| Nombre | `Blanca` |
| Apellidos | `Manrique` |
| Email | `blanca@gmail.com` |
| Contraseña | `1234` |
| Rol | `USER` (asignado automáticamente) |



---

## Tecnologías
- Java 21
- Spring Boot
- Maven
- H2 Database
- JUnit
- MockMvc
- GitHub Actions

## Cómo ejecutar el proyecto

```bash
mvn spring-boot:run
```


El frontend debe abrirse con **Live Server** desde VS Code (clic derecho en `login.html` → *Open with Live Server*).

## Base de datos
El proyecto usa H2 en memoria. Los datos se pierden al reiniciar el servidor.

Configuración principal en `application.properties`:
- URL: `jdbc:h2:mem:pistapadel`
- Usuario: `sa`
- Consola H2 habilitada en: `http://localhost:8080/h2-console`

## Endpoints principales

### Auth
- `POST /pistaPadel/auth/register`
- `POST /pistaPadel/auth/login`
- `POST /pistaPadel/auth/logout`
- `GET  /pistaPadel/auth/me`

### Usuarios
- `GET   /pistaPadel/users`
- `GET   /pistaPadel/users/{id}`
- `PATCH /pistaPadel/users/{id}`

### Pistas
- `GET  /pistaPadel/courts`
- `GET  /pistaPadel/courts/{id}`
- `POST /pistaPadel/courts`

### Reservas
- `GET    /pistaPadel/reservations`
- `GET    /pistaPadel/reservations/{id}`
- `POST   /pistaPadel/reservations`
- `PATCH  /pistaPadel/reservations/{id}`
- `DELETE /pistaPadel/reservations/{id}`

### Disponibilidad y salud
- `GET /pistaPadel/availability`
- `GET /pistaPadel/health`

## Tests

```bash
mvn test
```

O con el wrapper de Maven:

```bash
./mvnw test
```



