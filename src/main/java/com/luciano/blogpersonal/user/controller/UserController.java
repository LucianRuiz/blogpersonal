package com.luciano.blogpersonal.user.controller;

import com.luciano.blogpersonal.common.dto.ApiResponse;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.user.dto.UserPasswordUpdateRequest;
import com.luciano.blogpersonal.user.dto.UserResponse;
import com.luciano.blogpersonal.user.dto.UserUpdateRequest;
import com.luciano.blogpersonal.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejo de usuarios
 * Proporciona endpoints para gestión de usuarios del sistema
 */

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final UserService userService;

    @Autowired
    public UserController (UserService userService){
        this.userService = userService;
    }

    /**
     * Obtiene todos los usuarios (solo ADMIN)
     * GET /api/users
     */
    @GetMapping
    @PreAuthorize("hasRole ('ADMIN')")
    public ResponseEntity<PaginatedResponse<UserResponse>> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir)
    {
    PaginatedResponse<UserResponse> users = userService.getAllUsers(pageNo, pageSize, sortBy, sortDir );
    return ResponseEntity.ok(users);
    }

    /**
     * Obtiene un usuario por su ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById (@PathVariable Long id){
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene el usuario actualmente autenticado
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser (){
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene un usuario por su nombre de usuario
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername (@PathVariable String username){
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Actualiza la información de un usuario
     * PUT /api/users/{id}
     */

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser (@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest){
        UserResponse user = userService.updateUser(id, userUpdateRequest);
        return ResponseEntity.ok(user);
    }

    /**
     * Cambia la contraseña de un usuario
     * PUT /api/users/{id}/password
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse> updatePassword (@PathVariable Long id, @Valid @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest){
        userService.updatePassword(id, userPasswordUpdateRequest);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Contraseña actualizada exitosamente")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina un usuario (solo ADMIN)
     * DELETE /api/users/{id}
     */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser (@PathVariable Long id){

        userService.deleteUser(id);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Usuario eliminado exitosamente")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si un email está en uso
     * GET /api/users/check-email
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmailExists (@RequestParam String email){
        boolean isActive = userService.isEmailExist(email);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(isActive)
                .message(isActive?"Email ya en uso":"Email disponible")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Verifica si un nombre de usuario está en uso
     * GET /api/users/check-username
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse> checkUsernameExists (@RequestParam String username){
        boolean isActive = userService.isUsernameExist(username);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(isActive)
                .message(isActive?"Username ya en uso":"Nombre de usuario disponible")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
