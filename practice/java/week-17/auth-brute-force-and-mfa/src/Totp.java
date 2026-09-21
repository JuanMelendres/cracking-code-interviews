import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * RFC 6238 (TOTP) over RFC 4226 (HOTP), HMAC-SHA1 mode only -- the mode
 * RFC 6238 Appendix B publishes official test vectors for, used directly in
 * TotpDemo to prove this implementation is correct against the standard,
 * not just internally self-consistent.
 */
public final class Totp {

    private static final long STEP_SECONDS = 30;

    private Totp() {
    }

    public static String generate(byte[] key, long unixTimeSeconds, int digits) {
        long counter = unixTimeSeconds / STEP_SECONDS;
        return hotp(key, counter, digits);
    }

    public static boolean verify(byte[] key, long unixTimeSeconds, int digits, String code) {
        return generate(key, unixTimeSeconds, digits).equals(code);
    }

    private static String hotp(byte[] key, long counter, int digits) {
        byte[] msg = new byte[8];
        long c = counter;
        for (int i = 7; i >= 0; i--) {
            msg[i] = (byte) (c & 0xff);
            c >>= 8;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(msg);

            int offset = hash[hash.length - 1] & 0x0f;
            int binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);

            int otp = (int) (binary % Math.pow(10, digits));
            return String.format("%0" + digits + "d", otp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
