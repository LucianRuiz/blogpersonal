package com.luciano.blogpersonal.security.jwt;

import com.luciano.blogpersonal.common.utils.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     *
     * @param request Representa la solicitud HTTP entrante del cliente con el token JWT del encabezado Authorization
     * @param response Representa la respuesta HTTP que se enviará al cliente
     * @param filterChain Representa a los demás filtros por los que tendrá que pasar la solicitud
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Obtener el token JWT del encabezado Authorization
        String token = getJwtFromRequest(request);

        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)){

            //Obtener el nombre de usuario del token
            String username = tokenProvider.getUsernameFromToken(token);
            //Cargar el usuario desde la base de datos
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            //Crear la autenticación mandando los detalles del usuario, el rol del usuario y la contrasena en null porque el usuario ya está autenticado.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            //Almacena detalles de la autenticación como el ip y entre otras cosas en el objeto WebAuthenticationDetailSource, el cual se almacena en el SecurityContext.
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //Declaramos que este usuario ya esta autenticado para toda la aplicación
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        //Seguimos con los demás filtros
        filterChain.doFilter(request,response);

    }

    //Obtener el token desde la solicitud HTTP
    private String getJwtFromRequest(HttpServletRequest request) {
        //Obtiene el token desde el header
        String bearerToken = request.getHeader(AppConstants.HEADER_STRING);

        //Si el token existe y empieza con el prefijo bearer se eliminara 7 caracteres incluyendo el espacio.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AppConstants.TOKEN_PREFIX + " ")) {
            return bearerToken.substring(7);
        }
        //En caso no cumpla con el if se devuelve null
        return null;
    }
}
