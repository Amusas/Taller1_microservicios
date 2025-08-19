package com.uniquindio.saludonombre.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniquindio.saludonombre.dto.ErrorResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("Public key hash: {}", KeyUtils.getPublicKey().hashCode());

        if ("/saludo".equals(path)) {
            if (!authenticateRequest(request, response)) {
                return; // ya se escribió el error
            }
        }

        filterChain.doFilter(request, response);
    }

    // ================== helpers ==================

    /** Valida header/token, verifica issuer y subject, y si va bien, AUTENTICA en el contexto. */
    private boolean authenticateRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String path = request.getRequestURI();
        final String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return writeError(response, new ErrorResponse(ts(), 401, "Unauthorized",
                    "Falta token JWT o formato inválido (se espera 'Bearer <token>')", path));
        }

        final String token = header.substring(7);
        final String nombreParam = request.getParameter("nombre");

        try {
            Claims claims = jwtUtils.validateToken(token);

            if (!isIssuerValid(claims)) {
                return writeError(response, new ErrorResponse(ts(), 403, "Forbidden",
                        "Emisor inválido", path));
            }

            if (!isSubjectValid(claims, nombreParam)) {
                return writeError(response, new ErrorResponse(ts(), 403, "Forbidden",
                        "El parámetro 'nombre' no coincide con el subject del token", path));
            }

            // ✅ Si llegamos aquí: token válido → setear Authentication en el contexto
            setAuthentication(claims);

            return true;

        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
            return writeError(response, new ErrorResponse(ts(), 401, "Unauthorized",
                    "Token expirado", path));
        } catch (SignatureException e) {
            log.warn("Firma inválida del token: {}", e.getMessage());
            return writeError(response, new ErrorResponse(ts(), 401, "Unauthorized",
                    "Firma del token inválida", path));
        } catch (JwtException e) {
            log.warn("Token inválido o mal formado: {}", e.getMessage());
            return writeError(response, new ErrorResponse(ts(), 401, "Unauthorized",
                    "Token inválido o mal formado", path));
        } catch (Exception e) {
            log.error("Error inesperado al validar token", e);
            return writeError(response, new ErrorResponse(ts(), 500, "InternalServerError",
                    "Error al validar token", path));
        }
    }

    private boolean isIssuerValid(Claims claims) {
        return "ingesis.uniquindio.edu.co".equals(claims.getIssuer());
    }

    private boolean isSubjectValid(Claims claims, String nombreParam) {
        return Objects.equals(claims.getSubject(), nombreParam);
    }

    /** Construye el Authentication y lo coloca en el SecurityContext. */
    private void setAuthentication(Claims claims) {
        String username = claims.getSubject();

        // Como no hay roles, pasamos una lista vacía — pero usamos el constructor con authorities
        Collection<SimpleGrantedAuthority> authorities = Collections.emptyList();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);

        log.info("Usuario autenticado en contexto: {} — Authentication: {}",
                username, SecurityContextHolder.getContext().getAuthentication());
    }



    private boolean writeError(HttpServletResponse response, ErrorResponse body) throws IOException {
        if (response.isCommitted()) return false;
        response.setStatus(body.status());
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
        return false;
    }

    private static String ts() { return Instant.now().toString(); }

}


