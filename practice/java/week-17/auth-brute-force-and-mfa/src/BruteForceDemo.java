import java.util.List;

/**
 * Real timing-driven proof, no mocked clock:
 *   1. Against UnprotectedLoginService, an attacker working through a small
 *      wordlist reaches the correct password and logs in.
 *   2. Against LockingLoginService (5 attempts / 10s window / 3s lockout),
 *      the same attacker is locked out before ever reaching the correct
 *      password.
 *   3. The lockout's real trade-off, shown directly: while the attacker is
 *      locked out, the LEGITIMATE user is also locked out of that same
 *      username -- account lockout is itself a denial-of-service lever an
 *      attacker can pull deliberately. This is why production systems pair
 *      it with per-IP tracking, CAPTCHA, or (see TotpDemo) a second factor
 *      instead of relying on lockout alone.
 */
public class BruteForceDemo {

    private static final String REAL_PASSWORD = "Tr0ub4dor&3";

    private static final List<String> ATTACKER_WORDLIST = List.of(
            "password", "123456", "letmein", "qwerty", "admin123",
            "welcome1", "dragon99", "Tr0ub4dor&3", "sunshine", "iloveyou"
    );

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. UnprotectedLoginService: attacker works through wordlist ===");
        UnprotectedLoginService unprotected = new UnprotectedLoginService("alice", REAL_PASSWORD);
        boolean cracked = false;
        for (int i = 0; i < ATTACKER_WORDLIST.size(); i++) {
            String guess = ATTACKER_WORDLIST.get(i);
            boolean ok = unprotected.login("alice", guess);
            System.out.println("  attempt " + (i + 1) + ": \"" + guess + "\" -> " + (ok ? "SUCCESS" : "rejected"));
            if (ok) {
                cracked = true;
                break;
            }
        }
        System.out.println("  Result: " + (cracked ? "CRACKED, no rate limit stopped this." : "not cracked"));

        System.out.println();
        System.out.println("=== 2. LockingLoginService: same attacker, same wordlist, 5-attempt lockout ===");
        LockingLoginService locking = new LockingLoginService("alice", REAL_PASSWORD, 5, 10_000, 3_000);
        boolean reachedRealPassword = false;
        for (int i = 0; i < ATTACKER_WORDLIST.size(); i++) {
            String guess = ATTACKER_WORDLIST.get(i);
            LoginResult result = locking.login("alice", guess);
            System.out.println("  attempt " + (i + 1) + ": \"" + guess + "\" -> " + describe(result));
            if (guess.equals(REAL_PASSWORD)) {
                reachedRealPassword = true;
            }
            if (result.status() == LoginResult.Status.LOCKED_OUT
                    || result.status() == LoginResult.Status.JUST_LOCKED) {
                if (result.status() == LoginResult.Status.LOCKED_OUT) {
                    break;
                }
            }
        }
        System.out.println("  Result: attacker " + (reachedRealPassword ? "reached" : "never reached")
                + " the real password \"" + REAL_PASSWORD + "\" before being locked out.");

        System.out.println();
        System.out.println("=== 3. Real trade-off: the legitimate user is locked out too, right now ===");
        LoginResult legitAttempt = locking.login("alice", REAL_PASSWORD);
        System.out.println("  alice logs in with her REAL password while locked: " + describe(legitAttempt));

        System.out.println();
        System.out.println("=== Waiting out the 3s lockout window (real Thread.sleep, no mocked clock) ===");
        Thread.sleep(3_100);
        LoginResult afterWait = locking.login("alice", REAL_PASSWORD);
        System.out.println("  alice logs in again after the lockout expires: " + describe(afterWait));
    }

    private static String describe(LoginResult r) {
        return switch (r.status()) {
            case SUCCESS -> "SUCCESS";
            case FAILURE -> "rejected (" + r.attemptsRemaining() + " attempts remaining before lockout)";
            case JUST_LOCKED -> "LOCKED just now for " + r.millisRemaining() + "ms";
            case LOCKED_OUT -> "rejected, still locked for " + r.millisRemaining() + "ms";
        };
    }
}
