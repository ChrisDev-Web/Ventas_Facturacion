package Config;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {
    }

    public static String hashPassword(char[] password) {
        try {
            byte[] salt = generateSalt();
            byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

            return "pbkdf2$"
                    + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (GeneralSecurityException e) {
            throw new RuntimeException("No se pudo encriptar la contraseña.", e);
        }
    }

    public static boolean verifyPassword(char[] password, String storedPassword) {
        try {
            if (storedPassword == null || !storedPassword.startsWith("pbkdf2$")) {
                return false;
            }

            String[] parts = storedPassword.split("\\$");

            if (parts.length != 4) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] storedHash = Base64.getDecoder().decode(parts[3]);

            byte[] calculatedHash = pbkdf2(password, salt, iterations, storedHash.length * 8);

            return MessageDigest.isEqual(storedHash, calculatedHash);

        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws GeneralSecurityException {

        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        try {
            return factory.generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}