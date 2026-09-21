import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Password check plus a mandatory TOTP second factor. The point this proves:
 * a correct password alone -- exactly what a credential-stuffing attacker
 * has, reused from an unrelated breach -- is not sufficient here, because
 * the attacker doesn't have the shared TOTP secret.
 */
public class MfaProtectedLoginService {

    private final String username;
    private final byte[] correctPasswordHash;
    private final byte[] totpSecret;
    private static final int TOTP_DIGITS = 6;

    public MfaProtectedLoginService(String username, String correctPassword, byte[] totpSecret) {
        this.username = username;
        this.correctPasswordHash = sha256(correctPassword);
        this.totpSecret = totpSecret;
    }

    public boolean login(String attemptUsername, String attemptPassword, String totpCode) {
        boolean passwordOk = username.equals(attemptUsername)
                && Arrays.equals(sha256(attemptPassword), correctPasswordHash);
        if (!passwordOk) {
            return false;
        }
        return Totp.verify(totpSecret, System.currentTimeMillis() / 1000, TOTP_DIGITS, totpCode);
    }

    private static byte[] sha256(String s) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
