package Util;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SenhaUtil {
    private static final int ITERACOES = 120000;

    // Formato salvo no banco: iteracoes:salt:hash (use VARCHAR(255) em usuario.senha)
    public static String gerarHash(String senha) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(senha, salt, ITERACOES);
        return ITERACOES + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String senha, String armazenado) {
        String[] p = armazenado.split(":");
        if (p.length != 3) return false;
        byte[] salt = Base64.getDecoder().decode(p[1]);
        byte[] esperado = Base64.getDecoder().decode(p[2]);
        byte[] calc = pbkdf2(senha, salt, Integer.parseInt(p[0]));
        return java.security.MessageDigest.isEqual(esperado, calc);
    }

    private static byte[] pbkdf2(String senha, byte[] salt, int it) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, it, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
