package com.uniquindio.saludonombre.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {


    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(KeyUtils.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
