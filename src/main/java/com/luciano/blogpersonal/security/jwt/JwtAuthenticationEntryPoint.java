package com.luciano.blogpersonal.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luciano.blogpersonal.common.dto.ApiResponse;
import com.luciano.blogpersonal.common.utils.AppConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 *Intercepta los tokens que no son válidos y cuando alguien intenta acceder a un recurso sin autenticarse
 *
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()); //Se pone para que el ObjectMapper pueda manejar LocalDateTime
    

    public void commence (HttpServletRequest request,
                          HttpServletResponse response,
                          AuthenticationException exception) throws IOException, ServletException{


        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .message(AppConstants.UNAUTHENTICATED + exception.getMessage())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }


}