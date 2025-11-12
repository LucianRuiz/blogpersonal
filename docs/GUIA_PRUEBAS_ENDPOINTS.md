# 🧪 Guía Completa de Pruebas - Endpoints Blog Personal

## 📋 Índice
1. [Configuración Inicial](#configuración-inicial)
2. [Flujo Completo de Pruebas](#flujo-completo-de-pruebas)
3. [Autenticación](#autenticación)
4. [Usuarios](#usuarios)
5. [Categorías](#categorías)
6. [Tags](#tags)
7. [Posts](#posts)
8. [Comentarios](#comentarios)
9. [Verificaciones y Validaciones](#verificaciones-y-validaciones)

---

## 🔧 Configuración Inicial

### Base URL
```
http://localhost:8080
```

### Headers Comunes
```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer {JWT_TOKEN}"
}
```

### Herramientas Recomendadas
- **Postman** (Recomendado)
- **Insomnia**
- **cURL** (Terminal)
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

## 🎯 Flujo Completo de Pruebas

### **PASO 1: Configuración Inicial**
1. Crear usuarios (ADMIN + 2 USERS)
2. Hacer login con todos los usuarios
3. Guardar tokens JWT

### **PASO 2: Crear Datos Base**
1. Crear 3 categorías (como ADMIN)
2. Crear 3 tags (como USER)
3. Crear 3 posts (como USER)

### **PASO 3: Probar Funcionalidades**
1. Comentar en posts
2. Responder comentarios
3. Buscar y filtrar posts
4. Actualizar perfiles

### **PASO 4: Verificar Permisos**
1. Intentar acceder a endpoints ADMIN como USER
2. Verificar que solo el propietario puede editar sus posts/comentarios

---

## 🔐 Autenticación

### 1. Registro de Usuario ADMIN
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Admin",
  "lastName": "Sistema",
  "username": "admin",
  "email": "admin@blog.com",
  "password": "admin123456",
  "bio": "Administrador del sistema"
}
```

### 2. Registro de Usuario 1
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "María",
  "lastName": "García",
  "username": "mariagarcia",
  "email": "maria.garcia@email.com",
  "password": "maria123456",
  "bio": "Desarrolladora frontend apasionada por React y Vue.js"
}
```

### 3. Registro de Usuario 2
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Carlos",
  "lastName": "López",
  "username": "carloslopez",
  "email": "carlos.lopez@email.com",
  "password": "carlos123456",
  "bio": "Desarrollador backend especializado en Java y Spring Boot"
}
```

### 4. Login ADMIN
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameEmail": "admin",
  "password": "admin123456"
}
```
token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MjcwMjg2MCwiZXhwIjoxNzUzMzA3NjYwfQ.MLeJP-24M9GhiTftu72EBMw1CqIR0P-ue4GCBZB0tsQ

### 5. Login Usuario 1
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameEmail": "mariagarcia",
  "password": "maria123456"
}
```
token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJpYWdhcmNpYSIsImlhdCI6MTc1MjcwMjE0NCwiZXhwIjoxNzUzMzA2OTQ0fQ.9XwsWXFnQl7ktrI2A0b1I0BLcdIF12M7SoR49BOSr4w

### 6. Login Usuario 2
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameEmail": "carloslopez",
  "password": "carlos123456"
}
```
token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXJsb3Nsb3BleiIsImlhdCI6MTc1MjcwMjE2MiwiZXhwIjoxNzUzMzA2OTYyfQ.adtnDfKRkGLyFroLnM7WV_sty-aQh2Cl-fCMAPDkQsI

**Respuesta Esperada (para todos los logins):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

### 7. Validar Token
```http
POST /api/auth/validate?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Respuesta Esperada:**
```json
{
  "success": true,
  "message": "Token válido"
}
```

---

## 👥 Usuarios

### 1. Obtener Todos los Usuarios (Solo ADMIN)
```http
GET /api/users?pageNo=0&pageSize=10&sortBy=id&sortDir=asc
Authorization: Bearer {ADMIN_TOKEN}
```

### 2. Obtener Usuario por ID
```http
GET /api/users/1
```

### 3. Obtener Usuario Actual
```http
GET /api/users/me
Authorization: Bearer {JWT_TOKEN}
```

### 4. Obtener Usuario por Username
```http
GET /api/users/username/mariagarcia
```

### 5. Actualizar Usuario 1
```http
PUT /api/users/2
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "name": "María Elena",
  "lastName": "García Rodríguez",
  "bio": "Desarrolladora frontend senior con 8 años de experiencia en React, Vue.js y Angular",
  "email": "maria.elena@email.com",
  "profileImage": "https://ejemplo.com/maria-profile.jpg"
}
```

### 6. Actualizar Usuario 2
```http
PUT /api/users/3
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "name": "Carlos Alberto",
  "lastName": "López Martínez",
  "bio": "Desarrollador backend senior especializado en Java, Spring Boot y microservicios",
  "email": "carlos.alberto@email.com",
  "profileImage": "https://ejemplo.com/carlos-profile.jpg"
}
```

### 7. Cambiar Contraseña Usuario 1
```http
PUT /api/users/2/password
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "currentPassword": "maria123456",
  "newPassword": "mariaNueva2024",
  "confirmPassword": "mariaNueva2024"
}
```

### 8. Cambiar Contraseña Usuario 2
```http
PUT /api/users/3/password
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "currentPassword": "carlos123456",
  "newPassword": "carlosNuevo2024",
  "confirmPassword": "carlosNuevo2024"
}
```

### 9. Eliminar Usuario (Solo ADMIN)
```http
DELETE /api/users/1
Authorization: Bearer {ADMIN_TOKEN}
```

### 10. Verificar Email Disponible
```http
GET /api/users/check-email?email=nuevo@email.com
```

### 11. Verificar Username Disponible
```http
GET /api/users/check-username?username=nuevoUsuario
```

---

## 📂 Categorías

### 1. Obtener Categorías Paginadas
```http
GET /api/categories?pageNo=0&pageSize=10&sortBy=name&sortDir=asc
```

### 2. Obtener Todas las Categorías
```http
GET /api/categories/all
```

### 3. Obtener Categoría por ID
```http
GET /api/categories/1
```

### 4. Obtener Categoría por Slug
```http
GET /api/categories/slug/tecnologia
```

### 5. Obtener Categorías Populares
```http
GET /api/categories/popular?limit=5
```

### 6. Crear Categoría 1 (Solo ADMIN)
```http
POST /api/categories
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "Tecnología",
  "description": "Artículos sobre tecnología, programación y desarrollo de software"
}
```

### 7. Crear Categoría 2 (Solo ADMIN)
```http
POST /api/categories
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "Desarrollo Web",
  "description": "Tutoriales y guías sobre desarrollo web, frontend y backend"
}
```

### 8. Crear Categoría 3 (Solo ADMIN)
```http
POST /api/categories
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "Inteligencia Artificial",
  "description": "Artículos sobre IA, machine learning y tecnologías emergentes"
}
```

### 9. Crear Categoría 4 - NUEVA (Solo ADMIN)
```http
POST /api/categories
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "DevOps y Cloud",
  "description": "Artículos sobre DevOps, CI/CD, contenedores y servicios en la nube"
}
```

### 10. Crear Categoría 5 - NUEVA (Solo ADMIN)
```http
POST /api/categories
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "Base de Datos",
  "description": "Artículos sobre bases de datos SQL, NoSQL, optimización y diseño"
}
```

### 11. Crear Categoría 6 - NUEVA (Solo ADMIN)
```http
POST /api/categories
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "Seguridad Informática",
  "description": "Artículos sobre ciberseguridad, mejores prácticas y vulnerabilidades"
}
```

### 12. Actualizar Categoría 1 (Solo ADMIN)
```http
PUT /api/categories/1
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "Tecnología Avanzada",
  "description": "Artículos sobre tecnología avanzada, programación y desarrollo de software moderno"
}
```

### 13. Actualizar Categoría 2 (Solo ADMIN)
```http
PUT /api/categories/2
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "Desarrollo Web Moderno",
  "description": "Tutoriales y guías sobre desarrollo web moderno, frameworks y mejores prácticas"
}
```

### 14. Eliminar Categoría (Solo ADMIN)
```http
DELETE /api/categories/3
Authorization: Bearer {ADMIN_TOKEN}
```

### 15. Verificar Nombre Disponible
```http
GET /api/categories/check-name?name=nueva-categoria
```

### 16. Verificar Slug Disponible
```http
GET /api/categories/check-slug?slug=nueva-categoria
```

---

## 🏷️ Tags

### 1. Obtener Tags Paginados
```http
GET /api/tags?pageNo=0&pageSize=10&sortBy=name&sortDir=asc
```

### 2. Obtener Todos los Tags
```http
GET /api/tags/all
```

### 3. Obtener Tag por ID
```http
GET /api/tags/1
```

### 4. Obtener Tag por Slug
```http
GET /api/tags/slug/java
```

### 5. Obtener Tags Populares
```http
GET /api/tags/popular?limit=5
```

### 6. Crear Tag 1
```http
POST /api/tags
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "name": "Java"
}
```

### 7. Crear Tag 2
```http
POST /api/tags
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "name": "Spring Boot"
}
```

### 8. Crear Tag 3
```http
POST /api/tags
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "name": "React"
}
```

### 9. Crear Tag 4
```http
POST /api/tags
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "name": "JavaScript"
}
```

### 10. Crear Tag 5
```http
POST /api/tags
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "name": "Microservicios"
}
```

### 11. Crear Tag 6 - NUEVO
```http
POST /api/tags
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "name": "Docker"
}
```

### 12. Crear Tag 7 - NUEVO
```http
POST /api/tags
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "name": "Kubernetes"
}
```

### 13. Crear Tag 8 - NUEVO
```http
POST /api/tags
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "name": "PostgreSQL"
}
```

### 14. Actualizar Tag 1 (Solo ADMIN)
```http
PUT /api/tags/1
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "Java Programming"
}
```

### 15. Actualizar Tag 2 (Solo ADMIN)
```http
PUT /api/tags/2
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "name": "Spring Boot Framework"
}
```

### 16. Eliminar Tag (Solo ADMIN)
```http
DELETE /api/tags/5
Authorization: Bearer {ADMIN_TOKEN}
```

### 17. Verificar Nombre Disponible
```http
GET /api/tags/check-name?name=nuevo-tag
```

### 18. Verificar Slug Disponible
```http
GET /api/tags/check-slug?slug=nuevo-tag
```

---

## 📝 Posts

### 1. Obtener Posts Publicados
```http
GET /api/posts?pageNo=0&pageSize=10&sortBy=createdAt&sortDir=desc
```

### 2. Obtener Todos los Posts (Solo ADMIN)
```http
GET /api/posts/all?pageNo=0&pageSize=10
Authorization: Bearer {ADMIN_TOKEN}
```

### 3. Obtener Post por ID
```http
GET /api/posts/1
```

### 4. Obtener Post por Slug
```http
GET /api/posts/slug/mi-primer-post
```

### 5. Crear Post 1 (Usuario 1)
```http
POST /api/posts
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "title": "Introducción a Spring Boot",
  "excerpt": "Una guía completa para comenzar con Spring Boot y crear aplicaciones web robustas",
  "content": "Spring Boot es un framework de Java que simplifica el desarrollo de aplicaciones web. En este artículo aprenderás los conceptos básicos, configuración inicial y mejores prácticas para crear aplicaciones robustas y escalables. Spring Boot elimina la necesidad de configuración compleja y te permite enfocarte en la lógica de negocio de tu aplicación.",
  "featuredImage": "https://ejemplo.com/spring-boot-intro.jpg",
  "published": true,
  "categoryIds": [1],
  "tagIds": [1, 2]
}
```

### 6. Crear Post 2 (Usuario 1)
```http
POST /api/posts
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "title": "Desarrollo Frontend con React",
  "excerpt": "Explorando las mejores prácticas para desarrollar aplicaciones frontend con React",
  "content": "React es una de las bibliotecas más populares para el desarrollo frontend. En este post exploraremos las mejores prácticas, patrones de diseño y herramientas modernas que te ayudarán a crear aplicaciones React escalables y mantenibles. Desde hooks personalizados hasta optimización de rendimiento.",
  "featuredImage": "https://ejemplo.com/react-development.jpg",
  "published": true,
  "categoryIds": [2],
  "tagIds": [3, 4]
}
```

### 7. Crear Post 3 (Usuario 2)
```http
POST /api/posts
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "title": "Arquitectura de Microservicios",
  "excerpt": "Diseñando sistemas escalables con arquitectura de microservicios",
  "content": "Los microservicios han revolucionado la forma en que diseñamos aplicaciones empresariales. En este artículo profundizaremos en los principios de diseño, patrones de comunicación, gestión de datos distribuidos y las mejores prácticas para implementar una arquitectura de microservicios exitosa.",
  "featuredImage": "https://ejemplo.com/microservices.jpg",
  "published": true,
  "categoryIds": [1, 2],
  "tagIds": [1, 2, 5]
}
```

### 8. Crear Post 4 - PRUEBA CON NUEVAS RELACIONES (Usuario 1)
```http
POST /api/posts
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "title": "Despliegue con Docker y Kubernetes",
  "excerpt": "Guía completa para desplegar aplicaciones usando contenedores y orquestación",
  "content": "En este artículo aprenderemos cómo desplegar aplicaciones modernas usando Docker para la containerización y Kubernetes para la orquestación. Cubriremos desde la creación de Dockerfiles hasta la configuración de deployments, servicios y configmaps en Kubernetes. También exploraremos las mejores prácticas para un despliegue exitoso en producción.",
  "featuredImage": "https://ejemplo.com/docker-kubernetes.jpg",
  "published": true,
  "categoryIds": [4, 5],
  "tagIds": [6, 7, 8]
}
```

### 9. Crear Post 5 - PRUEBA CON NUEVAS RELACIONES (Usuario 2)
```http
POST /api/posts
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "title": "Seguridad en Aplicaciones Web",
  "excerpt": "Mejores prácticas para proteger aplicaciones web contra vulnerabilidades comunes",
  "content": "La seguridad en aplicaciones web es fundamental en el desarrollo moderno. En este post exploraremos las vulnerabilidades más comunes como SQL Injection, XSS, CSRF y cómo prevenirlas. También cubriremos autenticación segura, autorización, encriptación de datos y herramientas de seguridad para mantener nuestras aplicaciones protegidas.",
  "featuredImage": "https://ejemplo.com/web-security.jpg",
  "published": true,
  "categoryIds": [6],
  "tagIds": [1, 4]
}
```

### 10. Actualizar Post 1
```http
PUT /api/posts/1
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "title": "Introducción a Spring Boot - Guía Completa",
  "excerpt": "Una guía completa y actualizada para comenzar con Spring Boot y crear aplicaciones web robustas",
  "content": "Spring Boot es un framework de Java que simplifica el desarrollo de aplicaciones web. En este artículo aprenderás los conceptos básicos, configuración inicial y mejores prácticas para crear aplicaciones robustas y escalables. Spring Boot elimina la necesidad de configuración compleja y te permite enfocarte en la lógica de negocio de tu aplicación. También cubriremos temas avanzados como testing, deployment y monitoreo.",
  "featuredImage": "https://ejemplo.com/spring-boot-complete.jpg",
  "published": true,
  "categoryIds": [1],
  "tagIds": [1, 2]
}
```

### 11. Eliminar Post
```http
DELETE /api/posts/2
Authorization: Bearer {USER1_TOKEN}
```

### 12. Obtener Posts de un Usuario
```http
GET /api/posts/user/2?pageNo=0&pageSize=10
```

### 13. Buscar Posts
```http
GET /api/posts/search?keyword=spring&pageNo=0&pageSize=10
```

### 12. Obtener Posts por Categoría
```http
GET /api/posts/category/1?pageNo=0&pageSize=10
```

### 13. Obtener Posts por Tag
```http
GET /api/posts/tag/1?pageNo=0&pageSize=10
```

### 14. Verificar Slug Disponible
```http
GET /api/posts/check-slug?slug=mi-nuevo-post
```

---
FALTA
## 💬 Comentarios

### 1. Obtener Comentarios de un Post
```http
GET /api/posts/1/comments
```

### 2. Obtener Comentarios Paginados
```http
GET /api/posts/1/comments/paginated?pageNo=0&pageSize=10
```

### 3. Crear Comentario 1 (Usuario 2 en Post 1)
```http
POST /api/posts/1/comments
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "content": "Excelente artículo! Muy bien explicado el proceso de configuración inicial de Spring Boot.",
  "postId": 1
}
```

### 4. Crear Comentario 2 (Usuario 1 en Post 3)
```http
POST /api/posts/3/comments
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "content": "Muy interesante el enfoque en la arquitectura de microservicios. ¿Podrías profundizar en los patrones de comunicación?",
  "postId": 3
}
```

### 5. Crear Comentario 3 (Usuario 2 en Post 1)
```http
POST /api/posts/1/comments
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "content": "¿Tienes algún ejemplo práctico de testing con Spring Boot? Sería muy útil.",
  "postId": 1
}
```

### 6. Responder a Comentario 1
```http
POST /api/comments/1/replies
Authorization: Bearer {USER1_TOKEN}
Content-Type: application/json

{
  "content": "¡Gracias! Me alegra que te haya sido útil. En el próximo post profundizaré en testing.",
  "postId": 1
}
```

### 7. Responder a Comentario 2
```http
POST /api/comments/2/replies
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "content": "¡Por supuesto! En el próximo artículo cubriré los patrones de comunicación como REST, gRPC y mensajería.",
  "postId": 3
}
```

### 8. Obtener Comentario por ID
```http
GET /api/comments/1
```

### 9. Obtener Respuestas de un Comentario
```http
GET /api/comments/1/replies?pageNo=0&pageSize=10
```

### 10. Actualizar Comentario 1
```http
PUT /api/comments/1
Authorization: Bearer {USER2_TOKEN}
Content-Type: application/json

{
  "content": "Excelente artículo! Muy bien explicado el proceso de configuración inicial de Spring Boot. Me ayudó mucho."
}
```

### 11. Eliminar Comentario
```http
DELETE /api/comments/3
Authorization: Bearer {USER2_TOKEN}
```

### 12. Obtener Comentarios de un Usuario
```http
GET /api/users/2/comments?pageNo=0&pageSize=10
```

### 13. Aprobar Comentario (Solo ADMIN)
```http
PUT /api/comments/1/approve
Authorization: Bearer {ADMIN_TOKEN}
```

### 14. Desaprobar Comentario (Solo ADMIN)
```http
PUT /api/comments/2/disapprove
Authorization: Bearer {ADMIN_TOKEN}
```

---

## 🔍 Verificaciones y Validaciones

### 1. Verificar Email Disponible
```http
GET /api/users/check-email?email=nuevo@email.com
```

### 2. Verificar Username Disponible
```http
GET /api/users/check-username?username=nuevoUsuario
```

### 3. Verificar Nombre de Categoría Disponible
```http
GET /api/categories/check-name?name=nueva-categoria
```

### 4. Verificar Slug de Categoría Disponible
```http
GET /api/categories/check-slug?slug=nueva-categoria
```

### 5. Verificar Nombre de Tag Disponible
```http
GET /api/tags/check-name?name=nuevo-tag
```

### 6. Verificar Slug de Tag Disponible
```http
GET /api/tags/check-slug?slug=nuevo-tag
```

### 7. Verificar Slug de Post Disponible
```http
GET /api/posts/check-slug?slug=mi-nuevo-post
```

---

## ⚠️ Códigos de Respuesta Comunes

- **200**: OK - Operación exitosa
- **201**: Created - Recurso creado exitosamente
- **400**: Bad Request - Datos inválidos
- **401**: Unauthorized - No autenticado
- **403**: Forbidden - No autorizado
- **404**: Not Found - Recurso no encontrado
- **500**: Internal Server Error - Error del servidor

---

## 🔍 Tips para Pruebas

1. **Guarda los tokens JWT** después del login para usarlos en otras peticiones
2. **Usa IDs reales** que existan en tu base de datos
3. **Prueba casos edge**: datos vacíos, IDs inexistentes, etc.
4. **Verifica permisos** intentando acceder sin autenticación
5. **Revisa los logs** del servidor para debuggear errores
6. **Sigue el flujo ordenado** para evitar dependencias faltantes

---

## 📱 Ejemplo con cURL

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameEmail":"admin","password":"admin123456"}'

# Crear categoría (con token)
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{"name":"Tecnología","description":"Artículos sobre tecnología"}'

# Crear post (con token)
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{"title":"Mi Post","excerpt":"Resumen","content":"Contenido","published":true}'
```

¡Listo para probar! 🚀 