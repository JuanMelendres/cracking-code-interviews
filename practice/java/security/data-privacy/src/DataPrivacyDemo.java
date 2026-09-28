import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

/**
 * Real, executed demonstration of the three techniques a data-privacy design
 * actually rests on: pseudonymisation, crypto-shredding (erasure by key
 * destruction), and why an unsalted hash is not anonymisation.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/DataPrivacyDemo.java
 *   java -cp out DataPrivacyDemo
 */
public class DataPrivacyDemo {

    public static void main(String[] args) throws Exception {
        section("1. Pseudonymisation: a stable reference that is useless on its own");
        pseudonymisation();

        section("2. Why an unsalted hash of an email is NOT anonymisation");
        hashIsNotAnonymous();

        section("3. Crypto-shredding: erasure by destroying the key");
        cryptoShredding();

        section("4. What each technique does and does not give you");
        summary();
    }

    // ------------------------------------------------ 1

    /** Per-subject pseudonym store. In production this is a separate, tightly-controlled system. */
    private static final Map<String, String> PSEUDONYMS = new HashMap<>();
    private static int counter = 1000;

    private static String pseudonymFor(String email) {
        return PSEUDONYMS.computeIfAbsent(email, e -> "u-" + (++counter));
    }

    private static void pseudonymisation() {
        String email = "alice@example.com";
        String ref = pseudonymFor(email);

        System.out.println("Application/event/log records carry:  user_ref=" + ref);
        System.out.println("The mapping " + ref + " -> the real identifier lives in ONE system,");
        System.out.println("with its own access control and its own audit trail.");
        System.out.println("Same subject, asked twice: " + pseudonymFor(email) + " (stable, so joins still work)");
        System.out.println();
        System.out.println("Pseudonymised data is STILL personal data under GDPR -- re-identification is");
        System.out.println("possible by design, because the mapping exists. It reduces blast radius and");
        System.out.println("narrows who can re-identify; it does not take the data out of scope.");
    }

    // ------------------------------------------------ 2

    private static String sha256(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
    }

    private static void hashIsNotAnonymous() throws Exception {
        String stored = sha256("alice@example.com");
        System.out.println("Stored 'anonymised' value: " + stored.substring(0, 32) + "...");

        String[] guesses = {"bob@example.com", "carol@example.com", "alice@example.com"};
        for (String guess : guesses) {
            boolean match = sha256(guess).equals(stored);
            System.out.printf("  guess %-22s -> %s%n", guess, match ? "MATCH -- identity recovered" : "no match");
        }
        System.out.println();
        System.out.println("The value space of email addresses is small enough to enumerate or to test");
        System.out.println("against a known customer list, so an unsalted hash is a lookup away from the");
        System.out.println("original. Hashing an identifier is PSEUDONYMISATION, not anonymisation.");
        System.out.println("A keyed hash (HMAC with a secret) raises the bar; true anonymisation means");
        System.out.println("the subject cannot be re-identified BY ANYONE, which usually means losing");
        System.out.println("row-level granularity (aggregation, k-anonymity), not transforming a column.");
    }

    // ------------------------------------------------ 3

    /** Stands in for the key-management system: one data-encryption key per subject. */
    private static final Map<String, SecretKey> KEYS = new HashMap<>();

    private static byte[] encryptFor(String subject, String plaintext) throws Exception {
        SecretKey key = KEYS.computeIfAbsent(subject, s -> {
            try {
                KeyGenerator gen = KeyGenerator.getInstance("AES");
                gen.init(256);
                return gen.generateKey();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] out = new byte[iv.length + ct.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(ct, 0, out, iv.length, ct.length);
        return out;
    }

    private static String decryptFor(String subject, byte[] blob) throws Exception {
        SecretKey key = KEYS.get(subject);
        if (key == null) {
            throw new IllegalStateException("no key for subject " + subject);
        }
        byte[] iv = new byte[12];
        System.arraycopy(blob, 0, iv, 0, 12);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(blob, 12, blob.length - 12), StandardCharsets.UTF_8);
    }

    private static void cryptoShredding() throws Exception {
        String subject = "u-1001";
        byte[] backupCopy = encryptFor(subject, "Alice Smith, 12 Rose Lane, born 1988-04-02");
        byte[] archiveCopy = encryptFor(subject, "support transcript mentioning Alice Smith");

        System.out.println("Two ciphertexts exist, in systems with different deletion capabilities:");
        System.out.println("  backup:  " + HexFormat.of().formatHex(backupCopy).substring(0, 40) + "...");
        System.out.println("  archive: " + HexFormat.of().formatHex(archiveCopy).substring(0, 40) + "...");
        System.out.println("Readable today: \"" + decryptFor(subject, backupCopy) + "\"");

        System.out.println();
        System.out.println("Erasure request received. Destroying the subject's key (NOT chasing the copies):");
        KEYS.remove(subject);

        for (String label : new String[]{"backup", "archive"}) {
            byte[] blob = label.equals("backup") ? backupCopy : archiveCopy;
            try {
                System.out.println("  " + label + " -> " + decryptFor(subject, blob));
            } catch (IllegalStateException e) {
                System.out.println("  " + label + " -> unrecoverable: " + e.getMessage());
            }
        }
        System.out.println();
        System.out.println("The ciphertext is still physically present in both systems and is now");
        System.out.println("permanently unreadable. This is how erasure is honoured in immutable");
        System.out.println("backups, append-only logs, and event stores that cannot delete a row --");
        System.out.println("the one design that makes 'delete my data' compatible with 'we keep an");
        System.out.println("immutable audit log'. Its cost: key management becomes availability-critical,");
        System.out.println("because losing a key is indistinguishable from erasing a subject.");
    }

    // ------------------------------------------------ 4

    private static void summary() {
        System.out.printf("  %-26s %-38s %s%n", "TECHNIQUE", "GIVES YOU", "STILL PERSONAL DATA?");
        System.out.printf("  %-26s %-38s %s%n", "Pseudonymisation", "smaller blast radius, joins still work", "YES");
        System.out.printf("  %-26s %-38s %s%n", "Unsalted hash", "very little -- enumerable", "YES");
        System.out.printf("  %-26s %-38s %s%n", "Keyed hash (HMAC)", "pseudonym an attacker cannot reverse", "YES");
        System.out.printf("  %-26s %-38s %s%n", "Crypto-shredding", "erasure where deletion is impossible", "until the key dies");
        System.out.printf("  %-26s %-38s %s%n", "Aggregation / k-anonymity", "genuine anonymisation", "NO (if done right)");
        System.out.println();
        System.out.println("Only the last row leaves the regulation's scope, and it costs row-level detail.");
        System.out.println("Everything above it is risk reduction, which is valuable and is not the same thing.");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
