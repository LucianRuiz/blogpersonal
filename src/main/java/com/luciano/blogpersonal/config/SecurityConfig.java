package com.luciano.blogpersonal.config;

import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.security.jwt.JwtAuthenticationEntryPoint;
import com.luciano.blogpersonal.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Encriptador de Contraseñas
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Verifica que las credenciales sean correctas
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authconfig) throws Exception {
        return authconfig.getAuthenticationManager();
    }

    // Configuración de filtros de seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // Desactiva CSRF ya que JWT no trabaja con sesiones
                .csrf(AbstractHttpConfigurer::disable)
                // Configuración de CORS
                .cors(withDefaults())
                // Asigna a JwtAuthenticationEntryPoint como responsable para las excepciones de autenticación
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(unauthorizedHandler))
                // Administra la gestión de estados poniéndolo en sin estado, ya que no trabajamos con ellos
                .sessionManagement(sessionManagement->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Administra los permisos sobre las solicitudes HTTP
                .authorizeHttpRequests(auth -> auth
                        // ===============================
                        // ENDPOINTS PÚBLICOS
                        // ===============================

                        // Autenticación - completamente público
                        .requestMatchers("/api/auth/**").permitAll()

                        // Swagger/OpenAPI - público
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // ===============================
                        // POSTS - Mayormente público para lectura
                        // ===============================
                        .requestMatchers(HttpMethod.GET,
                                "/api/posts",
                                "/api/posts/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()

                        // ===============================
                        // CATEGORÍAS - Lectura pública, escritura admin
                        // ===============================
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole(AppConstants.SPRING_ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole(AppConstants.SPRING_ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole(AppConstants.SPRING_ROLE_ADMIN)

                        // ===============================
                        // TAGS - Lectura pública, creación autenticada, gestión admin
                        // ===============================
                        .requestMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/tags/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/tags/**").hasRole(AppConstants.SPRING_ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/tags/**").hasRole(AppConstants.SPRING_ROLE_ADMIN)

                        // ===============================
                        // USUARIOS - Configuración granular
                        // ===============================

                        // Endpoints públicos de usuarios
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/check-email",
                                "/api/users/check-username"
                        ).permitAll()

                        // Ver perfil público de usuarios
                        .requestMatchers(HttpMethod.GET,
                                "/api/users/username/{username}",     // Perfil por username
                                "/api/users/{id:[0-9]+}"         // Perfil por ID (regex para números)
                        ).permitAll()

                        // Perfil propio del usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()

                        // Actualización de perfil propio
                        .requestMatchers(HttpMethod.PUT,
                                "/api/users/*/password"     // Cambio de contraseña
                        ).authenticated()

                        // Actualización de perfil (se valida ownership en el servicio)
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").authenticated()

                        // Administración de usuarios - solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole(AppConstants.SPRING_ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole(AppConstants.SPRING_ROLE_ADMIN)

                        // ===============================
                        // COMENTARIOS - Requieren autenticación
                        // ===============================
                        .requestMatchers(HttpMethod.GET,
                                "/api/posts/*/comments/**",
                                "/api/comments/*/replies",
                                "/api/users/*/comments"
                        ).permitAll()  // Lectura pública de comentarios

                        .requestMatchers(
                                "/api/posts/*/comments",     // Crear comentario
                                "/api/comments/*/replies",   // Crear respuesta
                                "/api/comments/*"            // Gestionar comentarios
                        ).authenticated()

                        // Moderación de comentarios - solo ADMIN
                        .requestMatchers(
                                "/api/comments/*/approve",
                                "/api/comments/*/disapprove"
                        ).hasRole(AppConstants.SPRING_ROLE_ADMIN)

                        // ===============================
                        // RECURSOS ESTÁTICOS
                        // ===============================
                        .requestMatchers(
                                "/uploads/**",
                                "/static/**",
                                "/images/**",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        // ===============================
                        // TODO LO DEMÁS REQUIERE AUTENTICACIÓN
                        // ===============================
                        .anyRequest().authenticated()
                );

        // Implementar el filtro jwtAuthenticationFilter antes del filtro para verificar credenciales
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}