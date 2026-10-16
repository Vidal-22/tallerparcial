# API Tienda – Productos y Usuarios

API REST construida con Spring Boot que expone el CRUD completo de **Productos** y **Usuarios**, con arquitectura por capas (controlador → servicio → repositorio), Lombok, validación de datos, manejo global de errores y contraseñas guardadas con hash BCrypt.

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java (nivel de compilación) | 25 |
| Spring Boot | 4.1.1 (Spring MVC, Spring Data JPA, Bean Validation) |
| Hibernate ORM | 7 (incluido en Spring Boot) |
| Lombok | gestionado por Spring Boot |
| Base de datos | PostgreSQL 17 (Docker) |
| Spring Security Crypto | solo para BCrypt (sin autenticación) |
| Maven | 3.9 vía Maven Wrapper (`mvnw` / `mvnw.cmd`) |
| Pruebas | JUnit 5, MockMvc y H2 en memoria (solo en pruebas) |

> **Nota sobre la base de datos:** la guía toma MySQL como referencia, pero este proyecto se desarrolló desde el inicio con PostgreSQL, ejecutado en Docker. Se mantuvo esa configuración: el código no depende del motor, así que para usar MySQL basta con cambiar el driver en `pom.xml` y la URL en `application.properties`.

## Requisitos previos

- JDK 25 o superior.
- Docker Desktop, para levantar PostgreSQL con `docker-compose.yml`. También sirve un PostgreSQL propio.
- Postman, para las pruebas manuales.

No hace falta instalar Maven: el proyecto incluye Maven Wrapper.

## Configuración de la base de datos

1. Levante PostgreSQL con Docker desde la raíz del proyecto:

   ```bash
   docker compose up -d
   ```

   Se crea el contenedor `tiendaya-db`, que expone el puerto **5433** de la máquina y crea la base de datos `tiendaya`.

2. Revise `src/main/resources/application.properties`:

   ```properties
   spring.application.name=api
   server.port=8080
   spring.datasource.url=jdbc:postgresql://localhost:5433/tiendaya?sslmode=disable
   spring.datasource.username=<USUARIO_BD>
   spring.datasource.password=<CONTRASEÑA_BD>
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   ```

   El usuario y la contraseña deben coincidir con `POSTGRES_USER` y `POSTGRES_PASSWORD` de `docker-compose.yml`. Con `ddl-auto=update`, Hibernate crea las tablas `products` y `users` y conserva los datos entre reinicios.

## Ejecución

```bash
# Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw clean install
./mvnw spring-boot:run
```

También puede ejecutar el jar generado con `java -jar target/api-0.0.1-SNAPSHOT.jar`. Cuando la aplicación está lista, la consola muestra `Tomcat started on port 8080`.

`mvn clean install` ejecuta las pruebas automatizadas sobre H2 en memoria, así que no necesita PostgreSQL ni modifica sus datos.

## Endpoints

URL base: `http://localhost:8080`

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/productos` | Lista todos los productos | 200 |
| GET | `/api/productos/{id}` | Consulta un producto por id | 200 / 404 |
| POST | `/api/productos` | Crea un producto | 201 / 400 |
| PUT | `/api/productos/{id}` | Actualiza un producto | 200 / 400 / 404 |
| DELETE | `/api/productos/{id}` | Elimina un producto | 204 / 404 |
| GET | `/api/usuarios` | Lista todos los usuarios | 200 |
| GET | `/api/usuarios/{id}` | Consulta un usuario por id | 200 / 404 |
| POST | `/api/usuarios` | Crea un usuario | 201 / 400 |
| PUT | `/api/usuarios/{id}` | Actualiza un usuario | 200 / 400 / 404 |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario | 204 / 404 |

### Productos

Petición (`POST` / `PUT /api/productos`):

```json
{
  "nombre": "Teclado mecánico",
  "descripcion": "Switches rojos, retroiluminado",
  "precio": 189900,
  "stock": 15
}
```

Respuesta `201 Created`, que además incluye la cabecera `Location: /api/productos/1`:

```json
{
  "id": 1,
  "nombre": "Teclado mecánico",
  "descripcion": "Switches rojos, retroiluminado",
  "precio": 189900.0,
  "stock": 15
}
```

Reglas de validación:
- `nombre`: obligatorio, máximo 100 caracteres.
- `precio`: obligatorio y mayor que 0.
- `stock`: no puede ser negativo.
- `descripcion`: máximo 255 caracteres.

### Usuarios

Petición (`POST /api/usuarios`):

```json
{
  "nombre": "Ana Gómez",
  "correo": "ana@correo.com",
  "contrasena": "secreto123",
  "rol": "ADMIN"
}
```

Respuesta `201 Created`. La contraseña **nunca** se devuelve:

```json
{
  "id": 1,
  "nombre": "Ana Gómez",
  "correo": "ana@correo.com",
  "rol": "ADMIN"
}
```

Reglas de negocio:
- El correo es único, sin distinguir mayúsculas. Si ya existe, la API responde **400**. Al actualizar, el usuario puede conservar su propio correo.
- La contraseña es obligatoria al crear y debe tener entre 6 y 72 caracteres. Se guarda con hash BCrypt. En `PUT` es opcional: si se omite, se conserva la contraseña actual.
- Ninguna respuesta (listar, consultar, crear o actualizar) incluye la contraseña.

### Errores

Recurso inexistente, `404 Not Found`:

```json
{ "error": "Producto no encontrado con id 999" }
```

Correo duplicado, `400 Bad Request`:

```json
{ "error": "Ya existe un usuario con el correo ana@correo.com" }
```

Datos inválidos, `400 Bad Request`:

```json
{
  "error": "Datos inválidos",
  "campos": {
    "nombre": "El nombre es obligatorio",
    "precio": "El precio debe ser mayor que cero"
  }
}
```

Las respuestas de error nunca incluyen trazas ni datos de conexión. Los errores inesperados responden `500` con el mensaje `Error interno del servidor` y se registran en el log.

## Pruebas con Postman

1. Importe `postman/API-Tienda.postman_collection.json` (Import → File).
2. En las variables de la colección, cambie `nombreAprendiz` por su nombre. Se usa como nombre del primer usuario creado.
3. Con la API en ejecución, ejecute las peticiones en orden, carpeta por carpeta: **Productos** y luego **Usuarios**. Las peticiones de creación guardan los ids en las variables `productoId`, `productoId2`, `usuarioId` y `usuarioId2`, y cada petición incluye pruebas del código de estado esperado.
4. Caso 11, verificación en la base de datos:

   ```bash
   docker exec -it tiendaya-db psql -U <USUARIO_BD> -d tiendaya -c "SELECT * FROM products;" -c "SELECT id, name, email, role FROM users;"
   ```

5. Caso 12, persistencia: detenga la API, vuelva a iniciarla y ejecute las peticiones `12 Persistencia`.

## Pruebas automatizadas

```bash
mvnw.cmd test
```

- `ProductControllerTests`: flujo CRUD completo, 404 en consultar, actualizar y eliminar, y 400 con datos inválidos.
- `UserControllerTests`: CRUD, correo duplicado (400) al crear y al actualizar, contraseña ausente de todas las respuestas, hash BCrypt guardado y 404.
- `ApiApplicationTests`: comprueba que el contexto de Spring carga.

## Estructura del proyecto

```
src/main/java/com/example/api
├── ApiApplication.java          Punto de entrada
├── config
│   └── PasswordEncoderConfig    Bean BCrypt
├── controller
│   ├── ProductController        /api/productos
│   └── UserController           /api/usuarios
├── exception
│   ├── ResourceNotFoundException     → 404
│   ├── DuplicateResourceException    → 400
│   └── GlobalExceptionHandler        @RestControllerAdvice
├── model
│   ├── Product, User                 Entidades JPA
│   ├── ProductRequest, UserRequest   DTO de petición (Lombok @Data)
│   └── ProductResponse, UserResponse DTO de respuesta (record)
├── repository
│   ├── ProductRepository
│   └── UserRepository
└── service
    ├── ProductService
    └── UserService
src/main/resources/application.properties
src/test/...                      Pruebas JUnit + MockMvc (H2 en memoria)
postman/API-Tienda.postman_collection.json
docker-compose.yml                PostgreSQL 17
```

## Autor

- `<Nombre del aprendiz>` – Ficha `<número de ficha>`
