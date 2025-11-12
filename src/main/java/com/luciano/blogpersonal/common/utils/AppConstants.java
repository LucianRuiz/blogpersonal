package com.luciano.blogpersonal.common.utils;

public class AppConstants {
    // Paginación por defecto
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    // Roles
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String SPRING_ROLE_USER = "USER";  // Para Spring Security hasRole()
    public static final String SPRING_ROLE_ADMIN = "ADMIN"; // Para Spring Security hasRole()

    // Mensajes de éxito
    public static final String USER_REGISTERED_SUCCESS = "Usuario registrado exitosamente";
    public static final String CATEGORY_DELETED_SUCCESS = "Categoría eliminada exitosamente";
    public static final String TAG_DELETED_SUCCESS = "Tag eliminado exitosamente";
    public static final String POST_DELETED_SUCCESS = "Post eliminado exitosamente";
    public static final String COMMENT_DELETED_SUCCESS = "El comentario fue eliminado exitosamente";
    public static final String USER_DELETED_SUCCESS = "Usuario eliminado exitosamente";
    public static final String PASSWORD_UPDATED_SUCCESS = "Contraseña actualizada exitosamente";

    // Mensajes de error
    public static final String INSUFFICIENT_PERMISSIONS = "Usted no tiene los permisos necesarios para realizar esta acción";
    public static final String USERNAME_EXISTS = "El nombre de usuario ya se encuentra en uso";
    public static final String EMAIL_EXISTS = "El correo electrónico ya se encuentra en uso";
    public static final String CATEGORY_NAME_EXISTS = "Ya existe una categoría con este nombre";
    public static final String CATEGORY_SLUG_EXISTS = "Ya existe un slug asignado a otra categoría";
    public static final String TAG_NAME_EXISTS = "El nombre del tag ya esta en uso";
    public static final String TAG_SLUG_EXISTS = "El slug del tag ya esta en uso";
    public static final String INVALID_CREDENTIALS = "Credenciales incorrectas";
    public static final String CATEGORY_HAS_POSTS = "No se puede eliminar una categoría con posts asociados";
    public static final String INCORRECT_PASSWORD = "La contrasena ingresada no es correcta";
    public static final String PASSWORDS_NOT_MATCH = "Las contrasenas ingresadas no son iguales";
    public static final String INVALID_PARENT_COMMENT = "El post del comentario padre es incorrecto";
    public static final String USER_NOT_ACTIVE = "El usuario no esta activo";
    public static final String TOKEN_INVALID = "Token inválido";
    public static final String TOKEN_VALID = "Token válido";
    public static final String UNAUTHENTICATED = "Usted no se ha autenticado";
    public static final String UNAUTHORIZED = "No tiene autorización para acceder a este recurso";

    // JWT
    public static final long JWT_EXPIRATION_TIME = 604800000; // 7 días en milisegundos
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String HEADER_STRING = "Authorization";

    // Rutas de archivos
    public static final String FILE_UPLOAD_DIR = "uploads/";
    public static final String PROFILE_IMAGES_DIR = "profiles/";
    public static final String POST_IMAGES_DIR = "posts/";

    // Evitar instanciación
    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
}