package com.luciano.blogpersonal.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    //Llama a un valor declarado en application properties
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    //Llama a un valor declarado en application properties
    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationInMs;

    /**
     * Genera una clave segura para firmar el token
     *
     * @return Key generada a partir del secret
     */
    private SecretKey getSigninKey(){
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    /**
     * Genera un token JWT a partir del nombre de usuario
     * @param username Nombre de usuario
     * @return Token JWT generado
     */
    public String generateToken(String username){
        Date currentDate = new Date(); //Fecha de creación
        Date expiryDate = new Date(currentDate.getTime()+jwtExpirationInMs); //Fecha de expiración

        return Jwts.builder()
                .subject(username) //Lo declaramos el sujeto
                .issuedAt(new Date()) //Le establecemos la fecha de emisión
                .expiration(expiryDate) //Le establecemos la fecha de expiración
                .signWith(getSigninKey()) // Aseguramos el token con una clave secreta
                .compact();
    }

    /**
     * Extrae el nombre de usuario del token JWT
     * @param token Token JWT
     * @return Nombre de usuario extraido
     */

    public String getUsernameFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Valida un token JWT
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(getSigninKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException ex) {
            // JWT con firma invalida
            return false;
        } catch (MalformedJwtException ex){
            // JWT malformado
            return  false;
        } catch (ExpiredJwtException ex) {
            // JWT expirado
            return false;
        }catch (UnsupportedJwtException ex){
            // JWT no soportado
            return false;
        }catch (IllegalArgumentException ex){
            return false;
        }
    }
}
