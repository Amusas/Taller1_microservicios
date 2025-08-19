package com.uniquindio.saludonombre.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Slf4j
public class KeyUtils {

    @Getter
    private static RSAPublicKey publicKey;

    public KeyUtils() {
        initializeKeys();
    }

    private void initializeKeys() {
        try {
            String pubPath = System.getenv("PUBLIC_KEY_PATH");
            if (pubPath == null) {
                throw new IllegalStateException("❌ La variable de entorno PUBLIC_KEY_PATH no está definida");
            }

            Path publicKeyPath = Paths.get(pubPath);
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyPath);
            publicKey = readPublicKey(publicKeyBytes);

            log.info("✅ Clave pública cargada desde {}", publicKeyPath.toAbsolutePath());
        } catch (Exception e) {
            log.error("❌ Error cargando la clave pública", e);
            throw new RuntimeException("Error cargando las keys", e);
        }
    }

    private RSAPublicKey readPublicKey(byte[] keyBytes) throws Exception {
        String publicKeyPEM = new String(keyBytes)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
    }
}
