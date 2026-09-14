import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * A real, minimal JWT: HMAC-SHA256 signing and verification using only
 * javax.crypto (no external library) -- real signature bytes, real
 * tamper detection, real expiry check. Demonstrates, concretely, why a
 * JWT cannot be revoked before its expiry: verification is a pure,
 * stateless cryptographic check against the token's own bytes, with no
 * database lookup involved at all.
 */
public class JwtDemo {

    static final String SECRET = "this-is-a-demo-secret-not-for-production-use";

    public static void main(String[] args) throws Exception {
        System.out.println("=== 1. Issue a token, verify it succeeds ===");
        String token = issue("user-42", Instant.now().plusSeconds(3600));
        System.out.println("Token: " + token);
        System.out.println("Verification: " + verify(token));

        System.out.println("\n=== 2. Tamper with the payload, verify it's rejected ===");
        String[] parts = token.split("\\.");
        String tamperedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"user-999-PRIVILEGE-ESCALATED\"}".getBytes(StandardCharsets.UTF_8));
        String tamperedToken = parts[0] + "." + tamperedPayload + "." + parts[2]; // signature NOT recomputed
        System.out.println("Tampered token: " + tamperedToken);
        System.out.println("Verification: " + verify(tamperedToken));

        System.out.println("\n=== 3. An expired token is rejected, even with a correct signature ===");
        String expiredToken = issue("user-42", Instant.now().minusSeconds(10));
        System.out.println("Verification: " + verify(expiredToken));

        System.out.println("\n=== 4. Why a valid, non-expired JWT cannot be revoked ===");
        System.out.println("Verification is PURELY a signature + expiry check against the token's own");
        System.out.println("bytes -- no database or session store is consulted at all. A token issued");
        System.out.println("one second ago, even if the user's account is deleted or compromised THIS");
        System.out.println("instant, will still verify successfully until it naturally expires:");
        String stillValidEvenIfUserWasJustDeleted = issue("user-to-be-deleted", Instant.now().plusSeconds(3600));
        System.out.println("Verification: " + verify(stillValidEvenIfUserWasJustDeleted)
                + "  <-- still VALID; nothing about deleting the user changes this token's bytes");
        System.out.println("\nReal mitigations (this demo does not implement them, naming them honestly instead):");
        System.out.println("  - Short expiry (minutes, not hours) + a refresh-token flow, so the exposure window is bounded");
        System.out.println("  - A server-side deny-list checked at verification time (reintroduces a stateful lookup,");
        System.out.println("    trading away the whole point of a stateless token for revocability)");
    }

    static String issue(String subject, Instant expiresAt) throws Exception {
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64Url("{\"sub\":\"" + subject + "\",\"exp\":" + expiresAt.getEpochSecond() + "}");
        String signature = sign(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    static String verify(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) return "INVALID (malformed)";
        try {
            String expectedSignature = sign(parts[0] + "." + parts[1]);
            if (!constantTimeEquals(expectedSignature, parts[2])) {
                return "INVALID (signature mismatch -- token was tampered with)";
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            long exp = Long.parseLong(payloadJson.replaceAll(".*\"exp\":(\\d+).*", "$1"));
            if (Instant.now().getEpochSecond() > exp) {
                return "INVALID (expired)";
            }
            return "VALID";
        } catch (Exception e) {
            return "INVALID (" + e.getMessage() + ")";
        }
    }

    private static String sign(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
    }

    private static String base64Url(String s) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static boolean constantTimeEquals(String a, String b) {
        // Real JWT libraries compare signatures in constant time specifically
        // to avoid a timing side-channel leaking how many leading bytes matched.
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) result |= a.charAt(i) ^ b.charAt(i);
        return result == 0;
    }
}
