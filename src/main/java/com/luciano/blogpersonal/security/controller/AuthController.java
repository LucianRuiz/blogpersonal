package com.luciano.blogpersonal.security.controller;

import com.luciano.blogpersonal.common.dto.ApiResponse;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.security.dto.JwtAuthResponse;
import com.luciano.blogpersonal.security.dto.LoginRequest;
import com.luciano.blogpersonal.security.service.AuthService;
import com.luciano.blogpersonal.user.dto.UserCreateRequest;
import com.luciano.blogpersonal.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejo de autenticación
 * Proporciona endpoints para registro y login de usuarios
 */

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController (AuthService authService){
        this.authService = authService;
    }

    /**
     * Registra un nuevo usuario en el sistema
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register (@Valid @RequestBody UserCreateRequest userCreateRequest){

        authService.register(userCreateRequest);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(true)
                .message(AppConstants.USER_REGISTERED_SUCCESS)
                .build();

        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

    /**
     *Autentica un usuario y devuelve un token JWT
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login (@Valid @RequestBody LoginRequest loginRequest){

        JwtAuthResponse jwtAuthResponse = authService.login(loginRequest);

        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

    /**
     *Valida un token JWT
     * POST /api/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validateToken(@RequestParam String token){
        boolean isValid = authService.validateToken(token);
        ApiResponse apiResponse = ApiResponse.builder()
                .success(isValid)
                .message(isValid?AppConstants.TOKEN_VALID:AppConstants.TOKEN_INVALID)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
