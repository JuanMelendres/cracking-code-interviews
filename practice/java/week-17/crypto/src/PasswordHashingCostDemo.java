import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.HexFormat;

// Real demo: PBKDF2WithHmacSHA256 (JDK-native, no external library) hashing a
// password at two iteration counts, showing the deliberate cost/time tradeoff
// that makes offline brute-forcing expensive. Also proves a wrong password
// produces a different hash under the same salt+iterations.
public class PasswordHashingCostDemo {

    static byte[] hash(char[] password, byte[] salt, int iterations) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, 256);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return f.generateSecret(spec).getEncoded();
    }

    // Each iteration count runs in its own fresh JVM process (invoked separately
    // by the shell) so JIT warmup from one measurement cannot leak into another --
    // a single PBKDF2 call at login time is a cold, one-shot cost in production too.
    public static void main(String[] args) throws Exception {
        int iterations = Integer.parseInt(args[0]);
        byte[] salt = HexFormat.of().parseHex("00112233445566778899aabbccddeeff");
        char[] correct = "correct horse battery staple".toCharArray();
        char[] wrong = "correct horse battery staplf".toCharArray();

        long start = System.nanoTime();
        byte[] h = hash(correct, salt, iterations);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("iterations=%-9d time=%5dms hash=%s%n",
                iterations, elapsedMs, HexFormat.of().formatHex(h).substring(0, 32) + "...");

        byte[] hWrong = hash(wrong, salt, iterations);
        System.out.println("same salt+iterations, one-char-different password -> equal hash? "
                + java.util.Arrays.equals(h, hWrong));
    }
}
