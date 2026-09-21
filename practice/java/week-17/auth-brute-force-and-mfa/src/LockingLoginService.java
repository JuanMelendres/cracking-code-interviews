import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Per-username sliding-window failed-attempt counter with a real time-based
 * lockout. State is tracked per attempted username, not per correct account
 * only -- this is deliberate: an attacker probing a username that doesn't
 * exist still consumes an attempt slot for that username, so the service
 * doesn't leak "this username exists" through lockout-vs-not-lockout timing.
 */
public class LockingLoginService {

    private static final class Attempts {
        int failedCount;
        long windowStartMillis;
        long lockedUntilMillis;
    }

    private final String username;
    private final byte[] correctPasswordHash;
    private final int maxAttempts;
    private final long windowMillis;
    private final long lockoutMillis;
    private final Map<String, Attempts> attemptsByUsername = new HashMap<>();

    public LockingLoginService(String username, String correctPassword,
                                int maxAttempts, long windowMillis, long lockoutMillis) {
        this.username = username;
        this.correctPasswordHash = sha256(correctPassword);
        this.maxAttempts = maxAttempts;
        this.windowMillis = windowMillis;
        this.lockoutMillis = lockoutMillis;
    }

    public synchronized LoginResult login(String attemptUsername, String attemptPassword) {
        long now = System.currentTimeMillis();
        Attempts a = attemptsByUsername.computeIfAbsent(attemptUsername, k -> new Attempts());

        if (now < a.lockedUntilMillis) {
            return LoginResult.lockedOut(a.lockedUntilMillis - now);
        }

        if (now - a.windowStartMillis > windowMillis) {
            a.windowStartMillis = now;
            a.failedCount = 0;
        }

        boolean correct = username.equals(attemptUsername)
                && Arrays.equals(sha256(attemptPassword), correctPasswordHash);

        if (correct) {
            a.failedCount = 0;
            return LoginResult.success();
        }

        a.failedCount++;
        if (a.failedCount >= maxAttempts) {
            a.lockedUntilMillis = now + lockoutMillis;
            return LoginResult.justLocked(lockoutMillis);
        }
        return LoginResult.failure(maxAttempts - a.failedCount);
    }

    private static byte[] sha256(String s) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
