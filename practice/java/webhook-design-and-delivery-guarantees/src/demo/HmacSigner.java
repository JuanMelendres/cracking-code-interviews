package demo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/** Real HMAC-SHA256 over the raw request body -- the actual mechanism
 * webhook providers (Stripe, GitHub, etc.) use so a receiver can prove a
 * payload came from the real sender and wasn't modified in transit or
 * forged by a third party who doesn't know the shared secret. */
public class HmacSigner {

    public static String sign(byte[] payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
