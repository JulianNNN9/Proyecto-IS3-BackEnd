package co.edu.uniquindio.laos.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;


@Component
public class JWTUtils {


    public String generarToken(String email, Map<String, Object> claims){

        Instant now = Instant.now();

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(1L, ChronoUnit.HOURS)))
                .signWith( getKey() )
                .compact();
    }

    public Jws<Claims> parseJwt(String jwtString) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {
        JwtParser jwtParser = Jwts.parser().verifyWith( getKey() ).build();
        return jwtParser.parseSignedClaims(jwtString);
    }
    // Refresca un token JWT expirado generando uno nuevo con los mismos claims
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

    private SecretKey getKey(){
        String claveSecreta = "SoSecretKey";
        byte[] secretKeyBytes = claveSecreta.getBytes();
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }


}

