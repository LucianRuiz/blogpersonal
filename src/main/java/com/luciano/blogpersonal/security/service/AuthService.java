package com.luciano.blogpersonal.security.service;

import com.luciano.blogpersonal.security.dto.JwtAuthResponse;
import com.luciano.blogpersonal.security.dto.LoginRequest;
import com.luciano.blogpersonal.user.dto.UserCreateRequest;
import com.luciano.blogpersonal.user.dto.UserResponse;

public interface AuthService {

    /**
     * Registra un nuevo usuario en el sistema
     * @param userCreateRequest Datos del usuario a registrar
     * @return Respuesta con información del usuario registrado
     */
    UserResponse register (UserCreateRequest userCreateRequest);

    /**
     * Auténtica a un usuario en el sistema
     * @param loginRequest Credenciales de inicio de sesión
     * @return Respuesta con el token JWT y datos del usuario
     */
    JwtAuthResponse login (LoginRequest loginRequest);

    /**
     * Valida un token JWT
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean validateToken(String token);

    /**
     * Obtiene nombre de usuario del token JWT
     * @param token TokenJWT
     * @return Nombre de usuario extraído del token
     */
    String getUsernameFromToken(String token);


    /**
     * Genera un nuevo token JWT para un usuario
     * @param username Nombre de usuario
     * @return token JWT generado
     */
    String generateToken(String username);
}
