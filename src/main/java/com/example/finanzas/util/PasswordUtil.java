package com.example.finanzas.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/** Hash de contraseñas con PBKDF2-HMAC-SHA256 y sal aleatoria. */
public final class PasswordUtil {
    private PasswordUtil() {}

    public static String nuevaSal() {
        byte[] b = new byte[16];
        new SecureRandom().nextBytes(b);
        return Base64.getEncoder().encodeToString(b);
    }

    public static String hash(String password, String salBase64) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salBase64), 65536, 256);
            byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(h);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
