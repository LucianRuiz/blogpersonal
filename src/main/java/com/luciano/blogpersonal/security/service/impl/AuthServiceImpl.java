package com.luciano.blogpersonal.security.service.impl;

import com.luciano.blogpersonal.common.exception.BlogApiException;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.security.dto.JwtAuthResponse;
import com.luciano.blogpersonal.security.dto.LoginRequest;
import com.luciano.blogpersonal.security.jwt.JwtTokenProvider;
import com.luciano.blogpersonal.security.service.AuthService;
import com.luciano.blogpersonal.user.dto.UserCreateRequest;
import com.luciano.blogpersonal.user.dto.UserResponse;
import com.luciano.blogpersonal.user.model.User;
import com.luciano.blogpersonal.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserService userService, JwtTokenProvider tokenProvider
            , AuthenticationManager authenticationManager){

        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public UserResponse register (UserCreateRequest userCreateRequest){

        if (userService.isUsernameExist(userCreateRequest.getUsername())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.USERNAME_EXISTS);
        }

        if (userService.isEmailExist(userCreateRequest.getEmail())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.EMAIL_EXISTS);
        }

        return userService.createUser(userCreateRequest);
    }

    @Override
    @Transactional
    public JwtAuthResponse login(LoginRequest loginRequest) {
        //Autenticar usuario
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameEmail(),
                            loginRequest.getPassword()
                    )
            );

            //Informarle al sistema que el usuario ya está autenticado
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Obtiene los detalles del usuario en el contexto de autenticación
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            //Obtenemos detalles del usuario
            User user = userService.getUserByUsernameOrEmail(userDetails.getUsername());

            //Generar Token
            String token = tokenProvider.generateToken(userDetails.getUsername());

            return JwtAuthResponse.builder()
                    .accessToken(token)
                    .tokenType(AppConstants.TOKEN_PREFIX)
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();

        } catch (Exception e) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.INVALID_CREDENTIALS);
        }

    }

    @Override
    @Transactional
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    @Override
    @Transactional
    public String getUsernameFromToken(String token) {
        return tokenProvider.getUsernameFromToken(token);
    }

    @Override
    @Transactional
    public String generateToken(String username) {

        return tokenProvider.generateToken(username);
    }
}
