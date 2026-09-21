import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * No rate limiting, no lockout, no attempt tracking at all. Every call is
 * independent -- an attacker can try passwords as fast as the JVM can loop.
 */
public class UnprotectedLoginService {

    private final String username;
    private final byte[] correctPasswordHash;

    public UnprotectedLoginService(String username, String correctPassword) {
        this.username = username;
        this.correctPasswordHash = sha256(correctPassword);
    }

    public boolean login(String attemptUsername, String attemptPassword) {
        return username.equals(attemptUsername)
                && Arrays.equals(sha256(attemptPassword), correctPasswordHash);
    }

    private static byte[] sha256(String s) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
