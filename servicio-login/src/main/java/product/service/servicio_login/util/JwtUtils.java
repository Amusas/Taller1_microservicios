package product.service.servicio_login.util;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import product.service.servicio_login.dto.LoginRequest;

import java.time.Instant;
import java.util.Date;

/**
 * Utilidad para la generación y validación de tokens JWT.
 * <p>
 * Esta clase se encarga de generar tokens JWT firmados, extrayendo los datos relevantes del usuario
 * (como username, userId y roles) y configurando la expiración. Además, proporciona métodos para parsear
 * y validar tokens.
 * </p>
 */
@Component
@Slf4j
public class JwtUtils {

    private static final long EXPIRATION_TIME = 3600000; // 1 hora en milisegundos
    // Tiempo de expiración del refresh token: 30 días (en milisegundos)

    /**
     * Genera un token JWT para el usuario proporcionado.
     *
     * @param user Usuario para el cual se genera el token.
     * @return Token JWT generado.
     */
    public String generateToken(LoginRequest user) {
        log.debug("Generando token JWT para el usuario: {}", user.name());
        log.info("Private key hash: {}", KeyUtils.getPrivateKey().hashCode());
        Instant now = getCurrentInstant();
        Instant expiration = calculateExpiration(now);
        String token = buildJwtToken(user, now, expiration);
        log.info("Token JWT generado para el usuario {} (expira a las {})", user.name(), expiration);
        return token;
    }


    /**
     * Obtiene el instante actual.
     *
     * @return Instant actual.
     */
    private Instant getCurrentInstant() {
        return Instant.now();
    }

    /**
     * Calcula la fecha de expiración del token basándose en el instante actual y el tiempo de expiración configurado.
     *
     * @param now Instante actual.
     * @return Instante en el que expira el token.
     */
    private Instant calculateExpiration(Instant now) {
        return now.plusMillis(EXPIRATION_TIME);
    }

    /**
     * Construye y firma el token JWT utilizando los datos proporcionados, el instante de emisión y expiración.
     *
     * @param issuedAt  Instante en el que se emite el token.
     * @param expiration Instante en el que expira el token.
     * @return Token JWT firmado.
     */
    private String buildJwtToken(LoginRequest user, Instant issuedAt, Instant expiration) {
        return Jwts.builder()
                .subject(user.name())
                .issuedAt(Date.from(issuedAt))
                .claim("iss", "ingesis.uniquindio.edu.co")
                .expiration(Date.from(expiration))
                .signWith(KeyUtils.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

}
