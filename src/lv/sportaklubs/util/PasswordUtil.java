package lv.sportaklubs.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Paroļu šifrēšana ar SHA-256.
 */
public final class PasswordUtil {

    private PasswordUtil() { }

    /**
     * Aprēķina paroles SHA-256 jaucējkodu.
     *
     * @param password parole
     * @return 64 simbolu heksadecimāla virkne
     */
    public static String hash(String password) {
        try {
            byte[] b = MessageDigest.getInstance("SHA-256")
                    .digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) {
                sb.append(String.format("%02x", x));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
