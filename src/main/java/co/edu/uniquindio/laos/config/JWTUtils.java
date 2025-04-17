package co.edu.uniquindio.laos.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * Utilidad para la gestión de tokens JWT (JSON Web Tokens).
 * Esta clase proporciona métodos para crear, validar y refrescar tokens JWT
 * utilizados en la autenticación y autorización de la aplicación.
 */
@Component
public class JWTUtils {

    /**
     * Genera un nuevo token JWT para un usuario identificado por su email.
     * @param email El correo electrónico del usuario (subject del token)
     * @param claims Información adicional a incluir en el payload del token
     * @return Token JWT firmado con validez de 1 hora
     */
    public String generarToken(String email, Map<String, Object> claims){
        Instant now = Instant.now();

        return Jwts.builder()
                .claims(claims)        // Añade los claims personalizados (rol, etc.)
                .subject(email)        // Establece el subject (email del usuario)
                .issuedAt(Date.from(now))  // Fecha de emisión
                .expiration(Date.from(now.plus(1L, ChronoUnit.HOURS)))  // Expiración: 1 hora
                .signWith(getKey())    // Firma con la clave secreta
                .compact();            // Genera el token en formato compacto
    }

    /**
     * Analiza y valida un token JWT.
     * @param jwtString El token JWT en formato String
     * @return El token parseado con sus claims si es válido
     * @throws ExpiredJwtException Si el token ha expirado
     * @throws UnsupportedJwtException, MalformedJwtException Si el token tiene formato incorrecto
     */
    public Jws<Claims> parseJwt(String jwtString) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {
        JwtParser jwtParser = Jwts.parser().verifyWith(getKey()).build();
        return jwtParser.parseSignedClaims(jwtString);
    }

    /**
     * Refresca un token JWT expirado generando uno nuevo con los mismos claims.
     * Este método permite renovar un token vencido sin necesidad de reautenticar al usuario.
     * @param expiredToken El token JWT expirado
     * @return Nuevo token JWT con los mismos datos pero nueva fecha de expiración, o null si el token es inválido
     */
    public String refreshToken(String expiredToken) {
        try {
            // Intenta parsear el token, permitiendo que esté expirado
            Jws<Claims> claims = parseJwt(expiredToken);

            // Si el token no ha expirado, usa los mismos claims para generar un nuevo token
            String email = claims.getPayload().getSubject();
            Map<String, Object> currentClaims = claims.getPayload();
            return generarToken(email, currentClaims);

        } catch (ExpiredJwtException e) {
            // Si el token ha expirado, recupera los claims desde la excepción
            Claims expiredClaims = e.getClaims();
            String email = expiredClaims.getSubject();
            return generarToken(email, expiredClaims); // Genera un nuevo token usando los claims anteriores

        } catch (JwtException e) {
            // Si el token es inválido, retorna null
            return null;
        }
    }

    /**
     * Obtiene la clave secreta usada para firmar y verificar tokens.
     * @return Clave secreta para operaciones criptográficas de JWT
     */
    private SecretKey getKey(){
        String claveSecreta = "secretsecretsecretsecretsecretsecretsecretsecret";
        byte[] secretKeyBytes = claveSecreta.getBytes();
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }
}