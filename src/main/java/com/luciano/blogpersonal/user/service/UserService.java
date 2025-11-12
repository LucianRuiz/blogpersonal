package com.luciano.blogpersonal.user.service;

import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.user.dto.UserCreateRequest;
import com.luciano.blogpersonal.user.dto.UserPasswordUpdateRequest;
import com.luciano.blogpersonal.user.dto.UserResponse;
import com.luciano.blogpersonal.user.dto.UserUpdateRequest;
import com.luciano.blogpersonal.user.model.User;

public interface UserService {

    /**
     * Registra un nuevo usuario en el sistema
     * @param userCreateRequest Datos del usuario a crear
     * @return Respuesta con los datos del usuario creado
     */
    UserResponse createUser (UserCreateRequest userCreateRequest);

    /**
     * Obtiene un usuario por id
     * @param userId ID del usuario a buscar
     * @return Respuesta con los datos del usuario
     */
    UserResponse getUserById (Long userId);

    /**
     * Obtiene un usuario por su nombre de usuario]
     * @param username Nombre del usuario a buscar
     * @return Respuesta con los datos del usuario
     */
    UserResponse getUserByUsername(String username);

    /**
     * Obtiene el usuario que está actualmente autenticado
     * @return Respuesta con los datos del usuario
     */
    UserResponse getCurrentUser();

    /**
     * Actualiza la información del usuario
     * @param userId ID del usuario a actualizar
     * @param userUpdateRequest Datos a actualizar
     * @return Respuesta con los datos actualizados
     */
    UserResponse updateUser (Long userId, UserUpdateRequest userUpdateRequest);

    /**
     * Cambia la contrasena de un usuario
     * @param userId ID del usuario a actualizar
     * @param userPasswordUpdateRequest Contrasena a actualizar
     * @return Respuesta con los datos del uosuario
     */
    UserResponse updatePassword (Long userId, UserPasswordUpdateRequest userPasswordUpdateRequest);

    /**
     * Elimina un usuario por su id
     * @param userId ID del usuario a eliminar
     */

    void deleteUser (Long userId);

    /**
     * Obtiene un listado paginado de todos los usuarios
     * @param pageNo Número de página
     * @param pageSize Tamano de pagina
     * @param sortBy Campo por el que odenar
     * @param sortDir Dirección de ordenamiento
     * @return Respuesta paginada con usuarios
     *
     */
    PaginatedResponse<UserResponse> getAllUsers (int pageNo, int pageSize, String sortBy, String sortDir);

    /**
     * Comprueba si un email esta en uso
     * @param email Email a comprobar
     * @return true si está en uso, false en casa contrario
     */
    boolean isEmailExist (String email);

    /**
     * Comprueba si un usuario existe
     * @param username Nombre de usuario a comprobar
     * @return true si está en uso, false en caso contrario
     */
    boolean isUsernameExist (String username);

    /**
     * Obtiene un usuario por su nombre de usuario o email para autenticación
     * @param usernameOrEmail Nombre de usuario o email
     * @return Usuario encontrado
     */
    User getUserByUsernameOrEmail (String usernameOrEmail);
}
