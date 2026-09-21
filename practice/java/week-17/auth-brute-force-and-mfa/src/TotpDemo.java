import java.nio.charset.StandardCharsets;

/**
 * Part 1: verifies this Totp implementation against RFC 6238 Appendix B's
 * own published test vectors -- official reference, not a self-consistency
 * check.
 *
 * Part 2: a credential-stuffing scenario. "Attacker" has alice's REAL
 * password, exactly as if it leaked in an unrelated breach and alice reused
 * it here. Password alone is not enough against MfaProtectedLoginService.
 */
public class TotpDemo {

    // RFC 6238 Appendix B: Secret = ASCII "12345678901234567890" for the SHA1 mode,
    // used directly as HMAC key bytes (not hex-decoded). Test vectors use 8 digits.
    private static final byte[] RFC_TEST_SECRET =
            "12345678901234567890".getBytes(StandardCharsets.US_ASCII);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. Totp verified against RFC 6238 Appendix B official test vectors (SHA1, 8 digits) ===");
        long[] times = {59L, 1111111109L, 1111111111L, 1234567890L, 2000000000L};
        String[] expected = {"94287082", "07081804", "14050471", "89005924", "69279037"};
        boolean allPassed = true;
        for (int i = 0; i < times.length; i++) {
            String actual = Totp.generate(RFC_TEST_SECRET, times[i], 8);
            boolean pass = actual.equals(expected[i]);
            allPassed &= pass;
            System.out.println("  T=" + times[i] + "  expected=" + expected[i] + "  actual=" + actual
                    + "  -> " + (pass ? "PASS" : "FAIL"));
        }
        System.out.println("  All RFC 6238 vectors: " + (allPassed ? "PASS" : "FAIL"));

        System.out.println();
        System.out.println("=== 2. Credential stuffing: attacker has alice's REAL password, no TOTP secret ===");
        byte[] aliceTotpSecret = "a-real-32-byte-shared-totp-secret-key".getBytes(StandardCharsets.UTF_8);
        MfaProtectedLoginService mfaService = new MfaProtectedLoginService("alice", "Tr0ub4dor&3", aliceTotpSecret);

        boolean attackerNoCode = mfaService.login("alice", "Tr0ub4dor&3", "000000");
        System.out.println("  attacker: correct password, guessed code \"000000\" -> "
                + (attackerNoCode ? "SUCCESS (bad)" : "rejected"));

        String attackerGuess = String.valueOf((int) (Math.random() * 1_000_000));
        attackerGuess = "0".repeat(6 - attackerGuess.length()) + attackerGuess;
        boolean attackerRandomCode = mfaService.login("alice", "Tr0ub4dor&3", attackerGuess);
        System.out.println("  attacker: correct password, random code \"" + attackerGuess + "\" -> "
                + (attackerRandomCode ? "SUCCESS (bad)" : "rejected"));

        String realCode = Totp.generate(aliceTotpSecret, System.currentTimeMillis() / 1000, 6);
        boolean legitLogin = mfaService.login("alice", "Tr0ub4dor&3", realCode);
        System.out.println("  alice: correct password, real current TOTP code \"" + realCode + "\" -> "
                + (legitLogin ? "SUCCESS" : "rejected (bad)"));
    }
}
