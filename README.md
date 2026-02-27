## Blog Personal - API REST con Spring Boot

**Blog Personal** es una API REST desarrollada con **Spring Boot 3**, **Spring Security**, **JWT** y **Spring Data JPA**, que permite gestionar un blog completo con:

- Autenticación y autorización con JWT
- Gestión de usuarios y perfil
- Publicación y administración de posts
- Comentarios en posts
- Categorías y tags
- Respuestas paginadas y manejo centralizado de errores

---

### Tecnologías utilizadas

- **Java 21**
- **Spring Boot 3.4.3**
  - spring-boot-starter-web
  - spring-boot-starter-data-jpa
  - spring-boot-starter-security
  - spring-boot-starter-validation
- **MySQL** (conector `mysql-connector-j`)
- **JWT** (`io.jsonwebtoken:jjwt-*`)
- **Lombok**
- **springdoc-openapi** (Swagger UI para documentación)
- **Maven** como gestor de dependencias

---

### Estructura principal del proyecto

La estructura de paquetes (bajo `src/main/java/com/luciano/blogpersonal`) está organizada por dominios:

- `common/`
  - `dto/` – DTOs genéricos (`ApiResponse`, `PaginatedResponse`)
  - `exception/` – Manejo global de excepciones (`GlobalExceptionHandler`, `BlogApiException`, `ResourceNotFoundException`)
  - `utils/` – Utilidades comunes (`AppConstants`, `SlugUtils`)
- `security/`
  - `controller/` – Endpoints de autenticación (`AuthController`)
  - `dto/` – DTOs de login y respuesta JWT (`LoginRequest`, `JwtAuthResponse`)
  - `jwt/` – Lógica JWT (`JwtTokenProvider`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`)
  - `service/` – Servicios de autenticación (`AuthService`, `AuthServiceImpl`)
  - `UserDetailsServiceImpl` – Integración con Spring Security
- `user/`
  - `controller/` – Endpoints de usuario (`UserController`)
  - `model/` – Entidad `User`
  - `dto/` – DTOs de creación/actualización/respuesta de usuario
  - `repository/` – `UserRepository`
  - `mapper/` – `UserMapper`
  - `service/` – `UserService` y su implementación
- `post/`
  - `controller/` – `PostController`
  - `model/` – Entidad `Post`
  - `dto/` – DTOs de creación, actualización, detalle y resumen de posts
  - `repository/`, `mapper/`, `service/`
- `comment/`
  - `controller/` – `CommentController`
  - `model/` – Entidad `Comment`
  - `dto/`, `repository/`, `mapper/`, `service/`
- `category/`
  - `controller/`, `model/`, `dto/`, `repository/`, `mapper/`, `service/`
- `tag/`
  - `controller/`, `model/`, `dto/`, `repository/`, `mapper/`, `service/`
- `config/`
  - `SecurityConfig` – Configuración de Spring Security
  - `WebConfig` – Configuración web adicional
- `BlogpersonalApplication.java` – Clase principal de arranque de Spring Boot

---

### Requisitos previos

- **Java 21** instalado y configurado (`JAVA_HOME`)
- **Maven 3+**
- **MySQL** en ejecución (local o remoto)
- Un usuario de base de datos con permisos para crear y modificar la BD

---

### Configuración de base de datos y aplicación

La configuración por defecto se encuentra en `src/main/resources/application.properties`:

---

### Instalación y ejecución

#### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd blogpersonal
```

#### 2. Configurar base de datos MySQL

1. Crear la base de datos (si no se crea automáticamente):

```sql
CREATE DATABASE blogpersonal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Ajustar usuario y contraseña en `application.properties` si es necesario:

```properties
spring.datasource.username=<tu_usuario>
spring.datasource.password=<tu_password>
```

#### 3. Compilar y ejecutar con Maven

```bash
mvn clean install
mvn spring-boot:run
```

Por defecto, la aplicación quedará disponible en:

- `http://localhost:8080`

---

### Documentación de la API (Swagger / OpenAPI)

Este proyecto incluye **springdoc-openapi** (`springdoc-openapi-starter-webmvc-ui`), por lo que, una vez levantada la aplicación, podrás acceder a la documentación interactiva de la API en:

- `http://localhost:8080/swagger-ui/index.html`

Desde allí podrás explorar y probar los endpoints para:

- Autenticación (login / registro según cómo lo tengas implementado)
- Gestión de usuarios
- Posts
- Comentarios
- Categorías
- Tags

---

### Autenticación y autorización

La seguridad está basada en **JWT**:

- El usuario se autentica mediante un endpoint de login (por ejemplo `POST /api/auth/login`) enviando sus credenciales.
- La API responde con un **token JWT**.
- Las peticiones a endpoints protegidos deben incluir el token en el header `Authorization`:

```http
Authorization: Bearer <tu_token_jwt>
```

La configuración de filtros, proveedores de token y puntos de entrada se maneja en:

- `security/jwt/` (`JwtTokenProvider`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`)
- `config/SecurityConfig`

---

### Tests

El proyecto incluye dependencias para pruebas:

- `spring-boot-starter-test`
- `spring-security-test`

Puedes ejecutar los tests con:

```bash
mvn test
```

---

### Autor

- **Nombre**: Luciano  
- **Proyecto**: Blog Personal API con Spring Boot

